package de.hsh.grappa.cache;

import de.hsh.grappa.config.CacheConfig;
import de.hsh.grappa.exceptions.NotFoundException;
import de.hsh.grappa.proforma.ResponseResource;
import de.hsh.grappa.proforma.SubmissionResource;
import de.hsh.grappa.proforma.TaskResource;
import de.hsh.grappa.utils.StringByteCodec;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.SetArgs;
import io.netty.util.internal.StringUtil;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.IntStream;

public class RedisController {
    private static final Logger log = LoggerFactory.getLogger(RedisController.class);

    private static RedisController instance = new RedisController();
    private RedisClient redisClient = null;
    private CacheConfig cacheConfig;

    private RedisController() {
    }

    public static RedisController getInstance() {
        return instance;
    }

    public synchronized void init(CacheConfig cc) {
        this.cacheConfig = cc;
        var redisURI =
            RedisURI.Builder.redis(cacheConfig.getRedis().getHost(),
                cacheConfig.getRedis().getPort()).
                withPassword(cacheConfig.getRedis().getPassword()).build();
        redisURI.setVerifyPeer(false);
        log.info("Setting up redis connection with URI '{}'...", redisURI.toString());
        //ClientResources sharedRedis = DefaultClientResources.create();
        redisClient = RedisClient.create(/*sharedRedis,*/ redisURI);
    }

    public synchronized void shutdown() {
        if (null != redisClient)
            redisClient.shutdown();
    }

    public synchronized boolean ping() {
        try (var redis = redisClient.connect()) {
            String pong = redis.sync().ping();
            log.debug("PING... {}", pong);
            return pong.equals("PONG");
        } catch (Exception e) {
            log.error(e.getMessage());
            log.error(ExceptionUtils.getStackTrace(e));
        }
        return false;
    }

    /**
     * This is the list's name where submission's gradeProcIds to be
     * processed are queued. Each graderId has its own submission queue,
     * so the actual submission queue looks like this:
     * submission-queue:graderId
     */
    private static final String SUBMISSION_QUEUE_PREFIX = "submission-queue:";

    /**
     * Maps a gradeProcId to a graderId so the list for a garder submission queue can be
     * inferred.
     * <p>
     * Key: gradeprocid-to-graderid-map:gradeProcId
     */
    private static final String GRADEPROCID_TO_GRADERID_MAP = "gradeprocid-to-graderid-map:";

    private static final String GRADEPROCID_TO_TASKUUID_MAP = "gradeprocid-to-taskuuid-map:";

    /**
     * When a grader worker pops a submission gradeProcId from "submission-queue", the
     * gradeProcId must be put into the "processing" list so that submissions to be
     * processed do not get lost when a grader worker fails processing it.
     * This also means that when grappa starts up, it needs to move (failed) submissions
     * from "processing" back to "submission-queue".
     * <p>
     * E.g. processing:graderId
     */
    private static final String SUBMISSION_PROCESSING_LIST_PREFIX = "processing:";

    /**
     * This is the prefix for submission keys (gradeProcId) used for storing
     * submission byte arrays.
     * <p>
     * E.g. submission:gradeProcId
     */
    private static final String SUBMISSION_KEY_PREFIX = "submission:";

    /**
     * This is the prefix for task keys (taskUuid) used for storing
     * submission byte arrays.
     * <p>
     * E.g. task:taskUuid
     */
    private static final String TASK_KEY_PREFIX = "task:";

    private static final String TASK_AVG_GRADING_DURATION_SECONDS_KEY_PREFIX = "avg-grading-seconds:task:" +
        ":task:";

    /**
     * This is the prefix for response keys (gradeProcId) used for storing
     * response byte arrays.
     * <p>
     * E.g. response:gradeProcId
     */
    private static final String RESPONSE_KEY_PREFIX = "response:";

    /***
     * Returns the number of currently queued submissions for a given grader.
     * @param graderId
     * @return
     */
    public synchronized long getSubmissionQueueCount(String graderId) {
        // TODO: validate graderId
        // don't spam this: log.debug("[GraderId: '{}']: getSubmissionQueueCount()", graderId);
        try (var redis = redisClient.connect()) {
            return redis.sync().llen(SUBMISSION_QUEUE_PREFIX.concat(graderId));
        }
    }

    /***
     * Queues a new submission for a given grader, with a specified grader process id.
     * @param graderId
     * @param gradeProcId
     * @param submissionBlob
     */
    public synchronized void pushSubmission(String graderId, String gradeProcId,
                                            String taskUuid, SubmissionResource submissionBlob,
                                            boolean prioritize) {
        log.debug("[GraderId: '{}', GradeProcId: '{}']: pushSubmission(): {}", graderId, gradeProcId,
            submissionBlob);
        // cache the submission data
        String submKey = SUBMISSION_KEY_PREFIX.concat(gradeProcId);
        set(submKey, SerializationUtils.serialize(submissionBlob),
            cacheConfig.getSubmission_ttl_seconds());
        setTimestamp(submKey, cacheConfig.getSubmission_ttl_seconds());
        mapGraderProcIdToGraderId(gradeProcId, graderId);
        mapGraderProcIdToTaskUuid(gradeProcId, taskUuid);
        // push the graderProcId onto the queue
        try (var redis = redisClient.connect()) {
            long listSize;
            if (prioritize)
                listSize = redis.sync().lpush(SUBMISSION_QUEUE_PREFIX.concat(graderId), gradeProcId);
            else
                listSize = redis.sync().rpush(SUBMISSION_QUEUE_PREFIX.concat(graderId), gradeProcId);
            log.debug("[GraderId: '{}', GradeProcId: '{}']: new queue size: {}", graderId, gradeProcId,
                listSize);
        }
    }

    private synchronized void validateGraderProcId(String gradeProcId) throws NotFoundException {
        // If no graderId is mapped to this gradeProcId, then this
        // gradeProcId has never been created for a submission.
        String graderId = getAssociatedGraderId(gradeProcId);
//        if(null == graderId)
//            throw new NotFoundException(String.format("GradeProcId '%s' does not exist.", gradeProcId));
    }

    public synchronized boolean isSubmissionQueued(String gradeProcId) throws NotFoundException {
        log.debug("[GradeProcId: '{}']: isSubmissionQueued() called.", gradeProcId);
        validateGraderProcId(gradeProcId);
        String graderId = getAssociatedGraderId(gradeProcId);
        try (var redis = redisClient.connect()) {
            List<String> graderQueue = redis.sync().lrange(SUBMISSION_QUEUE_PREFIX.concat(graderId), 0, -1);
            int index = IntStream.range(0, graderQueue.size())
                .filter(i -> gradeProcId.equals(graderQueue.get(i)))
                .findFirst().orElse(-1);
            return -1 != index;
        }
    }

    /**
     * Get the index position of a submission in a queue.
     *
     * @param gradeProcId
     * @return -1, if the submission is not queued (anymore), or a positive number.
     * 0, if the submission is literally up next for grading.
     * @throws NotFoundException
     */
    public synchronized int getQueuedSubmissionIndex(String gradeProcId) throws NotFoundException {
        log.debug("[GradeProcId: '{}']: getSubmissionQueueIndex() called.", gradeProcId);
        validateGraderProcId(gradeProcId);
        String graderId = getAssociatedGraderId(gradeProcId);
        try (var redis = redisClient.connect()) {
            List<String> graderQueue = redis.sync().lrange(SUBMISSION_QUEUE_PREFIX.concat(graderId), 0, -1);
            return IntStream.range(0, graderQueue.size())
                .filter(i -> gradeProcId.equals(graderQueue.get(i)))
                .findFirst().orElse(-1);
        }
    }

    // TODO: removeAllQueuedSubmissions()

    /**
     * Cancels a queued or a currently being graded submission.
     *
     * @param gradeProcId
     * @return true, if the submission was cancelled (i.e. removed from cache and possibly cancelled mid-grading)
     */
    public synchronized boolean removeSubmission(String gradeProcId) {
        log.debug("[GradeProcId: '{}']: removeSubmission() called.", gradeProcId);
        try (var redis = redisClient.connect()) {
            String graderId = redis.sync().get(GRADEPROCID_TO_GRADERID_MAP.concat(gradeProcId));
            if (null != graderId) {
                long remCount = redis.sync().lrem(SUBMISSION_QUEUE_PREFIX.concat(graderId), 1, gradeProcId);
                assert remCount <= 1 : "Removed more than one occurrance of the same gardeProcId in a grader queue";
                if (1 == remCount) {
                    log.debug("[GradeProcId: '{}']: removeSubmission(): Submission removed from queue.", gradeProcId);
                    return true;
                } else {
                    log.debug("[GradeProcId: '{}']: removeSubmission(): Nothing to remove.", gradeProcId);
                }
            } else {
                log.debug("[GradeProcId: '{}']: Cannot procced with removing the submission, no information about the " +
                    "associated graderId is available.", gradeProcId);
            }
            return false;
        }
    }

    /***
     * Pops a submission gradeProcId from the queue (FIFO) of a spcified grader.
     *
     * The corresponding submission object is not automatically removed from the cache
     * when a gradeProcId is popped from the queue. It will be removed passively when
     * its TTL expires.
     * @param graderId
     * @return A QueuedSubmission object
     * @throws NotFoundException if a corresponding submission object for the gradeProcId does not exist (likely due
     * to TTL expiration)
     */
    public synchronized QueuedSubmission popSubmission(String graderId) throws NotFoundException {
        log.debug("[GraderId: '{}']: popSubmission()", graderId);
        String gradeProcId = null;
        try (var redis = redisClient.connect()) {
            gradeProcId = redis.sync().lpop(SUBMISSION_QUEUE_PREFIX.concat(graderId));
            //log.debug("Popped submission for grader '{}' with gradeProcId '{}'.",
            //        graderId, gradeProcId);
        }
        if (null != gradeProcId) {
            String key = SUBMISSION_KEY_PREFIX.concat(gradeProcId);
            byte[] subm = getByteArray(key);
            // No need to remove the cached submission object. It will be removed
            // when its TTL expires.

            if (null == subm) {
                // The submission object for this garderProcId likely expired
                throw new NotFoundException(String.format
                    ("The submission for graderProcId '%s' does not exist.",
                        gradeProcId));
            }

            return new QueuedSubmission(gradeProcId, SerializationUtils.deserialize(subm));
        }
        return null; // submission queue is empty
    }

//    /***
//     * Sets the state of a submission from 'queued' to 'processing'. Internally, the
//     * grade process id is moved from the queued stack to the processing one.
//     * @param graderId
//     * @param gradeProcId
//     */
//    public synchronized void setSubmissionProcessing(String graderId, String gradeProcId) {
//        try (var redis = redisClient.connect()) {
//            redis.sync().lpush(SUBMISSION_PROCESSING_LIST_PREFIX.concat(graderId), gradeProcId);
//        }
//    }
//
//    public synchronized void unsetSubmissionProcessing(String graderId, String gradeProcId) {
//        try (var redis = redisClient.connect()) {
//            long remCount = redis.sync().lrem(SUBMISSION_PROCESSING_LIST_PREFIX.concat(graderId), 1, gradeProcId);
//            assert remCount == 1 : "Tried to remove gradeProcId '" + gradeProcId + "' from processing list; there was nothing to be removed.";
//        }
//    }

    public synchronized void setResponse(String gradeProcId, ResponseResource resp) {
        log.debug("[GradeProcId: '{}']: setResponse(): {}", gradeProcId, resp);
        String respKey = RESPONSE_KEY_PREFIX.concat(gradeProcId);
        set(respKey, SerializationUtils.serialize(resp), cacheConfig.getResponse_ttl_seconds());
        log.debug("Response with gradeProcId '{}' set.", gradeProcId);
        setTimestamp(respKey, cacheConfig.getResponse_ttl_seconds());
    }

    public synchronized ResponseResource getResponse(String gradeProcId) {
        log.debug("[GradeProcId: '{}']: getResponse()", gradeProcId);
        try (var redis = redisClient.connect(new StringByteCodec())) {
            byte[] respBytes = redis.sync().get(RESPONSE_KEY_PREFIX.concat(gradeProcId));
            if (null == respBytes)
                return null;
            return SerializationUtils.deserialize(respBytes);
        }
    }

    public synchronized long getSubmissionQueueSize(String graderId) {
        log.debug("[GraderId: '{}']: getSubmissionQueueSize()", graderId);
        try (var redis = redisClient.connect(new StringByteCodec())) {
            return redis.sync().llen(SUBMISSION_QUEUE_PREFIX.concat(graderId));
        }
    }

    public synchronized boolean isTaskCached(String taskUuid) {
        return keyExists(TASK_KEY_PREFIX.concat(taskUuid));
    }

    public synchronized void cacheTask(String taskUuid, TaskResource task) {
        log.debug("[TaskUuid: '{}']: cacheTask(): {}", taskUuid, task);
        String taskKey = TASK_KEY_PREFIX.concat(taskUuid);
        set(taskKey, SerializationUtils.serialize(task), cacheConfig.getTask_ttl_seconds());
        setTimestamp(taskKey, cacheConfig.getTask_ttl_seconds());
    }

    public synchronized TaskResource getCachedTask(String taskUuid) throws NotFoundException {
        log.debug("[TaskUuid: '{}']: getCachedTask()", taskUuid);
        try (var redis = redisClient.connect(new StringByteCodec())) {
            byte[] taskBytes = redis.sync().get(TASK_KEY_PREFIX.concat(taskUuid));
            if (null == taskBytes)
                throw new NotFoundException(String.format("Task with uuid '%s' is not cached", taskUuid));
            return SerializationUtils.deserialize(taskBytes);
        }
    }

    public synchronized void refreshTaskTimeout(String taskUuid) {
        log.debug("[TaskUuid: '{}']: refreshing timeout for task", taskUuid);
        try (var redis = redisClient.connect()) {
            String prefixedKey = TASK_KEY_PREFIX.concat(taskUuid);
            if (!redis.sync().expire(prefixedKey,
                cacheConfig.getTask_ttl_seconds())) {
                // Setting the timeout may fail if the key already expired.
                log.error("Setting timeout for key '{}' failed.", prefixedKey);
            }
        }
    }

    /**
     * @param graderProcId
     * @return the associated gradeId for the graderProcId, or null if the graderProcId does not exist
     */
    public String getAssociatedGraderId(String graderProcId) throws NotFoundException {
        String id = this.getString(GRADEPROCID_TO_GRADERID_MAP.concat(graderProcId));
        if (null != id)
            return id;
        throw new NotFoundException(String.format("No associated graderId exists for gradeProcId '%s'.",
            graderProcId));
    }

    private void mapGraderProcIdToGraderId(String gradeProcId, String graderId) {
        set(GRADEPROCID_TO_GRADERID_MAP.concat(gradeProcId), graderId,
            cacheConfig.getSubmission_ttl_seconds());
    }

    public String getAssociatedTaskUuid(String graderProcId) throws NotFoundException {
        String id = this.getString(GRADEPROCID_TO_TASKUUID_MAP.concat(graderProcId));
        if (null != id)
            return id;
        throw new NotFoundException(String.format("No associated taskUuid exists for gradeProcId '%s'.",
            graderProcId));
    }

    private void mapGraderProcIdToTaskUuid(String gradeProcId, String taskUuid) {
        set(GRADEPROCID_TO_TASKUUID_MAP.concat(gradeProcId), taskUuid,
            cacheConfig.getSubmission_ttl_seconds());
    }

    public synchronized void setTaskAverageGradingDurationSeconds(String taskUuid, long seconds) {
        set(TASK_AVG_GRADING_DURATION_SECONDS_KEY_PREFIX.concat(taskUuid), String.valueOf(seconds),
            cacheConfig.getTask_ttl_seconds());
    }

    public synchronized long getSubmissionAverageGradingDurationSeconds(String gradeProcId, long defaultSeconds) throws NotFoundException {
        String taskUuid = getAssociatedTaskUuid(gradeProcId);
        return getTaskAverageGradingDurationSeconds(taskUuid, defaultSeconds);
    }

    public synchronized long getTaskAverageGradingDurationSeconds(String taskUuid, long defaultValue) {
        String s = getString(TASK_AVG_GRADING_DURATION_SECONDS_KEY_PREFIX.concat(taskUuid));
        return !StringUtil.isNullOrEmpty(s) ? Long.parseLong(s) : defaultValue;
    }

    private synchronized boolean keyExists(String key) {
        try (var redis = redisClient.connect()) {
            return 1 == redis.sync().exists(key);
        }
    }

    // Don't use UTC time zone
    //private static final TimeZone tz = TimeZone.getTimeZone("UTC");
    //private static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
    // Use localized time
    private static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");

    /**
     * Set a ISO 8601 timestamp for a key of another key/value pair chache entry.
     */
    private synchronized void setTimestamp(String key, long timeoutSeconds) {
        set("timestamp:".concat(key), df.format(new Date()), timeoutSeconds);
    }

    /**
     * Sets a persistent key/value pair.
     *
     * @param key
     * @param value
     */
    private synchronized void set(String key, byte[] value) {
        try (var redis = redisClient.connect(new StringByteCodec())) {
            redis.sync().set(key, value);
        }
    }

    private synchronized void set(String key, byte[] value, long timeoutSeconds) {
        try (var redis = redisClient.connect(new StringByteCodec())) {
            SetArgs sa = new SetArgs();
            sa.ex(timeoutSeconds);
            redis.sync().set(key, value, sa);
        }
    }

    /**
     * Sets a persistent key/value pair.
     *
     * @param key
     * @param value
     */
    private synchronized void set(String key, String value) {
        try (var redis = redisClient.connect()) {
            redis.sync().set(key, value);
        }
    }

    private synchronized void set(String key, String value, long timeoutSeconds) {
        try (var redis = redisClient.connect()) {
            SetArgs sa = new SetArgs();
            sa.ex(timeoutSeconds);
            redis.sync().set(key, value, sa);
        }
    }

    private synchronized void delete(String key) {
        try (var redis = redisClient.connect()) {
            redis.sync().del(key);
        }
    }

    private synchronized byte[] getByteArray(String key) {
        try (var redis = redisClient.connect(new StringByteCodec())) {
            return redis.sync().get(key);
        }
    }

    private synchronized String getString(String key) {
        try (var redis = redisClient.connect()) {
            return redis.sync().get(key);
        }
    }

//    private static <T> T createProformaObject(Class<T> clazz, ProformaObject obj) throws RuntimeException {
//        try {
//            Constructor<T> ctor = clazz.getConstructor(new Class[]{byte[].class, MimeType.class});
//            return ctor.newInstance(obj.getContent(), obj.getMimeType());//shallow copy
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
}
