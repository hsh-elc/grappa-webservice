package de.hsh.grappa.config;

public class CacheConfig {
    private long submission_ttl_seconds;
    private long task_ttl_seconds;
    private long response_ttl_seconds;
    private RedisConfig redis;

    public long getSubmission_ttl_seconds() {
        return submission_ttl_seconds;
    }

    public void setSubmission_ttl_seconds(long submission_ttl_seconds) {
        this.submission_ttl_seconds = submission_ttl_seconds;
    }

    public long getTask_ttl_seconds() {
        return task_ttl_seconds;
    }

    public void setTask_ttl_seconds(long task_ttl_seconds) {
        this.task_ttl_seconds = task_ttl_seconds;
    }

    public long getResponse_ttl_seconds() {
        return response_ttl_seconds;
    }

    public void setResponse_ttl_seconds(long response_ttl_seconds) {
        this.response_ttl_seconds = response_ttl_seconds;
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
            "submission_timeout=" + submission_ttl_seconds +
            ", task_timeout=" + task_ttl_seconds +
            ", response_timeout=" + response_ttl_seconds +
            ", redis=" + redis +
            '}';
    }
}
