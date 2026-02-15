package dev.mesfilmgratuits.mesfilmgratuits.controller;

import dev.mesfilmgratuits.mesfilmgratuits.repo.MovieRepository;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/watch")
public class WatchController {

    private final MovieRepository repo;

    public WatchController(MovieRepository repo) {
        this.repo = repo;
    }

    @GetMapping(value = "/{id}", produces = MediaType.TEXT_HTML_VALUE)
    public String watch(@PathVariable Long id) {
        var movie = repo.findById(id).orElseThrow();

        return """
            <!doctype html>
            <html>
            <head>
              <meta charset="utf-8"/>
              <title>Watch</title>
            </head>
            <body style="margin:0;background:black;">
              <div style="color:white;padding:12px;font-family:Arial;">
                Now playing: %s (id=%d)
              </div>
              <video style="width:100%%;height:calc(100vh - 50px);" controls autoplay>
                <source src="/stream/%d" type="video/mp4">
              </video>
            </body>
            </html>
            """.formatted(movie.getFilename(), id, id);
    }
}
