package com.saboremacao.blog.controller;

import com.saboremacao.blog.domain.Login;
import com.saboremacao.blog.domain.Revenue;
import com.saboremacao.blog.enums.ResponseEnums;
import com.saboremacao.blog.request.*;
import com.saboremacao.blog.service.CookieService;
import com.saboremacao.blog.service.LoginService;
import com.saboremacao.blog.service.PaginationService;
import com.saboremacao.blog.service.RevenueService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.io.UnsupportedEncodingException;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class BlogController {

    private final RevenueService revenueService;

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


    @GetMapping
    public ModelAndView home(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        ModelAndView mv = new ModelAndView("home/home");
        checkUser(mv, request);
        mv.addObject("listRecent", revenueService.findAllRevenue(PageRequest.of(0, 4, Sort.by(Sort.Direction.DESC, "datePublication"))));
        mv.addObject("listBolos", revenueService.findRevenueBySection("Bolos", PageRequest.of(0, 4, Sort.by("name"))));
        mv.addObject("listAcompanhamentos", revenueService.findRevenueBySection("Acompanhamentos", PageRequest.of(0, 4, Sort.by("name"))));
        mv.addObject("listDoces", revenueService.findRevenueBySection("Doces", PageRequest.of(0, 4, Sort.by("name"))));
        mv.addObject("listPeixes", revenueService.findRevenueBySection("Peixes", PageRequest.of(0, 4, Sort.by("name"))));
        mv.addObject("listSobremesas", revenueService.findRevenueBySection("Sobremesas", PageRequest.of(0, 4, Sort.by("name"))));
        return mv;

    }

    @GetMapping("/***")
    public ModelAndView errors() {
        return new ModelAndView("redirect:/");
    }

    @GetMapping("/sobre")
    public ModelAndView sobre(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new ModelAndView("sobre/sobre");
        checkUser(mv, request);
        return mv;
    }

    @GetMapping("/buscarReceita")
    public ModelAndView buscarReceita(@Valid RevenueSearch revenueSearch, BindingResult bindingResult, @Param("page") Optional<Integer> page, HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        try {
            if (bindingResult.hasErrors()) {
                return home(request, response);
            } else {
                ModelAndView mv = new ModelAndView("buscaReceita/buscaReceita");
                checkUser(mv, request);
                mv.addObject("name", revenueSearch.getName());
                int currentPage = page.orElse(1) - 1;
                if (currentPage < 0) {
                    currentPage = 0;
                }
                Page<Revenue> pageRevenue = revenueService.findRevenueBySectionOrName(revenueSearch.getName(), PageRequest.of(currentPage, 6, Sort.by("name")));
                if (pageRevenue.getTotalElements() > 0 && currentPage >= pageRevenue.getTotalPages()) {
                    currentPage = pageRevenue.getTotalPages() - 1;
                    pageRevenue = revenueService.findRevenueByName(revenueSearch.getName(), PageRequest.of(currentPage, 6, Sort.by("name")));
                }

                if (pageRevenue.getTotalElements() == 0) {
                    mv.addObject("msg", Boolean.TRUE);
                }
                mv.addObject("currentPage", currentPage + 1);
                mv.addObject("list", pageRevenue);
                PaginationService.paginationListRevenue(pageRevenue, currentPage, mv);
                return mv;
            }
        } catch (Exception e) {
            return home(request, response);
        }

    }

    @GetMapping("/listarReceitas")
    public ModelAndView listarReceitas(@Param("page") Optional<Integer> page, HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        try {
            ModelAndView mv = new ModelAndView("listarReceitaHome/listarReceitaHome");
            checkUser(mv, request);
            int currentPage = page.orElse(1) - 1;
            if (currentPage < 0) {
                currentPage = 0;
            }
            Page<Revenue> pageRevenue = revenueService.findAllRevenue(PageRequest.of(currentPage, 6, Sort.by(Sort.Direction.DESC, "datePublication")));
            if (pageRevenue.getTotalElements() > 0 && currentPage >= pageRevenue.getTotalPages()) {
                currentPage = pageRevenue.getTotalPages() - 1;
                pageRevenue = revenueService.findAllRevenue(PageRequest.of(currentPage, 6, Sort.by(Sort.Direction.DESC, "datePublication")));
            }

            mv.addObject("currentPage", currentPage + 1);
            mv.addObject("list", pageRevenue);
            PaginationService.paginationListRevenue(pageRevenue, currentPage, mv);
            return mv;
        } catch (Exception e) {
            return home(request, response);
        }

    }

    @GetMapping("/exibirReceita/{id}")
    public ModelAndView exibirReceita(@PathVariable String id, HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        try {
            try {
                ModelAndView mv = new ModelAndView("detalheReceita/detalheReceita");
                checkUser(mv, request);
                Revenue revenue = revenueService.findRevenueById(UUID.fromString(id));
                mv.addObject("revenue", revenue);
                return mv;
            } catch (Exception e) {
                return home(request, response);
            }
        } catch (Exception e) {
            return home(request, response);
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
