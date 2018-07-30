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
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.apache.commons.math3.transform.FastFourierTransformer;

import lombok.Getter;
import lombok.extern.apachecommons.CommonsLog;

@CommonsLog
public class NormalityDescriptor {
    final String regex = "\\[(.*)]\\s\\[(.*)]\\s\\[(.*)]\\s\\[(.*)]\\s(.+)";
    final Pattern pattern = Pattern.compile(regex);

    @Getter
    class Entry {
        LocalDateTime timestamp;
        String logLevel;

        public Entry(LocalDateTime timestamp, String logLevel) {
            this.timestamp = timestamp;
            this.logLevel = logLevel;
        }
    }

    @Getter
    class Probe {
        LocalDateTime timestamp;
        String logLevel;
        long count;

        public Probe(LocalDateTime timestamp, String logLevel, Long count) {
            this.timestamp = timestamp;
            this.logLevel = logLevel;
            this.count = count;
        }
    }

    private Optional<Entry> getSample(String line) {
        Matcher matches = pattern.matcher(line);
        if (matches.find()) {
            LocalDateTime timestamp = LocalDateTime.parse(matches.group(1), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            String[] group = matches.group(2).split(":");
            String level = (group.length == 2) ? group[1] : "<unknown>";
            return Optional.of(new Entry(timestamp, level));
        }
        return Optional.empty();
    }

    /**
     * 
     */
    public List<Probe> getProbes(String input, long slotWidth, String logLevel) {
        List<Probe> probes = new ArrayList<>();
        File file = new File(getClass().getClassLoader().getResource(input).getFile());
        try (Scanner scanner = new Scanner(file)) {
            Optional<Entry> first = Optional.empty();
            if (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                first = getSample(line);
                if (!first.isPresent()) {
                    log.error("missing first log line");
                    return Collections.emptyList();
                }
            }
            long count = first.get().getLogLevel().equals(logLevel) ? 1 : 0;
            LocalDateTime curr = first.get().getTimestamp();
            LocalDateTime next = curr.plusSeconds(slotWidth);
            while (scanner.hasNextLine()) {
                LocalDateTime prev = curr;
                String line = scanner.nextLine();
                Optional<Entry> sample = getSample(line);
                if (sample.isPresent()) {
                    curr = sample.get().getTimestamp();
                    String level = sample.get().getLogLevel();
                    if (curr.isBefore(next)) {
                        if (level.equals(logLevel)) {
                            count = count + 1;
                        }
                    } else {
                        probes.add(new Probe(prev, logLevel, count));
                        count = level.equals(logLevel) ? 1 : 0;
                        next = next.plusSeconds(slotWidth);
                    }
                }
            }
            probes.add(new Probe(curr, logLevel, count));
        } catch (FileNotFoundException e) {
            log.error(e);
            return Collections.emptyList();
        }
        return probes;
    }

    /**
     * 
     */
    public boolean getProbesFromStream(String input) {
        try {
            Path path = Paths.get(getClass().getClassLoader().getResource(input).toURI());
            try (Stream<String> stream = Files.lines(path)) {
                stream.forEach(System.out::println);
            }
        } catch (URISyntaxException | IOException e) {
            log.error(e);
            return false;
        }
        return true;
    }

    /**
     * Fast Fourier Transforms (FFT)
     */
    public void fft() {
        FastFourierTransformer transformer = new FastFourierTransformer(null);
    }
}
