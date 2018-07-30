package de.infinit.forge;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import lombok.Getter;
import lombok.extern.apachecommons.CommonsLog;

@CommonsLog
public class NormalityDescriptor {
    final String regex = "\\[(.*)]\\s\\[(.*)]\\s\\[(.*)]\\s\\[(.*)]\\s(.+)";
    final Pattern pattern = Pattern.compile(regex);

    @Getter
    class LogEntry {
        LocalDateTime logDateTime;
        String logLevel;

        public LogEntry(LocalDateTime logDateTime, String logLevel) {
            this.logDateTime = logDateTime;
            this.logLevel = logLevel;
        }
    }

    private Optional<LogEntry> getLogEntry(String line) {
        Matcher matches = pattern.matcher(line);
        if (matches.find()) {
            LocalDateTime timestamp = LocalDateTime.parse(matches.group(1), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            String[] group = matches.group(2).split(":");
            String level = (group.length == 2) ? group[1] : "<unknown>";
            return Optional.of(new LogEntry(timestamp, level));
        }
        return Optional.empty();
    }

    private List<Long> find(Map<String, List<Long>> samples, String level) {
        List<Long> list = samples.get(level);
        if (list == null) {
            list = new ArrayList<>();
            samples.put(level, list);
        }
        return list;
    }

    /**
     * 
     */
    public Map<String, List<Long>> getSamples(String inputFile, long samplingRate, String logLevels) {
        Map<String, List<Long>> samples = new HashMap<>();
        File file = new File(getClass().getClassLoader().getResource(inputFile).getFile());
        try (Scanner scanner = new Scanner(file)) {
            // get first log entry to get start date/time
            Optional<LogEntry> firstLogEntry = Optional.empty();
            if (scanner.hasNextLine()) {
                String logLine = scanner.nextLine();
                firstLogEntry = getLogEntry(logLine);
                if (!firstLogEntry.isPresent()) {
                    log.error("missing first log line");
                    return Collections.emptyMap();
                }
            }
            String level = firstLogEntry.get().getLogLevel();
            long count = level.equals(logLevels) ? 1 : 0;
            LocalDateTime currentLogDateTime = firstLogEntry.get().getLogDateTime();
            LocalDateTime nextSlotLogDateTime = currentLogDateTime.plusSeconds(samplingRate);
            while (scanner.hasNextLine()) {
                String logLine = scanner.nextLine();
                Optional<LogEntry> logEntry = getLogEntry(logLine);
                if (logEntry.isPresent()) {
                    level = logEntry.get().getLogLevel();
                    currentLogDateTime = logEntry.get().getLogDateTime();
                    if (currentLogDateTime.isBefore(nextSlotLogDateTime)) {
                        if (level.equals(logLevels)) {
                            count = count + 1;
                        }
                    } else {
                        // next slot starts
                        List<Long> samplesOfLevel = find(samples, level);
                        samplesOfLevel.add(count);
                        count = level.equals(logLevels) ? 1 : 0;
                        nextSlotLogDateTime = nextSlotLogDateTime.plusSeconds(samplingRate);
                    }
                }
            }
            List<Long> levelSamples = find(samples, level);
            levelSamples.add(count);
        } catch (FileNotFoundException e) {
            log.error(e);
            return Collections.emptyMap();
        }
        return samples;
    }

    /**
     * 
     */
    public boolean getStreamSamples(String input) {
        try {
            Path path = Paths.get(getClass().getClassLoader().getResource(input).toURI());
            try (Stream<String> stream = Files.lines(path)) {
                // @formatter:off
                stream
                    // .parallel()
                    .map(this::getLogEntry)
                    .filter(Optional::isPresent)
                    .map(Optional::get);
                // @formatter:on
            }
        } catch (URISyntaxException | IOException e) {
            log.error(e);
            return false;
        }
        return true;
    }
}
