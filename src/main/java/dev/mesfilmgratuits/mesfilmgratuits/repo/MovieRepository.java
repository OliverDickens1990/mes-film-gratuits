package dev.mesfilmgratuits.mesfilmgratuits.repo;

import dev.mesfilmgratuits.mesfilmgratuits.entity.MovieEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MovieRepository extends JpaRepository<MovieEntity, Long> {
    Optional<MovieEntity> findByFullPath(String fullPath);
}
