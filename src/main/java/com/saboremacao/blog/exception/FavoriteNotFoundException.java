package com.saboremacao.blog.exception;

import lombok.Getter;

@Getter
public class FavoriteNotFoundException extends RuntimeException {
    public FavoriteNotFoundException(String message) {
        super(message);
    }
}
