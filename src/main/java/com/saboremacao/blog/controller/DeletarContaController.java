package com.saboremacao.blog.controller;

import com.saboremacao.blog.domain.Login;
import com.saboremacao.blog.enums.ResponseEnums;
import com.saboremacao.blog.request.*;
import com.saboremacao.blog.service.CookieService;
import com.saboremacao.blog.service.LoginService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

@Controller
@RequestMapping("/deletarConta")
@RequiredArgsConstructor
public class DeletarContaController {

    private final String USER_AUTHENTICATED = "userAuthentication";

    private final LoginService loginService;

    @GetMapping
    public ModelAndView favoritoslimparTodos(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        try {
            if (CookieService.getCookie(request, USER_AUTHENTICATED) != null && loginService.findLoginById(UUID.fromString(CookieService.getCookie(request, USER_AUTHENTICATED))).getAccountActive().equals(ResponseEnums.N)) {
                ModelAndView mv = new ModelAndView("verificarCadastro/verificarCadastro");
                mv.addObject("user", loginService.findLoginByEmail(loginService.findLoginById(UUID.fromString(CookieService.getCookie(request, USER_AUTHENTICATED))).getEmail()));
                return mv;
            } else if ((CookieService.getCookie(request, USER_AUTHENTICATED) != null && loginService.findLoginById(UUID.fromString(CookieService.getCookie(request, USER_AUTHENTICATED))).getIsPasswordRecover().equals(ResponseEnums.Y))) {
                ModelAndView mv = new ModelAndView("verificarEsqueceu/verificarEsqueceu");
                Login loginByEmail = loginService.findLoginByEmail(loginService.findLoginById(UUID.fromString(CookieService.getCookie(request, USER_AUTHENTICATED))).getEmail());
                mv.addObject("user", loginByEmail);
                return mv;
            } else if (CookieService.getCookie(request, USER_AUTHENTICATED) != null && loginService.findLoginById(UUID.fromString(CookieService.getCookie(request, USER_AUTHENTICATED))).getAccountActive().equals(ResponseEnums.Y)) {
                try {
                    Login login = loginService.findLoginById(UUID.fromString(CookieService.getCookie(request, USER_AUTHENTICATED)));
                    login.getRevenues().clear();
                    LoginRequest loginRequest = new LoginRequest();
                    loginRequest.fromLogin(login);
                    loginService.replaceLogin(loginRequest, login.getId());
                    loginService.removeLogin(UUID.fromString(CookieService.getCookie(request, USER_AUTHENTICATED)));
                    ModelAndView mv = new ModelAndView("redirect:/");
                    return mv;

                } catch (Exception e) {
                    return new ModelAndView("redirect:/");
                }
            } else {
                return new ModelAndView("redirect:/");
            }
        } catch (Exception e) {
            return new ModelAndView("redirect:/");
        }
    }


    @ModelAttribute(value = "revenueSearch")
    public RevenueSearch revenueSearch() {
        return new RevenueSearch();
    }

    @ModelAttribute(value = "codeRequest")
    public CodeRequest codeRequest() {
        return new CodeRequest();
    }

    @ModelAttribute(value = "loginRequest")
    public LoginRequest loginRequest() {
        return new LoginRequest();
    }

    @ModelAttribute(value = "passwordEdit")
    public PasswordEdit passwordEdit() {
        return new PasswordEdit();
    }

    @ModelAttribute(value = "loginForgot")
    public LoginForgot loginForgot() {
        return new LoginForgot();
    }

    @ModelAttribute(value = "loginEdit")
    public LoginEdit loginEdit() {
        return new LoginEdit();
    }
}
