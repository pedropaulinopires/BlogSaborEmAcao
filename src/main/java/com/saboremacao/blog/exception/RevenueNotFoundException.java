package com.saboremacao.blog.exception;

import lombok.Getter;

@Getter
public class RevenueNotFoundException extends RuntimeException {
    public RevenueNotFoundException(String message) {
        super(message);
    }
}
