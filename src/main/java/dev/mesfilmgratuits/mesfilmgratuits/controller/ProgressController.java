package dev.mesfilmgratuits.mesfilmgratuits.controller;

import dev.mesfilmgratuits.mesfilmgratuits.repo.MovieRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/progress")
public class ProgressController {

    private final MovieRepository repo;

    public ProgressController(MovieRepository repo) {
        this.repo = repo;
    }

    @PostMapping("/{id}")
    public String saveProgress(@PathVariable Long id, @RequestParam int seconds) {
        var movie = repo.findById(id).orElseThrow();

        // basic safety (don’t store negatives)
        if (seconds < 0) seconds = 0;

        movie.setProgressSeconds(seconds);
        repo.save(movie);

        return "OK";
    }
}

