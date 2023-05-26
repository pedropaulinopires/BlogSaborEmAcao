package com.saboremacao.blog.controller;

import com.saboremacao.blog.domain.Login;
import com.saboremacao.blog.enums.ResponseEnums;
import com.saboremacao.blog.enums.Roles;
import com.saboremacao.blog.model.EmailModel;
import com.saboremacao.blog.request.*;
import com.saboremacao.blog.service.CookieService;
import com.saboremacao.blog.service.EmailService;
import com.saboremacao.blog.service.LoginService;
import jakarta.mail.MessagingException;
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

@RequiredArgsConstructor
@Controller
@RequestMapping("/cadastro")
public class CadastroController {

    private final String USER_AUTHENTICATED = "userAuthentication";

    private final String EMAIL_BLOG = "sabor.em.acao@gmail.com";
    private final EmailService emailService;

    private final LoginService loginService;

    private BCryptPasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @GetMapping
    public ModelAndView cadastro(HttpServletRequest request, HttpServletResponse response) {
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
                return new ModelAndView("cadastro/cadastro");
            }
        } catch (Exception e) {
            return new ModelAndView("cadastro/cadastro");
        }
    }


    @PostMapping("/criarConta")
    public ModelAndView criarConta(@Valid LoginRequest loginRequest, BindingResult bindingResult, HttpServletRequest request, HttpServletResponse response) throws MessagingException, UnsupportedEncodingException {
        try {
            if (bindingResult.hasErrors()) {
                ModelAndView mv = new ModelAndView("cadastro/cadastro");
                return mv;
            } else if (loginRequest.getName().length() < 2) {
                ModelAndView mv = new ModelAndView("cadastro/cadastro");
                mv.addObject("lengthName", Boolean.TRUE);
                return mv;
            } else if (!loginRequest.getEmail().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                ModelAndView mv = new ModelAndView("cadastro/cadastro");
                mv.addObject("emailInvalid", "E-mail inválido!");
                return mv;
            } else if (loginRequest.getEmail().length() < 8) {
                ModelAndView mv = new ModelAndView("cadastro/cadastro");
                mv.addObject("lengthEmail", Boolean.TRUE);
                return mv;
            } else if (loginService.findLoginByEmail(loginRequest.getEmail()) != null) {
                ModelAndView mv = new ModelAndView("cadastro/cadastro");
                mv.addObject("emailInvalid", "E-mail inválido, pois ele já esta em uso!");
                return mv;
            } else if (loginRequest.getPassword().length() < 8) {
                ModelAndView mv = new ModelAndView("cadastro/cadastro");
                mv.addObject("lengthPassword", Boolean.TRUE);
                return mv;
            } else {
                //success user , and verification
                int codeVerification = new Random().nextInt(900000) + 100000;
                loginRequest.setAccountActive(ResponseEnums.N);
                loginRequest.setIsPasswordRecover(ResponseEnums.N);
                loginRequest.setRole(Roles.ROLE_USER);
                loginRequest.setCodeVerify(codeVerification);
                loginRequest.setPassword(getPasswordEncoder().encode(loginRequest.getPassword()));
                //send code
                String text = "Olá, " + loginRequest.getName() + " segue seu código de verificação => " + loginRequest.getCodeVerify() + " .";
                emailService.sendEmail(new EmailModel(EMAIL_BLOG, loginRequest.getEmail(), "Verificação da conta!", text));
                Login login = loginService.saveLogin(loginRequest);
                ModelAndView mv = new ModelAndView("verificarCadastro/verificarCadastro");
                mv.addObject("user", login);
                CookieService.setCookie(response, USER_AUTHENTICATED, String.valueOf(login.getId()), 60 * 60 * 24);
                return mv;
            }

        } catch (Exception e) {
            return new ModelAndView("redirect:/");
        }

    }


    @PostMapping("/verificar/{id}")
    public ModelAndView verificar(@Valid CodeRequest codeRequest, BindingResult bindingResult, @PathVariable String id, HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        try {
            if (loginService.findLoginById(UUID.fromString(id)).getAccountActive().equals(ResponseEnums.N)) {
                if (bindingResult.hasErrors()) {
                    ModelAndView mv = new ModelAndView("verificarCadastro/verificarCadastro");
                    mv.addObject("user", loginService.findLoginById(UUID.fromString(id)));
                    return mv;
                } else if (!codeRequest.getCodeVerify().equals(loginService.findLoginById(UUID.fromString(id)).getCodeVerify())) {
                    ModelAndView mv = new ModelAndView("verificarCadastro/verificarCadastro");
                    mv.addObject("user", loginService.findLoginById(UUID.fromString(id)));
                    mv.addObject("codeInvalid", Boolean.TRUE);
                    return mv;
                } else {
                    Login login = loginService.findLoginById(UUID.fromString(id));
                    login.setAccountActive(ResponseEnums.Y);
                    login.setIsPasswordRecover(ResponseEnums.N);
                    login.setCodeVerify(null);
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

    @GetMapping("/reenviarVerificao/{id}")
    public ModelAndView reenviarVerificao(@PathVariable String id, HttpServletRequest request, HttpServletResponse response) {
        try {
            if (loginService.findLoginById(UUID.fromString(id)).getAccountActive().equals(ResponseEnums.N)) {
                int codeVerification = new Random().nextInt(900000) + 100000;
                Login login = loginService.findLoginById(UUID.fromString(id));
                login.setCodeVerify(codeVerification);
                LoginRequest loginRequest = new LoginRequest();
                loginRequest.fromLogin(login);
                loginService.replaceLogin(loginRequest, UUID.fromString(id));
                String text = "Olá, " + loginRequest.getName() + " segue seu código de verificação => " + loginRequest.getCodeVerify() + " .";
                emailService.sendEmail(new EmailModel(EMAIL_BLOG, loginRequest.getEmail(), "Verificação da conta!", text));
                ModelAndView mv = new ModelAndView("verificarCadastro/verificarCadastro");
                mv.addObject("user", login);
                return mv;
            } else {
                return new ModelAndView("redirect:/");
            }
        } catch (Exception e) {
            return new ModelAndView("redirect:/");
        }
    }

    @GetMapping("/editar/{id}")
    public ModelAndView editar(@PathVariable String id, LoginEdit loginEdit, HttpServletRequest request, HttpServletResponse response) {
        try {
            if (!loginService.findLoginById(UUID.fromString(id)).getAccountActive().equals(ResponseEnums.Y)) {
                ModelAndView mv = new ModelAndView("editarCadastro/editarCadastro");
                Login login = loginService.findLoginById(UUID.fromString(id));
                loginEdit.fromLogin(login);
                mv.addObject("userId", login.getId());
                return mv;
            } else {
                return new ModelAndView("redirect:/");
            }
        } catch (Exception e) {
            return new ModelAndView("redirect:/");
        }
    }

    @PostMapping("/atualizar/{id}")
    public ModelAndView atualizar(@PathVariable String id, @Valid LoginEdit loginEdit, BindingResult bindingResult, HttpServletRequest request, HttpServletResponse response) {
        try {
            if (!loginService.findLoginById(UUID.fromString(id)).getAccountActive().equals(ResponseEnums.Y)) {
                if (bindingResult.hasErrors()) {
                    ModelAndView mv = new ModelAndView("editarCadastro/editarCadastro");
                    Login login = loginService.findLoginById(UUID.fromString(id));
                    loginEdit.fromLogin(login);
                    mv.addObject("userId", login.getId());
                    return mv;
                } else if (!loginEdit.getEmail().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                    ModelAndView mv = new ModelAndView("editarCadastro/editarCadastro");
                    Login login = loginService.findLoginById(UUID.fromString(id));
                    loginEdit.fromLogin(login);
                    mv.addObject("userId", login.getId());
                    mv.addObject("emailInvalid", "E-mail inválido!");
                    return mv;
                } else if (loginEdit.getEmail().length() < 8) {
                    ModelAndView mv = new ModelAndView("editarCadastro/editarCadastro");
                    Login login = loginService.findLoginById(UUID.fromString(id));
                    loginEdit.fromLogin(login);
                    mv.addObject("userId", login.getId());
                    mv.addObject("lengthEmail", Boolean.TRUE);
                    return mv;
                } else if (loginService.findLoginByEmail(loginEdit.getEmail()) != null && loginService.findLoginByEmail(loginEdit.getEmail()).getEmail() != loginService.findLoginById(UUID.fromString(id)).getEmail()) {
                    ModelAndView mv = new ModelAndView("editarCadastro/editarCadastro");
                    Login login = loginService.findLoginById(UUID.fromString(id));
                    login.setEmail(loginEdit.getEmail());
                    loginEdit.fromLogin(login);
                    mv.addObject("userId", login.getId());
                    mv.addObject("emailInvalid", "E-mail inválido, pois ele já esta em uso!");
                    return mv;
                } else {
                    //success user , and verification
                    int codeVerification = new Random().nextInt(900000) + 100000;
                    Login login = loginService.findLoginById(UUID.fromString(id));
                    login.setEmail(loginEdit.getEmail());
                    LoginRequest loginRequest = new LoginRequest();
                    loginRequest.fromLogin(login);
                    loginRequest.setAccountActive(ResponseEnums.N);
                    loginRequest.setIsPasswordRecover(ResponseEnums.N);
                    loginRequest.setCodeVerify(codeVerification);
                    loginService.replaceLogin(loginRequest, UUID.fromString(id));
                    //send code
                    String text = "Olá, " + loginRequest.getName() + " segue seu código de verificação => " + loginRequest.getCodeVerify() + " .";
                    emailService.sendEmail(new EmailModel(EMAIL_BLOG, loginRequest.getEmail(), "Verificação da conta!", text));
                    ModelAndView mv = new ModelAndView("verificarCadastro/verificarCadastro");
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
