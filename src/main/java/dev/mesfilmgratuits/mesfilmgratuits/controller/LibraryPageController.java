package dev.mesfilmgratuits.mesfilmgratuits.controller;

import dev.mesfilmgratuits.mesfilmgratuits.entity.MovieEntity;
import dev.mesfilmgratuits.mesfilmgratuits.repo.MovieRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Controller
public class LibraryPageController {

    private final MovieRepository repo;

    public LibraryPageController(MovieRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/library")
    public String library(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);

        Page<MovieEntity> moviesPage =
                (search != null && !search.isBlank())
                        ? repo.findByTitleContainingIgnoreCase(search, pageable)
                        : repo.findAll(pageable);

        model.addAttribute("moviesPage", moviesPage);
        model.addAttribute("movies", moviesPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", moviesPage.getTotalPages());
        model.addAttribute("size", size);
        model.addAttribute("search", (search == null) ? "" : search);

        return "library";
    }


}
