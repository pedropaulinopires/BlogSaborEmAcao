package com.saboremacao.blog.request;

import com.saboremacao.blog.domain.Login;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class LoginEdit {

    @NotEmpty(message = "Campo de email obrigatório!")
    private String email;


    public void fromLogin(Login login) {
        this.email = login.getEmail();
    }
}
