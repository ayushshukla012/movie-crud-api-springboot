package com.moviefilx.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moviefilx.dto.MovieDto;
import com.moviefilx.dto.MoviePageResponse;
import com.moviefilx.exceptions.EmptyFileException;
import com.moviefilx.service.MovieService;
import com.moviefilx.utils.AppConstants;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/movie")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    // As only "file" and "text" can be send so we need to map the MovieDTO object to String and
    // later convert it to json

    // We cant directly send the "file" or "json" object so we need to always convert it to a "String" object
    // and make sure this "String" object is mapped with "Class" Object to interact with the "Service Layer".
    @PostMapping("/add-movie")
    public ResponseEntity<MovieDto> addMovieHandler(@RequestPart MultipartFile file,
                                                    @RequestPart String movieDto) throws IOException {

        if (file.isEmpty()) {
            throw new EmptyFileException("File is empty. Please send another file.");
        }
        MovieDto dto = convertToMovieDto(movieDto);
        return new ResponseEntity<>(movieService.addMovie(dto, file), HttpStatus.CREATED);
    }

    //Call the below by passing the movieId in the URL.
    //1. http://localhost:8080/api/v1/movie/4
    @GetMapping("/{movieId}")
    public ResponseEntity<MovieDto> getMovieHandler(@PathVariable Integer movieId) {
        return ResponseEntity.ok(movieService.getMovie(movieId));
    }

    //Call the below either by, no field input required.
    //1. http://localhost:8080/api/v1/movie/all
    @GetMapping("/all")
    public ResponseEntity<List<MovieDto>> getAllMovieHandler() {
        return ResponseEntity.ok(movieService.getAllMovies());
    }

    //Call the below by passing the movieId and give the "file" and "movieDtoObj" field data in the same variablename,
    // either can be given or both to update the particular field.
    //1. http://localhost:8080/api/v1/movie/update/2
    @PutMapping("/update/{movieId}")
    public ResponseEntity<MovieDto> updateMovieHandler(@PathVariable Integer movieId,
                                                       @RequestPart String movieDtoObj,
                                                       @RequestPart MultipartFile file) throws IOException {
        if(file.isEmpty()) file = null;
        MovieDto movieDto = convertToMovieDto(movieDtoObj);
        return ResponseEntity.ok(movieService.updateMovie(movieId,movieDto,file));
    }

    //Call the below either by, just pass the movieId in url.
    //1. http://localhost:8080/api/v1/movie/delete/3
    @DeleteMapping("/delete/{movieId}")
    public ResponseEntity<String> deleteMovieHandler(@PathVariable Integer movieId) throws IOException {
        return ResponseEntity.ok(movieService.deleteMovie(movieId));
    }


    //Call the below either by
    //1. http://localhost:8080/api/v1/movie/allMoviesPage?pageNumber=1&pageSize=2
    //2. http://localhost:8080/api/v1/movie/allMoviesPage?pageNumber=1
    //3. http://localhost:8080/api/v1/movie/allMoviesPage
    @GetMapping("/allMoviesPage")
    public ResponseEntity<MoviePageResponse> getMovieWithPagination(
            @RequestParam(defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize
    ) {
        return ResponseEntity.ok(movieService.getAllMovieWithPagination(pageNumber,pageSize));
    }

    //Call the below either by
    //1. http://localhost:8080/api/v1/movie/allMoviesPageSort
    //2. http://localhost:8080/api/v1/movie/allMoviesPageSort?sortBy=title
    //3. http://localhost:8080/api/v1/movie/allMoviesPageSort?sortBy=title&dir=dsc
    @GetMapping("/allMoviesPageSort")
    public ResponseEntity<MoviePageResponse> getMovieWithPaginationAndSorting(
            @RequestParam(defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(defaultValue = AppConstants.SORT_BY, required = false) String sortBy,
            @RequestParam(defaultValue = AppConstants.SORT_DIR, required = false) String dir
    ) {
        return ResponseEntity.ok(movieService.getAllMovieWithPaginationAndSorting(pageNumber,pageSize,sortBy,dir));
    }

    //Here is method that takes the object in String and
    // convert it to the MovieDTO object
    private MovieDto convertToMovieDto(String movieDtoObj) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(movieDtoObj, MovieDto.class);
    }
}
