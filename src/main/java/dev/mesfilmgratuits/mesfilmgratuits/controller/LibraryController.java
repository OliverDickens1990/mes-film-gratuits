package dev.mesfilmgratuits.mesfilmgratuits.controller;

import dev.mesfilmgratuits.mesfilmgratuits.entity.MovieEntity;
import dev.mesfilmgratuits.mesfilmgratuits.repo.MovieRepository;
import dev.mesfilmgratuits.mesfilmgratuits.service.LibraryService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
public class LibraryController {

    private final LibraryService libraryService;
    private final MovieRepository repo;

    @Value("${media.library.path}")
    private String libraryPath;

    public LibraryController(LibraryService libraryService, MovieRepository repo) {
        this.libraryService = libraryService;
        this.repo = repo;
    }

    @GetMapping("/scan")
    public String scan() throws IOException {
        int added = libraryService.scanAndSave(libraryPath);
        return "Scan complete. New movies added: " + added;
    }

    @GetMapping("/movies-db")
    public List<MovieEntity> moviesFromDb() {
        return repo.findAll();
    }
}
