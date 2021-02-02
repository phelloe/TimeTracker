package app;

import com.sun.jna.platform.win32.Advapi32Util;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class LogParser {
    protected static Map<Integer, TimeTrackerEntry> extracted(LocalDateTime startDate) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(
                new Advapi32Util.EventLogIterator("Application"), Spliterator.IMMUTABLE), true)
                .map(elem -> LocalDateTime.ofEpochSecond(elem.getRecord().TimeGenerated.longValue(), 0, ZoneOffset.ofTotalSeconds(0)))
                .filter(startDate::isBefore)
                .collect(Collectors.groupingBy(LocalDateTime::getDayOfYear,
                        Collectors.teeing(
                                Collectors.minBy(LocalDateTime::compareTo),
                                Collectors.maxBy(LocalDateTime::compareTo),
                                (min, max) -> new TimeTrackerEntry(min.orElse(LocalDateTime.now()), max.orElse(LocalDateTime.now())))));
    }
}
