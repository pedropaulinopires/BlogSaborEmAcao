package com.saboremacao.blog.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CodeRequest {

    @NotNull(message = "Código inválido, tente novamente!")
    private Integer codeVerify;
}
