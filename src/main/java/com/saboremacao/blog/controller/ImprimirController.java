package com.saboremacao.blog.controller;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.saboremacao.blog.domain.Revenue;
import com.saboremacao.blog.enums.ResponseEnums;
import com.saboremacao.blog.request.*;
import com.saboremacao.blog.service.CookieService;
import com.saboremacao.blog.service.LoginService;
import com.saboremacao.blog.service.RevenueService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.util.UUID;


@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class ImprimirController {

    private final RevenueService revenueService;

    private final String USER_AUTHENTICATED = "userAuthentication";

    private final LoginService loginService;

    private final TemplateEngine templateEngine;

    @RequestMapping(path = "/imprimir/{id}")
    public ResponseEntity<?> relatorio(@PathVariable String id, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException {
        if (CookieService.getCookie(request, USER_AUTHENTICATED) != null && loginService.findLoginById(UUID.fromString
                (CookieService.getCookie(request, USER_AUTHENTICATED))).getAccountActive().equals(ResponseEnums.Y) && loginService.findLoginById(UUID.fromString(CookieService.getCookie(request, USER_AUTHENTICATED))).getIsPasswordRecover().equals(ResponseEnums.N)) {
            Revenue revenue = revenueService.findRevenueById(UUID.fromString(id));
            if (revenue != null) {
                Context context = new Context();
                context.setVariable("revenue", revenue);
                String processingHtml = templateEngine.process("imprimir/receita", context);
                ByteArrayOutputStream target = new ByteArrayOutputStream();
                ConverterProperties converterProperties = new ConverterProperties();
                converterProperties.setBaseUri("http://ec2-34-238-239-157.compute-1.amazonaws.com:8080/");
                HtmlConverter.convertToPdf(processingHtml, target, converterProperties);
                byte[] bytes = target.toByteArray();
                return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + revenue.getName() + ".pdf").contentType(MediaType.APPLICATION_PDF).body(bytes);
            }
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
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
