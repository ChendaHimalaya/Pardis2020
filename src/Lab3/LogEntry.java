package Lab3;

class LogEntry implements Comparable<LogEntry> {
    long threadId;
    String info;
    long timeStamp;

    LogEntry(long threadId, String info, long timeStamp) {
        this.threadId = threadId;
        this.info = info;
        this.timeStamp = timeStamp;
    }

    @Override
    public int compareTo(LogEntry o) {
        return Long.compare(this.timeStamp, o.timeStamp);
    }
}
