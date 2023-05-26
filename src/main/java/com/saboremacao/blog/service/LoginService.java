package com.saboremacao.blog.service;

import com.saboremacao.blog.domain.Login;
import com.saboremacao.blog.exception.UserNotFoundException;
import com.saboremacao.blog.repository.LoginRepository;
import com.saboremacao.blog.request.LoginRequest;
import com.saboremacao.blog.request.LoginSingInRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final LoginRepository loginRepository;

    // find all login
    public Page<Login> findAllLogin(Pageable pageable) {
        return loginRepository.findAll(pageable);
    }

    //add login
    public Login saveLogin(LoginRequest loginRequest) {
        return loginRepository.save(loginRequest.build());
    }

    //find login by Email
    public Login findLoginByEmail(String username) {
        return loginRepository.findByEmail(username);
    }


    //find login by Email
    public Login findLoginByEmail(LoginRequest loginRequest) {
        return loginRepository.findByEmail(loginRequest.getEmail());
    }

    //find login by Email
    public Login findLoginByEmail(LoginSingInRequest loginSIngInRequest) {
        return loginRepository.findByEmail(loginSIngInRequest.getEmail());
    }

    //find login by id
    public Login findLoginById(UUID id) {
        return loginRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found by id"));
    }

    //replace login
    public void replaceLogin(LoginRequest loginRequest, UUID id) {
        findLoginById(id);
        loginRepository.save(loginRequest.build(loginRequest, id));
    }

    //remove login
    public void removeLogin(UUID id) {
        loginRepository.delete(findLoginById(id));
    }


}
