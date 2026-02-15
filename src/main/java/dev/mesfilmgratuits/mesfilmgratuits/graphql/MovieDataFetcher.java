package dev.mesfilmgratuits.mesfilmgratuits.graphql;

import com.netflix.graphql.dgs.*;
import dev.mesfilmgratuits.mesfilmgratuits.entity.MovieEntity;
import dev.mesfilmgratuits.mesfilmgratuits.repo.MovieRepository;
import org.springframework.data.domain.*;

import java.util.List;

@DgsComponent
public class MovieDataFetcher {

    private final MovieRepository repo;

    public MovieDataFetcher(MovieRepository repo) {
        this.repo = repo;
    }

    @DgsQuery
    public MoviePage movies(@InputArgument Integer page,
                            @InputArgument Integer size,
                            @InputArgument String search) {

        int p = (page == null) ? 0 : page;
        int s = (size == null) ? 20 : size;
        String q = (search == null) ? "" : search.trim();

        Pageable pageable = PageRequest.of(p, s);

        Page<MovieEntity> result =
                q.isBlank()
                        ? repo.findAll(pageable)
                        : repo.findByTitleContainingIgnoreCase(q, pageable);

        return new MoviePage(
                result.getContent(),
                p,
                s,
                result.getTotalPages(),
                result.getTotalElements()
        );
    }

    @DgsQuery
    public MovieEntity movie(@InputArgument Long id) {
        return repo.findById(id).orElse(null);
    }

    public record MoviePage(
            List<MovieEntity> items,
            int page,
            int size,
            int totalPages,
            long totalItems
    ) { }

    @DgsData(parentType = "Movie", field = "watchUrl")
    public String watchUrl(DgsDataFetchingEnvironment dfe) {
        MovieEntity movie = dfe.getSource();
        return "/watch/" + movie.getId();
    }

    @DgsData(parentType = "Movie", field = "streamUrl")
    public String streamUrl(DgsDataFetchingEnvironment dfe) {
        MovieEntity movie = dfe.getSource();
        return "/stream/" + movie.getId();
    }
}
