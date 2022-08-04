package de.hsh.grappa.service;

/**
 * Immutable read only class.
 * <p>
 * This class provides data about how many
 * of the grading processes (i.e. student submissions)
 * have been submitted in total, and how many have
 * succeeded, failed, timed out, or have been manually
 * cancelled by the client.
 */
public class GraderStatistics {
    private long executed = 0;
    private long succeeded = 0;
    private long failed = 0;
    private long timedOut = 0;
    private long cancelled = 0;

    public GraderStatistics() {
    }

    public GraderStatistics(long executed, long succeeded, long failed, long timedOut, long cancelled) {
        this.executed = executed;
        this.succeeded = succeeded;
        this.failed = failed;
        this.timedOut = timedOut;
        this.cancelled = cancelled;
    }

    public GraderStatistics add(GraderStatistics other) {
        return new GraderStatistics(
            this.executed + other.executed,
            this.succeeded + other.succeeded,
            this.failed + other.failed,
            this.timedOut + other.timedOut,
            this.cancelled + other.cancelled
        );
    }

    public long getExecuted() {
        return executed;
    }

    public long getSucceeded() {
        return succeeded;
    }

    public long getFailed() {
        return failed;
    }

    public long getTimedOut() {
        return timedOut;
    }

    public long getCancelled() {
        return cancelled;
    }

    @Override
    public String toString() {
        return "GraderStatistics{" +
            "executed=" + executed +
            ", succeeded=" + succeeded +
            ", failed=" + failed +
            ", timedOut=" + timedOut +
            ", cancelled=" + cancelled +
            '}';
    }
}
