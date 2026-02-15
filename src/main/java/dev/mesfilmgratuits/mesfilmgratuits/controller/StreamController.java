package dev.mesfilmgratuits.mesfilmgratuits.controller;

import dev.mesfilmgratuits.mesfilmgratuits.entity.MovieEntity;
import dev.mesfilmgratuits.mesfilmgratuits.repo.MovieRepository;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/stream")
public class StreamController {

    private static final long CHUNK_SIZE = 1024L * 1024L * 5L; // 5MB

    private final MovieRepository repo;

    public StreamController(MovieRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResourceRegion> stream(
            @PathVariable Long id,
            @RequestHeader HttpHeaders headers
    ) throws IOException {

        MovieEntity movie = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie not found: " + id));

        Path path = Path.of(movie.getFullPath());
        Resource resource = new UrlResource(path.toUri());

        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        long contentLength = resource.contentLength();
        MediaType mediaType = guessMediaType(movie.getFilename());

        List<HttpRange> ranges = headers.getRange();

        if (ranges == null || ranges.isEmpty()) {
            long rangeLength = Math.min(CHUNK_SIZE, contentLength);
            ResourceRegion region = new ResourceRegion(resource, 0, rangeLength);

            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                    .contentType(mediaType)
                    .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                    .header(HttpHeaders.CONTENT_RANGE, "bytes 0-" + (rangeLength - 1) + "/" + contentLength)
                    .contentLength(rangeLength)
                    .body(region);
        }

        HttpRange range = ranges.get(0);
        long start = range.getRangeStart(contentLength);
        long end = range.getRangeEnd(contentLength);
        if (end >= contentLength) end = contentLength - 1;

        long requestedLength = end - start + 1;
        long rangeLength = Math.min(CHUNK_SIZE, requestedLength);

        ResourceRegion region = new ResourceRegion(resource, start, rangeLength);

        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .contentType(mediaType)
                .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                .header(HttpHeaders.CONTENT_RANGE,
                        "bytes " + start + "-" + (start + rangeLength - 1) + "/" + contentLength)
                .contentLength(rangeLength)
                .body(region);
    }

    private MediaType guessMediaType(String filename) {
        String lower = filename.toLowerCase();
        if (lower.endsWith(".mp4")) return MediaType.valueOf("video/mp4");
        if (lower.endsWith(".webm")) return MediaType.valueOf("video/webm");
        // Chrome generally won't play mkv, but we still serve a type
        if (lower.endsWith(".mkv")) return MediaType.valueOf("video/x-matroska");
        return MediaType.APPLICATION_OCTET_STREAM;
    }
}
