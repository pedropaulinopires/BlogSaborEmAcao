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
public class PasswordEdit {

    @NotEmpty(message = "Campo de senha obrigatório!")
    private String password;

}
