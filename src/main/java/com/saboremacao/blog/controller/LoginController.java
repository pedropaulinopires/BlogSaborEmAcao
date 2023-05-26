package com.saboremacao.blog.controller;

import com.saboremacao.blog.domain.Login;
import com.saboremacao.blog.enums.ResponseEnums;
import com.saboremacao.blog.request.*;
import com.saboremacao.blog.service.CookieService;
import com.saboremacao.blog.service.LoginService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/login")
public class LoginController {

    private final String USER_AUTHENTICATED = "userAuthentication";

    private final LoginService loginService;


    private BCryptPasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @GetMapping
    public ModelAndView login(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        try {
            String cookie = CookieService.getCookie(request, USER_AUTHENTICATED);
            if (cookie != null && loginService.findLoginById(UUID.fromString(cookie)).getAccountActive().equals(ResponseEnums.N)) {
                ModelAndView mv = new ModelAndView("verificarCadastro/verificarCadastro");
                mv.addObject("user", loginService.findLoginByEmail(loginService.findLoginById(UUID.fromString(cookie)).getEmail()));
                return mv;
            } else if (cookie != null && loginService.findLoginById(UUID.fromString(cookie)).getIsPasswordRecover().equals(ResponseEnums.Y)) {
                ModelAndView mv = new ModelAndView("verificarEsqueceu/verificarEsqueceu");
                mv.addObject("user", loginService.findLoginById(UUID.fromString(cookie)));
                CookieService.setCookie(response, USER_AUTHENTICATED, String.valueOf(loginService.findLoginById(UUID.fromString(cookie)).getId()), 60 * 60 * 24);
                return mv;
            } else if (cookie != null) {
                return new ModelAndView("redirect:/");
            } else {
                return new ModelAndView("login/login");
            }
        } catch (Exception e) {
            return new ModelAndView("login/login");
        }
    }

    @PostMapping("/sing-in")
    public ModelAndView loginSingIn(@Valid LoginSingInRequest login, BindingResult bindingResult, @Param("remember") String remember, HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        try {
            if (bindingResult.hasErrors()) {
                return new ModelAndView("login/login");
            } else if (login.getEmail().length() < 8) {
                ModelAndView mv = new ModelAndView("login/login");
                mv.addObject("usernameLength", Boolean.TRUE);
                return mv;
            } else if (!login.getEmail().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                ModelAndView mv = new ModelAndView("login/login");
                mv.addObject("emailInvalid", Boolean.TRUE);
                return mv;
            } else if (login.getPassword().length() < 8) {
                ModelAndView mv = new ModelAndView("login/login");
                mv.addObject("passwordLength", Boolean.TRUE);
                return mv;
            } else if (loginService.findLoginByEmail(login.getEmail()) == null) {
                ModelAndView mv = new ModelAndView("login/login");
                mv.addObject("userInvalid", Boolean.TRUE);
                return mv;
            } else if ((loginService.findLoginByEmail(login.getEmail()) != null) && !(getPasswordEncoder().matches(login.getPassword(), loginService.findLoginByEmail(login.getEmail()).getPassword()))) {
                ModelAndView mv = new ModelAndView("login/login");
                mv.addObject("userInvalid", Boolean.TRUE);
                return mv;
            } else if ((loginService.findLoginByEmail(login.getEmail())).getAccountActive().equals(ResponseEnums.N)) {
                ModelAndView mv = new ModelAndView("verificarCadastro/verificarCadastro");
                mv.addObject("user", loginService.findLoginByEmail(login.getEmail()));
                int timeAuthenticated = 60 * 60 * 24;
                CookieService.setCookie(response, USER_AUTHENTICATED, String.valueOf(loginService.findLoginByEmail(login.getEmail()).getId()), timeAuthenticated);
                return mv;
            } else if ((loginService.findLoginByEmail(login.getEmail())).getIsPasswordRecover().equals(ResponseEnums.Y)) {
                int timeAuthenticated = 60 * 60 * 24;
                if (remember != null) {
                    timeAuthenticated = timeAuthenticated * 30;
                }
                Login loginByEmail = loginService.findLoginByEmail(login.getEmail());
                loginByEmail.setCodeVerify(null);
                loginByEmail.setIsPasswordRecover(ResponseEnums.N);
                LoginRequest loginRequest = new LoginRequest();
                loginRequest.fromLogin(loginByEmail);
                loginService.replaceLogin(loginRequest, loginByEmail.getId());
                CookieService.setCookie(response, USER_AUTHENTICATED, String.valueOf(loginByEmail.getId()), timeAuthenticated);
                return new ModelAndView("redirect:/");
            } else {
                int timeAuthenticated = 60 * 60 * 24;
                if (remember != null) {
                    timeAuthenticated = timeAuthenticated * 30;
                }

                CookieService.setCookie(response, USER_AUTHENTICATED, String.valueOf(loginService.findLoginByEmail(login.getEmail()).getId()), timeAuthenticated);
                return new ModelAndView("redirect:/");
            }


        } catch (Exception e) {
            return new ModelAndView("redirect:/");
        }
    }


    @ModelAttribute(value = "loginSingInRequest")
    public LoginSingInRequest loginSingInRequest() {
        return new LoginSingInRequest();
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
