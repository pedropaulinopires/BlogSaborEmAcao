package com.saboremacao.blog.service;

import com.saboremacao.blog.domain.Login;
import com.saboremacao.blog.domain.Revenue;
import com.saboremacao.blog.request.LoginRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final LoginService loginService;
    private final RevenueService revenueService;

    public void toggleFavorite(String idRevenue, UUID idLogin) {
        Revenue revenue = revenueService.findRevenueById(UUID.fromString(idRevenue));
        if (revenue != null) {
            Login login = loginService.findLoginById(idLogin);
            if (login.getRevenues().contains(revenue)) {
                login.getRevenues().remove(revenue);
            } else {
                login.getRevenues().add(revenue);
            }
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.fromLogin(login);
            loginService.replaceLogin(loginRequest, idLogin);
        }
    }
}
