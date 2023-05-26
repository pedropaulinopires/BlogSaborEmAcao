package com.saboremacao.blog.request;

import com.saboremacao.blog.domain.Login;
import com.saboremacao.blog.domain.Revenue;
import com.saboremacao.blog.enums.ResponseEnums;
import com.saboremacao.blog.enums.Roles;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginRequest {

    @NotEmpty(message = "Campo nome é obrigatório!")
    private String name;

    @NotEmpty(message = "Campo de email é obrigatório!")
    private String email;

    @NotEmpty(message = "Campo de senha é obrigatório!")
    private String password;

    private Roles role;

    private ResponseEnums accountActive;

    private ResponseEnums isPasswordRecover;

    private Integer codeVerify;

    private List<Revenue> revenues;

    public Login build() {
        return new Login().builder().name(this.name).email(this.email).password(this.password).role(this.role).accountActive(this.accountActive).codeVerify(this.codeVerify).isPasswordRecover(this.isPasswordRecover).revenues(this.revenues).build();
    }

    public Login build(LoginRequest loginRequest) {
        return new Login().builder().name(loginRequest.getName()).email(loginRequest.getEmail()).password(loginRequest.getPassword()).role(loginRequest.getRole()).codeVerify(loginRequest.getCodeVerify()).accountActive(loginRequest.getAccountActive()).isPasswordRecover(loginRequest.getIsPasswordRecover()).revenues(loginRequest.getRevenues()).build();
    }

    public Login build(LoginRequest loginRequest, UUID id) {
        return new Login().builder().id(id).name(loginRequest.getName()).email(loginRequest.getEmail()).password(loginRequest.getPassword()).role(loginRequest.getRole()).codeVerify(loginRequest.getCodeVerify()).accountActive(loginRequest.getAccountActive()).isPasswordRecover(loginRequest.getIsPasswordRecover()).revenues(loginRequest.getRevenues()).build();
    }

    public void fromLogin(Login login) {
        this.name = login.getName();
        this.email = login.getEmail();
        this.password = login.getPassword();
        this.role = login.getRole();
        this.isPasswordRecover = login.getIsPasswordRecover();
        this.accountActive = login.getAccountActive();
        this.codeVerify = login.getCodeVerify();
        this.revenues = login.getRevenues();
    }
}
