package com.saboremacao.blog.config;

import com.saboremacao.blog.interceptors.AdmInterceptors;
import com.saboremacao.blog.interceptors.LoginInterceptors;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final String[] EXCLUDE_PATHS = {"/font/**",
            "/images-project/**"
            , "/images-revenue/**"
            , "/js/**"
            , "/styles/**"
            , "/adm/login"
            , "/adm/sing-in"
            , "/"
            , "/sobre"
            , "/buscarReceita"
            , "/listarReceitas"
            , "/exibirReceita/**"
            , "/cadastro"
            , "/cadastro/criarConta"
            , "/login"
            , "/login/sing-in"
            , "/esqueceu"
            , "/esqueceu/enviarEmail"};


    private final String[] ADD_PATHS_ADM = {"/adm/**"};
    private final String[] ADD_PATHS_USER = {"/cadastro/reenviarVerificao/**"
            , "/cadastro/verificar/**"
            , "/cadastro/editar/**"
            , "/cadastro/atualizar/**"
            , "/cadastro/reenviarVerificao/**"
            , "/esqueceu/reenviarVerificao/**"
            , "/esqueceu/recuperarSenha/**"
            , "/esqueceu/redefinirSenha/**"
            , "/logout"
            , "/favorito/**"
            , "/perfil/**"
            , "/imprimir/**"
            , "/favoritos"};


    @Override
    public void addInterceptors(InterceptorRegistry registry) {


        registry.addInterceptor(new AdmInterceptors()).excludePathPatterns(EXCLUDE_PATHS).addPathPatterns(ADD_PATHS_ADM);

        registry.addInterceptor(new LoginInterceptors()).excludePathPatterns(EXCLUDE_PATHS).addPathPatterns(ADD_PATHS_USER);


    }


}
