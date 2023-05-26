package com.saboremacao.blog.controller;

import com.saboremacao.blog.domain.Login;
import com.saboremacao.blog.enums.ResponseEnums;
import com.saboremacao.blog.model.EmailModel;
import com.saboremacao.blog.request.*;
import com.saboremacao.blog.service.CookieService;
import com.saboremacao.blog.service.EmailService;
import com.saboremacao.blog.service.LoginService;
import com.saboremacao.blog.service.RevenueService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.UnsupportedEncodingException;
import java.util.Random;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/esqueceu")
public class EsqueceuController {

    private final String USER_AUTHENTICATED = "userAuthentication";

    private final String EMAIL_BLOG = "sabor.em.acao@gmail.com";
    private final RevenueService revenueService;
    private final EmailService emailService;

    private final LoginService loginService;


    private BCryptPasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @GetMapping
    public ModelAndView esqueceu(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        try {
            String cookie = CookieService.getCookie(request, USER_AUTHENTICATED);
            if (cookie != null && loginService.findLoginById(UUID.fromString(cookie)).getAccountActive().equals(ResponseEnums.N)) {
                ModelAndView mv = new ModelAndView("verificarCadastro/verificarCadastro");
                mv.addObject("user", loginService.findLoginByEmail(loginService.findLoginById(UUID.fromString(cookie)).getEmail()));
                return mv;
            } else if (cookie != null) {
                return new ModelAndView("redirect:/");
            } else {
                return new ModelAndView("esqueceu/esqueceu");
            }
        } catch (Exception e) {
            return new ModelAndView("esqueceu/esqueceu");
        }
    }

    @PostMapping("/enviarEmail")
    public ModelAndView recuperacao(@Valid LoginForgot loginForgot, BindingResult bindingResult, HttpServletResponse response) {
        try {

            if (bindingResult.hasErrors()) {
                ModelAndView mv = new ModelAndView("esqueceu/esqueceu");
                return mv;
            } else if (!loginForgot.getEmail().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                ModelAndView mv = new ModelAndView("esqueceu/esqueceu");
                mv.addObject("emailInvalid", "E-mail inválido!");
                return mv;
            } else if (loginForgot.getEmail().length() < 8) {
                ModelAndView mv = new ModelAndView("esqueceu/esqueceu");
                mv.addObject("lengthEmail", Boolean.TRUE);
                return mv;
            } else if (loginService.findLoginByEmail(loginForgot.getEmail()) == null) {
                ModelAndView mv = new ModelAndView("esqueceu/esqueceu");
                mv.addObject("emailInvalid", "E-mail inválido!");
                return mv;
            } else if (loginService.findLoginByEmail(loginForgot.getEmail()).getAccountActive().equals(ResponseEnums.N)) {
                ModelAndView mv = new ModelAndView("verificarCadastro/verificarCadastro");
                mv.addObject("user", loginService.findLoginByEmail(loginService.findLoginByEmail(loginForgot.getEmail()).getEmail()));
                int timeAuthenticated = 60 * 60 * 24;
                CookieService.setCookie(response, USER_AUTHENTICATED, String.valueOf(loginService.findLoginByEmail(loginService.findLoginByEmail(loginForgot.getEmail()).getEmail()).getId()), timeAuthenticated);
                return mv;
            } else {
                //success user , and verification
                int codeVerification = new Random().nextInt(900000) + 100000;
                Login login = loginService.findLoginByEmail(loginForgot.getEmail());
                login.setCodeVerify(codeVerification);
                login.setIsPasswordRecover(ResponseEnums.Y);
                LoginRequest loginRequest = new LoginRequest();
                loginRequest.fromLogin(login);
                loginService.replaceLogin(loginRequest, login.getId());
                String text = "Olá, " + loginRequest.getName() + " segue seu código de verificação  para recuperar sua senha => " + loginRequest.getCodeVerify() + " .";
                emailService.sendEmail(new EmailModel(EMAIL_BLOG, loginRequest.getEmail(), "Verificação da conta!", text));
                ModelAndView mv = new ModelAndView("verificarEsqueceu/verificarEsqueceu");
                mv.addObject("user", login);
                CookieService.setCookie(response, USER_AUTHENTICATED, String.valueOf(login.getId()), 60 * 60 * 24);
                return mv;
            }


        } catch (Exception e) {
            return new ModelAndView("redirect:/");
        }
    }

    @GetMapping("/reenviarVerificao/{id}")
    public ModelAndView reenviarVerificaoSenha(@PathVariable String id, HttpServletRequest request, HttpServletResponse response) {
        try {
            if (loginService.findLoginById(UUID.fromString(id)).getIsPasswordRecover().equals(ResponseEnums.Y)) {
                int codeVerification = new Random().nextInt(900000) + 100000;
                Login login = loginService.findLoginById(UUID.fromString(id));
                login.setCodeVerify(codeVerification);
                LoginRequest loginRequest = new LoginRequest();
                loginRequest.fromLogin(login);
                loginService.replaceLogin(loginRequest, UUID.fromString(id));
                String text = "Olá, " + loginRequest.getName() + " segue seu código de verificação para recuperação de senha => " + loginRequest.getCodeVerify() + " .";
                emailService.sendEmail(new EmailModel(EMAIL_BLOG, loginRequest.getEmail(), "Verificação da conta!", text));
                ModelAndView mv = new ModelAndView("verificarEsqueceu/verificarEsqueceu");
                mv.addObject("user", login);
                return mv;
            } else {
                return new ModelAndView("redirect:/");
            }
        } catch (Exception e) {
            return new ModelAndView("redirect:/");
        }
    }

    @PostMapping("/recuperarSenha/{id}")
    public ModelAndView verificarEmail(@Valid CodeRequest codeRequest, BindingResult bindingResult, @PathVariable String id, HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        try {
            if (loginService.findLoginById(UUID.fromString(id)).getIsPasswordRecover().equals(ResponseEnums.Y)) {
                if (bindingResult.hasErrors()) {
                    ModelAndView mv = new ModelAndView("verificarEsqueceu/verificarEsqueceu");
                    mv.addObject("user", loginService.findLoginById(UUID.fromString(id)));
                    return mv;
                } else if (!codeRequest.getCodeVerify().equals(loginService.findLoginById(UUID.fromString(id)).getCodeVerify())) {
                    ModelAndView mv = new ModelAndView("verificarEsqueceu/verificarEsqueceu");
                    mv.addObject("user", loginService.findLoginById(UUID.fromString(id)));
                    mv.addObject("codeInvalid", Boolean.TRUE);
                    return mv;
                } else {
                    ModelAndView mv = new ModelAndView("mudarSenha/mudarSenha");
                    Login login = loginService.findLoginById(UUID.fromString(id));
                    login.setCodeVerify(null);
                    LoginRequest loginRequest = new LoginRequest();
                    loginRequest.fromLogin(login);
                    loginService.replaceLogin(loginRequest, UUID.fromString(id));
                    mv.addObject("user", login);
                    return mv;
                }
            } else {
                return new ModelAndView("redirect:/");
            }
        } catch (Exception e) {
            return new ModelAndView("redirect:/");
        }
    }

    @PostMapping("/redefinirSenha/{id}")
    public ModelAndView redefinirSenha(@Valid PasswordEdit passwordEdit, BindingResult bindingResult, @PathVariable String id) {
        try {
            if (loginService.findLoginById(UUID.fromString(id)).getIsPasswordRecover().equals(ResponseEnums.Y) && loginService.findLoginById(UUID.fromString(id)).getCodeVerify() == null) {
                if (bindingResult.hasErrors()) {
                    ModelAndView mv = new ModelAndView("mudarSenha/mudarSenha");
                    mv.addObject("user", loginService.findLoginById(UUID.fromString(id)));
                    return mv;
                } else if (passwordEdit.getPassword().length() < 8) {
                    ModelAndView mv = new ModelAndView("mudarSenha/mudarSenha");
                    mv.addObject("user", loginService.findLoginById(UUID.fromString(id)));
                    mv.addObject("lengthPassword", Boolean.TRUE);
                    return mv;
                } else {
                    Login login = loginService.findLoginById(UUID.fromString(id));
                    login.setPassword(getPasswordEncoder().encode(passwordEdit.getPassword()));
                    login.setCodeVerify(null);
                    login.setIsPasswordRecover(ResponseEnums.N);
                    LoginRequest loginRequest = new LoginRequest();
                    loginRequest.fromLogin(login);
                    loginService.replaceLogin(loginRequest, UUID.fromString(id));
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
