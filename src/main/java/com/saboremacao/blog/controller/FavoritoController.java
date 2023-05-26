package com.saboremacao.blog.controller;

import com.saboremacao.blog.domain.Login;
import com.saboremacao.blog.domain.Revenue;
import com.saboremacao.blog.enums.ResponseEnums;
import com.saboremacao.blog.request.*;
import com.saboremacao.blog.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import java.io.UnsupportedEncodingException;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class FavoritoController {

    private final String USER_AUTHENTICATED = "userAuthentication";

    private final LoginService loginService;
    private final RevenueService revenueService;

    private final FavoriteService favoriteService;

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


    @GetMapping("/favorito/{id}")
    public ResponseEntity<Void> favorite(@PathVariable String id, HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        if (CookieService.getCookie(request, USER_AUTHENTICATED) != null && loginService.findLoginById(UUID.fromString(CookieService.getCookie(request, USER_AUTHENTICATED))).getAccountActive().equals(ResponseEnums.Y) && loginService.findLoginById(UUID.fromString(CookieService.getCookie(request, USER_AUTHENTICATED))).getIsPasswordRecover().equals(ResponseEnums.N)) {
            favoriteService.toggleFavorite(id, UUID.fromString(CookieService.getCookie(request, USER_AUTHENTICATED)));
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/favoritos")
    public ModelAndView perfil(@Param("page") Optional<Integer> page, HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
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
                    ModelAndView mv = new ModelAndView("favoritos/favoritos");
                    checkUser(mv, request);
                    int currentPage = page.orElse(1) - 1;
                    if (currentPage < 0) {
                        currentPage = 0;
                    }
                    Page<Revenue> pageRevenue = revenueService.findAllRevenueInLogin(UUID.fromString(CookieService.getCookie(request, USER_AUTHENTICATED)), PageRequest.of(currentPage, 6, Sort.by("name")));
                    if (pageRevenue.getTotalElements() > 0 && currentPage >= pageRevenue.getTotalPages()) {
                        currentPage = pageRevenue.getTotalPages() - 1;
                        pageRevenue = revenueService.findAllRevenueInLogin(UUID.fromString(CookieService.getCookie(request, USER_AUTHENTICATED)), PageRequest.of(currentPage, 6, Sort.by("name")));
                    }
                    if (pageRevenue.getTotalElements() == 0) {
                        mv.addObject("msg", "Você não possui nenhuma receita no favorito!");
                    }
                    mv.addObject("currentPage", currentPage + 1);
                    mv.addObject("list", pageRevenue);
                    PaginationService.paginationListRevenue(pageRevenue, currentPage, mv);
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

    @GetMapping("/favorito/{id}/remover")
    public ModelAndView favoritoRemover(@PathVariable String id, HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
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
                    Revenue revenue = revenueService.findRevenueById(UUID.fromString(id));
                    Login login = loginService.findLoginById(UUID.fromString(CookieService.getCookie(request, USER_AUTHENTICATED)));
                    if (login.getRevenues().contains(revenue)) {
                        login.getRevenues().remove(revenue);
                        LoginRequest loginRequest = new LoginRequest();
                        loginRequest.fromLogin(login);
                        loginService.replaceLogin(loginRequest, UUID.fromString(CookieService.getCookie(request, USER_AUTHENTICATED)));
                        ModelAndView mv = new ModelAndView("redirect:/favoritos");
                        if (!(login.getRevenues().size() == 0)) {
                            mv.addObject("msg", "Receita removida do favorito com sucesso!");
                        }
                        return mv;
                    } else {
                        return new ModelAndView("redirect:/");
                    }
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

    @GetMapping("/favorito/limparTodos")
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
                    ModelAndView mv = new ModelAndView("redirect:/favoritos");
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
