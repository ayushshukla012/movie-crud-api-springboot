package com.moviefilx.exceptions;

public class MovieNotFoundException extends RuntimeException{
    public MovieNotFoundException(String message) {
        super(message);
    }
}
