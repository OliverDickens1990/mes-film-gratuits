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

        int startAt = movie.getProgressSeconds() == null ? 0 : movie.getProgressSeconds();

        return """
            <!doctype html>
            <html>
            <head>
              <meta charset="utf-8"/>
              <title>Watch</title>
            </head>
            <body style="margin:0;background:black;">
              <div style="color:white;padding:12px;font-family:Arial;display:flex;justify-content:space-between;">
                <div>Now playing: %s (id=%d)</div>
                <div id="status" style="color:#aaa;">Saving…</div>
              </div>

              <video id="player" style="width:100%%;height:calc(100vh - 50px);" controls autoplay>
                <source src="/stream/%d" type="video/mp4">
              </video>

              <script>
                const movieId = %d;
                const startAt = %d;
                const player = document.getElementById("player");
                const status = document.getElementById("status");

                // When metadata is loaded, seek to where you left off
                player.addEventListener("loadedmetadata", () => {
                  if (startAt > 3 && startAt < player.duration - 3) {
                    player.currentTime = startAt;
                  }
                });

                // Save progress every 5 seconds while playing
                let lastSent = 0;

                async function sendProgress(seconds) {
                  try {
                    status.textContent = "Saving…";
                    await fetch(`/progress/${movieId}?seconds=${seconds}`, { method: "POST" });
                    status.textContent = "Saved";
                  } catch (e) {
                    status.textContent = "Save failed";
                  }
                }

                setInterval(() => {
                  if (!player.paused && !player.ended) {
                    const sec = Math.floor(player.currentTime);
                    if (sec > lastSent + 4) { // avoid spamming
                      lastSent = sec;
                      sendProgress(sec);
                    }
                  }
                }, 1000);

                // Save when pausing or leaving
                window.addEventListener("beforeunload", () => {
                  const sec = Math.floor(player.currentTime);
                  navigator.sendBeacon(`/progress/${movieId}?seconds=${sec}`);
                });

                player.addEventListener("pause", () => {
                  const sec = Math.floor(player.currentTime);
                  sendProgress(sec);
                });
              </script>
            </body>
            </html>
            """.formatted(movie.getFilename(), id, id, id, startAt);
    }
}
