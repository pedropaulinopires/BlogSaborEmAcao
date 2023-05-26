package com.saboremacao.blog.controller;

import com.saboremacao.blog.domain.Login;
import com.saboremacao.blog.enums.ResponseEnums;
import com.saboremacao.blog.enums.Roles;
import com.saboremacao.blog.request.*;
import com.saboremacao.blog.service.CookieService;
import com.saboremacao.blog.service.LoginService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

@Controller
@RequestMapping("/perfil")
@RequiredArgsConstructor
public class PerfilController {

    private final String USER_AUTHENTICATED = "userAuthentication";

    private final LoginService loginService;

    public void checkUser(ModelAndView mv, HttpServletRequest request) {
        try {
            String cookie = CookieService.getCookie(request, USER_AUTHENTICATED);
            Login login = loginService.findLoginById(UUID.fromString(cookie));
            if (login.getAccountActive().equals(ResponseEnums.Y) && login.getIsPasswordRecover().equals(ResponseEnums.N)) {
                mv.addObject("user", login);
            }
        } catch (Exception e) {
        }
    }

    private BCryptPasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @GetMapping
    public ModelAndView perfil(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
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
                ModelAndView mv = new ModelAndView("perfil/perfil");
                checkUser(mv, request);
                return mv;
            } else {
                return new ModelAndView("redirect:/");
            }
        } catch (Exception e) {
            return new ModelAndView("redirect:/");
        }
    }

    @GetMapping("/editar")
    public ModelAndView perfilEditar(LoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
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
                ModelAndView mv = new ModelAndView("editarPerfil/editarPerfil");
                checkUser(mv, request);
                loginRequest.fromLogin(loginService.findLoginById(UUID.fromString(CookieService.getCookie(request, USER_AUTHENTICATED))));
                loginRequest.setPassword("");
                return mv;
            } else {
                return new ModelAndView("redirect:/");
            }
        } catch (Exception e) {
            return new ModelAndView("redirect:/");
        }
    }

    @PostMapping("/{id}/editar")
    public ModelAndView perfilEditarSalvar(LoginRequest loginRequest, BindingResult bindingResult, @PathVariable String id, HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
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
                if (bindingResult.hasErrors()) {
                    ModelAndView mv = new ModelAndView("editarPerfil/editarPerfil");
                    checkUser(mv, request);
                    return mv;
                } else if (loginRequest.getName().length() < 2) {
                    ModelAndView mv = new ModelAndView("editarPerfil/editarPerfil");
                    checkUser(mv, request);
                    mv.addObject("lengthName", Boolean.TRUE);
                    return mv;
                } else if (!loginRequest.getEmail().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                    ModelAndView mv = new ModelAndView("editarPerfil/editarPerfil");
                    checkUser(mv, request);
                    mv.addObject("emailInvalid", "E-mail inválido!");
                    return mv;
                } else if (loginRequest.getEmail().length() < 8) {
                    ModelAndView mv = new ModelAndView("editarPerfil/editarPerfil");
                    checkUser(mv, request);
                    mv.addObject("lengthEmail", Boolean.TRUE);
                    return mv;
                } else if (loginService.findLoginByEmail(loginRequest.getEmail()) != null && !loginService.findLoginById(UUID.fromString(CookieService.getCookie(request, USER_AUTHENTICATED))).getEmail().equals(loginRequest.getEmail())) {
                    ModelAndView mv = new ModelAndView("editarPerfil/editarPerfil");
                    checkUser(mv, request);
                    mv.addObject("emailInvalid", "E-mail inválido, pois ele já esta em uso!");
                    return mv;
                } else if (loginRequest.getPassword().length() < 8) {
                    ModelAndView mv = new ModelAndView("editarPerfil/editarPerfil");
                    checkUser(mv, request);
                    mv.addObject("lengthPassword", Boolean.TRUE);
                    return mv;
                } else {
                    ModelAndView mv = new ModelAndView("redirect:/perfil");
                    loginRequest.setPassword(getPasswordEncoder().encode(loginRequest.getPassword()));
                    loginRequest.setCodeVerify(null);
                    loginRequest.setAccountActive(ResponseEnums.Y);
                    loginRequest.setCodeVerify(null);
                    loginRequest.setIsPasswordRecover(ResponseEnums.N);
                    if (loginService.findLoginById(UUID.fromString(CookieService.getCookie(request, USER_AUTHENTICATED))).getRole().equals(Roles.ROLE_ADMIN)) {
                        loginRequest.setRole(Roles.ROLE_ADMIN);
                    } else {
                        loginRequest.setRole(Roles.ROLE_USER);
                    }
                    loginService.replaceLogin(loginRequest, UUID.fromString(id));
                    mv.addObject("msg", "Perfil editado com sucesso!");
                    return mv;
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
