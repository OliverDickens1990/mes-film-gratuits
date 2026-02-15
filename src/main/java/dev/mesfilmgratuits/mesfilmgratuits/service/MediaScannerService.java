package dev.mesfilmgratuits.mesfilmgratuits.service;

import dev.mesfilmgratuits.mesfilmgratuits.entity.MovieEntity;
import org.springframework.stereotype.Service;
import dev.mesfilmgratuits.mesfilmgratuits.model.Movie;
import java.io.IOException;
import java.nio.file.*;
import java.util.stream.Stream;
import java.util.ArrayList;
import java.util.List;
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


    public MovieEntity toEntity(Path filePath) {
        String filename = filePath.getFileName().toString();

        // reuse your existing parsing logic (title/year/tags)
        var movie = parseMovieFilename(filename);

        return new MovieEntity(
                movie.getTitle(),
                movie.getYear(),
                movie.getResolution(),
                movie.getSource(),
                movie.getAudio(),
                movie.getGroup(),
                filePath.toAbsolutePath().toString(),
                filename
        );
    }

    public Movie parseMovieFilename(String filename) {

        // 1) Pattern: "Afterburn (2025) [1080p] [WEBRip] [5.1] [YTS.MX].mkv"
        Pattern bracketPattern = Pattern.compile(
                "^(?<title>.+?)\\s*\\((?<year>\\d{4})\\)\\s*(?<tags>(\\s*\\[[^\\]]+\\])*)",
                Pattern.CASE_INSENSITIVE
        );

        Matcher m1 = bracketPattern.matcher(filename);
        if (m1.find()) {
            String title = cleanTitle(m1.group("title"));
            Integer year = Integer.parseInt(m1.group("year"));
            String tags = m1.group("tags") == null ? "" : m1.group("tags");

            // pull tags in order (best-effort)
            List<String> tagList = new ArrayList<>();
            Matcher tagMatcher = Pattern.compile("\\[([^\\]]+)\\]").matcher(tags);
            while (tagMatcher.find()) {
                tagList.add(tagMatcher.group(1));
            }

            String resolution = findFirstMatching(tagList, "\\d{3,4}p");
            String source = findFirstMatching(tagList, "(WEBRip|BluRay|BrRip|HDRip|DVDRip|WEB-DL)");
            String audio = findFirstMatching(tagList, "(\\d\\.\\d|AAC\\d\\.\\d|DDP\\d\\.\\d|DTS)");
            String group = tagList.isEmpty() ? null : tagList.get(tagList.size() - 1);

            return new Movie(title, year, resolution, source, audio, group, filename);
        }

        // 2) Pattern: "10.Things.I.Hate.About.You.1999.1080p.BrRip.x264....mp4"
        Pattern dottedPattern = Pattern.compile(
                "^(?<title>.+?)[\\s._-]+(?<year>19\\d{2}|20\\d{2})(?<rest>.*)$",
                Pattern.CASE_INSENSITIVE
        );

        Matcher m2 = dottedPattern.matcher(stripExtension(filename));
        if (m2.find()) {
            String rawTitle = m2.group("title");
            Integer year = Integer.parseInt(m2.group("year"));
            String rest = m2.group("rest") == null ? "" : m2.group("rest");

            String title = cleanTitle(rawTitle.replace('.', ' ').replace('_', ' ').replace('-', ' '));

            // Find things like 1080p, WEBRip, BrRip, etc. anywhere in the rest
            String resolution = matchFirst(rest, "(\\d{3,4}p)");
            String source = matchFirst(rest, "(WEBRip|BluRay|BrRip|HDRip|DVDRip|WEB-DL)");
            String audio = matchFirst(rest, "(\\d\\.\\d|AAC\\d\\.\\d|DDP\\d\\.\\d|DTS)");
            String group = matchFirst(rest, "\\[?([A-Za-z0-9.]+)\\]?$");

            return new Movie(title, year, resolution, source, audio, group, filename);
        }

        // fallback if format doesn't match
        return new Movie(stripExtension(filename), null, null, null, null, null, filename);
    }

    private String stripExtension(String filename) {
        int dot = filename.lastIndexOf('.');
        return (dot > 0) ? filename.substring(0, dot) : filename;
    }

    private String cleanTitle(String title) {
        return title.trim().replaceAll("\\s+", " ");
    }

    private String findFirstMatching(List<String> tags, String regex) {
        Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        for (String t : tags) {
            if (p.matcher(t).find()) return t;
        }
        return null;
    }

    private String matchFirst(String text, String regex) {
        Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(text);
        if (m.find()) return m.group(1) != null ? m.group(1) : m.group();
        return null;
    }
}