package models;

import java.io.Serializable;
import java.time.LocalDateTime;

public class AlgoritmoResult implements Serializable {
    private final String algorithmName;
    private final int pathLength;
    private final long executionTimeNanos;

    private final LocalDateTime timestamp;

    public AlgoritmoResult(String algorithmName, int pathLength, long executionTimeNanos) {
        this.algorithmName = algorithmName;
        this.pathLength = pathLength;
        this.executionTimeNanos = executionTimeNanos;
        this.timestamp = LocalDateTime.now();
    }

    public String getAlgorithmName() {
        return algorithmName;
    }
    public long getExecutionTimeNanos() {
        return executionTimeNanos;
    }

    public int getPathLength() {
        return pathLength;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return algorithmName + "," + pathLength + "," + executionTimeNanos + "," + timestamp;
    }
}
