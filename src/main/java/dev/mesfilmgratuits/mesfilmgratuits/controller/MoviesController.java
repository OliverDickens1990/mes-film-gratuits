package dev.mesfilmgratuits.mesfilmgratuits.controller;

import dev.mesfilmgratuits.mesfilmgratuits.model.Movie;
import dev.mesfilmgratuits.mesfilmgratuits.service.MediaScannerService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@RestController
public class MoviesController {

    private final MediaScannerService scanner;

    @Value("${media.library.path}")
    private String libraryPath;

    public MoviesController(MediaScannerService scanner) {
        this.scanner = scanner;
    }

    @GetMapping("/movies")
    public List<Movie> movies() throws IOException {
        List<Path> files = scanner.listMovieFiles(libraryPath);

        return files.stream()
                .map(p -> scanner.parseMovieFilename(p.getFileName().toString()))
                .sorted((a, b) -> a.getTitle().compareToIgnoreCase(b.getTitle()))
                .toList();
    }
}
