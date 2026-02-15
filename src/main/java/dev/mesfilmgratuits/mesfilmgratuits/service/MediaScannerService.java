package dev.mesfilmgratuits.mesfilmgratuits.service;

import org.springframework.stereotype.Service;
import dev.mesfilmgratuits.mesfilmgratuits.model.Movie;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Stream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class MediaScannerService {

    public List<Path> listMovieFiles(String rootFolder) throws IOException {
        Path root = Paths.get(rootFolder);

        if (!Files.exists(root) || !Files.isDirectory(root)) {
            throw new IllegalArgumentException("Folder does not exist or is not a directory: " + rootFolder);
        }

        try (Stream<Path> stream = Files.walk(root)) {
            return stream
                    .filter(Files::isRegularFile)
                    .filter(this::isVideoFile)
                    .limit(2000) // safety limit for now
                    .toList();
        }
    }

    private boolean isVideoFile(Path path) {
        String name = path.getFileName().toString().toLowerCase();
        return name.endsWith(".mp4")
                || name.endsWith(".mkv")
                || name.endsWith(".avi")
                || name.endsWith(".mov")
                || name.endsWith(".webm");
    }

    public Movie parseMovieFilename(String filename) {

        Pattern pattern = Pattern.compile("^(.*?) \\((\\d{4})\\) \\[(.*?)\\] \\[(.*?)\\] \\[(.*?)\\] \\[(.*?)\\]");
        Matcher matcher = pattern.matcher(filename);

        if (matcher.find()) {
            String title = matcher.group(1);
            Integer year = Integer.parseInt(matcher.group(2));
            String resolution = matcher.group(3);
            String source = matcher.group(4);
            String audio = matcher.group(5);
            String group = matcher.group(6);

            return new Movie(title, year, resolution, source, audio, group, filename);
        }

        // fallback if format doesn't match
        return new Movie(filename, null, null, null, null, null, filename);
    }

}