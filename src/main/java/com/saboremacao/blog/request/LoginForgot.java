package com.saboremacao.blog.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class LoginForgot {

    @NotEmpty(message = "Campo de email obrigatório!")
    private String email;
}
