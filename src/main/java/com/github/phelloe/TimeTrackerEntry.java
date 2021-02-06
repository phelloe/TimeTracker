package com.github.phelloe;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class TimeTrackerEntry {
    private final LocalDateTime start;
    private final LocalDateTime end;
    private final long delta;

    public TimeTrackerEntry(LocalDateTime startTime, LocalDateTime endTime) {
        this.start = startTime;
        this.end = endTime;
        this.delta = endTime.toEpochSecond(ZoneOffset.UTC) - startTime.toEpochSecond(ZoneOffset.UTC);
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

    public String getDeltaString() {
        return String.format("%.2f", delta/3600.0);
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

    public long getDelta() {
        return delta;
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
