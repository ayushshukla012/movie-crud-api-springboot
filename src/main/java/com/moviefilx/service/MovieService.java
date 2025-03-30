package com.moviefilx.service;

import com.moviefilx.dto.MovieDto;
import com.moviefilx.dto.MoviePageResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface MovieService {
    MovieDto addMovie(MovieDto movieDto, MultipartFile file) throws IOException;

    MovieDto getMovie(Integer movieId);

    List<MovieDto> getAllMovies();

    MovieDto updateMovie(Integer movieId, MovieDto movieDto, MultipartFile file) throws IOException;

    String deleteMovie(Integer movieId) throws IOException;

    MoviePageResponse getAllMovieWithPagination(Integer pageNumber, Integer pageSize);

    MoviePageResponse getAllMovieWithPaginationAndSorting(Integer pageNumber, Integer pageSize,
                                                          String sortBy, String dir);
}
