package de.hsh.grappa.config;

public class CacheConfig {
    private long submission_timeout;
    private long task_timeout;
    private long response_timeout;
    private RedisConfig redis;

    public long getSubmission_timeout() {
        return submission_timeout;
    }

    public void setSubmission_timeout(long submission_timeout) {
        this.submission_timeout = submission_timeout;
    }

    public long getTask_timeout() {
        return task_timeout;
    }

    public void setTask_timeout(long task_timeout) {
        this.task_timeout = task_timeout;
    }

    public long getResponse_timeout() {
        return response_timeout;
    }

    public void setResponse_timeout(long response_timeout) {
        this.response_timeout = response_timeout;
    }

    public RedisConfig getRedis() {
        return redis;
    }

    public void setRedis(RedisConfig redis) {
        this.redis = redis;
    }

    @Override
    public String toString() {
        return "Cache{" +
            "submission_timeout=" + submission_timeout +
            ", task_timeout=" + task_timeout +
            ", response_timeout=" + response_timeout +
            ", redis=" + redis +
            '}';
    }
}
