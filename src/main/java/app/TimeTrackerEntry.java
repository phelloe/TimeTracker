package app;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class TimeTrackerEntry {
    private final LocalDateTime start;
    private final LocalDateTime end;

    public TimeTrackerEntry(LocalDateTime startTime, LocalDateTime endTime) {
        this.start = startTime;
        this.end = endTime;
    }

    public String getDateString() {
        return start.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    public String getStartTimeString() {
        return start.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    public String getEndTimeString() {
        return end.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    public long getStartTime() {
        return start.toEpochSecond(ZoneOffset.UTC);
    }

    public long getEndTime() {
        return end.toEpochSecond(ZoneOffset.UTC);
    }

    public long getDate() {
        return start.toEpochSecond(ZoneOffset.UTC);
    }

    @Override
    public String toString() {
        return getDateString() +
                ", " + getStartTimeString() +
                " - " + getEndTimeString();
    }

    public String toCSVString() {
        return getDateString() + "," + getStartTimeString() + "," + getEndTimeString();
    }
}
