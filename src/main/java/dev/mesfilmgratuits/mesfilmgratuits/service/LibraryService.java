package dev.mesfilmgratuits.mesfilmgratuits.service;

import dev.mesfilmgratuits.mesfilmgratuits.entity.MovieEntity;
import dev.mesfilmgratuits.mesfilmgratuits.repo.MovieRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Service
public class LibraryService {

    private final MediaScannerService scanner;
    private final MovieRepository repo;

    public LibraryService(MediaScannerService scanner, MovieRepository repo) {
        this.scanner = scanner;
        this.repo = repo;
    }

    public int scanAndSave(String rootFolder) throws IOException {
        List<Path> files = scanner.listMovieFiles(rootFolder);

        int saved = 0;

        for (Path path : files) {
            String fullPath = path.toAbsolutePath().toString();

            // upsert by fullPath
            MovieEntity entity = scanner.toEntity(path);

            var existing = repo.findByFullPath(fullPath);
            if (existing.isPresent()) {
                MovieEntity e = existing.get();
                e.setTitle(entity.getTitle());
                e.setYear(entity.getYear());
                e.setResolution(entity.getResolution());
                e.setSource(entity.getSource());
                e.setAudio(entity.getAudio());
                e.setGroupName(entity.getGroupName());
                e.setFilename(entity.getFilename());
                repo.save(e);
            } else {
                repo.save(entity);
                saved++;
            }
        }

        return saved;
    }
}
