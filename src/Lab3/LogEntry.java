package Lab3;

class LogEntry<T> implements Comparable<LogEntry<T>> {
    long threadId;
    String op;
    T val;
    boolean ret;
    long timeStamp;

    LogEntry(long threadId, String op, T val, boolean ret, long timeStamp) {
        this.threadId = threadId;
        this.op = op;
        this.val = val;
        this.ret = ret;
        this.timeStamp = timeStamp;
    }

    @Override
    public int compareTo(LogEntry o) {
        return Long.compare(this.timeStamp, o.timeStamp);
    }

    @Override
    public String toString() {
        return "LogEntry{" +
                "threadId=" + threadId +
                ", op='" + op + '\'' +
                ", val=" + val +
                ", ret=" + ret +
                ", timeStamp=" + timeStamp +
                '}';
    }
}
