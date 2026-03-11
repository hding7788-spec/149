package ext.casc.util;

public class SnowflakeIdGenerator {

    private final long workerId;
    private long sequence = 0L;
    private final long maxWorkerId = 9L; // 最大工作节点ID，这里设置为9以确保生成的ID不超过12位
    private final long sequenceBits = 2L;
    private final long workerIdShift = sequenceBits;
    private final long timestampLeftShift = sequenceBits + 4L; // 4位时间戳
    private final long sequenceMask = -1L ^ (-1L << sequenceBits);
    private long lastTimestamp = -1L;

    public SnowflakeIdGenerator(long workerId) {
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("Worker ID must be between 0 and %d", maxWorkerId));
        }
        this.workerId = workerId;
    }

    public synchronized long generateId() {
        long timestamp = System.currentTimeMillis();

        if (timestamp < lastTimestamp) {
            throw new RuntimeException("Clock moved backwards. Refusing to generate id");
        }

        if (timestamp == lastTimestamp) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence > sequenceMask) {
                timestamp = tilNextMillis(lastTimestamp);
                sequence = 0L;
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        return ((timestamp % 10000) << timestampLeftShift) |
                (workerId << workerIdShift) |
                sequence;
    }

    private long tilNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }

    public static void main(String[] args) {
        SnowflakeIdGenerator idGenerator = new SnowflakeIdGenerator(1); // Replace 1 with your worker ID
        for (int i = 0; i < 10; i++) {
            long id = idGenerator.generateId();
            System.out.println(String.format("%012d", id)); // Format as 12 digits
        }
    }
}