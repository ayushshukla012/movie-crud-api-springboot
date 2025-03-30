package com.moviefilx.service;

import com.moviefilx.dto.MovieDto;
import com.moviefilx.entities.Movie;
import com.moviefilx.exceptions.FileExistsException;
import com.moviefilx.exceptions.MovieNotFoundException;
import com.moviefilx.repositories.MovieRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class MovieServiceImpl implements MovieService{

    private final MovieRepository movieRepository;

    private final FileService fileService;

    //This value is from the application.yml file.
    @Value("${project.poster}")
    private String path;

    @Value("${base.url}")
    private String baseUrl;

    public MovieServiceImpl(MovieRepository movieRepository, FileService fileService) {
        this.movieRepository = movieRepository;
        this.fileService = fileService;
    }

    @Override
    public MovieDto addMovie(MovieDto movieDto, MultipartFile file) throws IOException {
        //1. Upload the file, if file exist throw an exception
        if(Files.exists(Paths.get(path + File.separator + file.getOriginalFilename()))) {
            throw new FileExistsException("File already exists! Please enter another file name");
        }
        String uploadedFileName = fileService.uploadFile(path, file);

        //2. Set the value of field 'poster' as filename
        movieDto.setPoster(uploadedFileName);

        //3. Map DTO to movie object
        Movie movie = new Movie(
                null,
                movieDto.getTitle(),
                movieDto.getDirector(),
                movieDto.getStudio(),
                movieDto.getMovieCast(),
                movieDto.getReleaseYear(),
                movieDto.getPoster()
        );

        //4. Save the movie object -> saved Movie Object
        Movie savedMovie = movieRepository.save(movie);

        //5. Generate the Poster URL
        String posterUrl = baseUrl + "/file/" + uploadedFileName;

        //6. Map Movie object to DTO object and return it.
        MovieDto response = new MovieDto(
                savedMovie.getMovieId(),
                savedMovie.getTitle(),
                savedMovie.getDirector(),
                savedMovie.getStudio(),
                savedMovie.getMovieCast(),
                savedMovie.getReleaseYear(),
                savedMovie.getPoster(),
                posterUrl
        );

        return response;
    }

    @Override
    public MovieDto getMovie(Integer movieId) {
        //1. Check the data in DB and if exists, fetch the data of given ID
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new MovieNotFoundException("Movie not found with ID = " + movieId + " !!"));

        //2. Generate PosterURL
        String posterUrl = baseUrl + "/file/" + movie.getPoster();

        //3. Map to MovieDTO object and return it.
        MovieDto response = new MovieDto(
                movie.getMovieId(),
                movie.getTitle(),
                movie.getDirector(),
                movie.getStudio(),
                movie.getMovieCast(),
                movie.getReleaseYear(),
                movie.getPoster(),
                posterUrl
        );

        return response;
    }

    @Override
    public List<MovieDto> getAllMovies() {
        //1. To fetch all data dfrom DB
        List<Movie> movies = movieRepository.findAll();

        //2. Iterate through the list, generate posterURL for each movie object.
        // and map to MovieDTO object
        List<MovieDto> movieDtos = new ArrayList<>();

        for (Movie movie : movies) {
            String posterUrl = baseUrl + "/file/" + movie.getPoster();
            MovieDto movieDto = new MovieDto(
                    movie.getMovieId(),
                    movie.getTitle(),
                    movie.getDirector(),
                    movie.getStudio(),
                    movie.getMovieCast(),
                    movie.getReleaseYear(),
                    movie.getPoster(),
                    posterUrl
            );

            movieDtos.add(movieDto);
        }

        return movieDtos;
    }

    @Override
    public MovieDto updateMovie(Integer movieId, MovieDto movieDto, MultipartFile file) throws IOException {
        //1. Check if movie object exists with given movieID
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new MovieNotFoundException("Movie not found with ID = " + movieId + " !!"));

        //2. If file is null, do nothing.
        // But if file is not null, then delete existing file associated with the record,
        // and upload the new file
        String fileName = movie.getPoster();
        if(file != null) {
            Files.deleteIfExists(Paths.get(path + File.separator + fileName));
            fileName = fileService.uploadFile(path, file);
        }

        //3. Set movieDto's poster value, according to Step2
        movieDto.setPoster(fileName);

        //4. Map it to the Movie Object
        Movie movieObj = new Movie(
                movieDto.getMovieId(),
                movieDto.getTitle(),
                movieDto.getDirector(),
                movieDto.getStudio(),
                movieDto.getMovieCast(),
                movieDto.getReleaseYear(),
                movieDto.getPoster()
        );

        //5. Save the movie Object -> return saved movie Object
        Movie updatedMovie = movieRepository.save(movieObj);

        //6. Generate PosterURL for it
        String posterUrl = baseUrl + "/file/" + fileName;

        //7. Map to MovieDTO and Return it.
        MovieDto response = new MovieDto(
                movie.getMovieId(),
                movie.getTitle(),
                movie.getDirector(),
                movie.getStudio(),
                movie.getMovieCast(),
                movie.getReleaseYear(),
                movie.getPoster(),
                posterUrl
        );

        return response;
    }

    @Override
    public String deleteMovie(Integer movieId) throws IOException {
        //1. Check if movie object exists in DB
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new MovieNotFoundException("Movie not found with ID = " + movieId + " !!"));
        Integer id = movie.getMovieId();

        //2. Delete the file associated with this Object.
        Files.deleteIfExists(Paths.get(path + File.separator + movie.getPoster()));

        //3. Delete the movie Object
        movieRepository.delete(movie);


        return "Movie deleted with ID: " + id;
    }
}
