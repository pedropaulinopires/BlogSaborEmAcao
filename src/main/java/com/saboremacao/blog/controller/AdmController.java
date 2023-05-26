package com.saboremacao.blog.controller;

import com.saboremacao.blog.domain.Login;
import com.saboremacao.blog.domain.Revenue;
import com.saboremacao.blog.domain.Section;
import com.saboremacao.blog.enums.Roles;
import com.saboremacao.blog.request.*;
import com.saboremacao.blog.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/adm")
@RequiredArgsConstructor
public class AdmController {

    private final LoginService loginService;

    private final SectionService sectionService;

    private final RevenueService revenueService;

    private final CommonMarkService commonMarkService;

    private final String ADM_AUTHENTICATED = "admAuthenticated";

    private BCryptPasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }


    private void admAuthenticated(HttpServletResponse response, HttpServletRequest request, ModelAndView modelAndView) throws UnsupportedEncodingException {
        Login admSession = loginService.findLoginById(UUID.fromString(CookieService.getCookie(request, ADM_AUTHENTICATED)));
        modelAndView.addObject(ADM_AUTHENTICATED, admSession);
    }

    private void listSection(ModelAndView mv) {
        mv.addObject("listSection", sectionService.findAllSection());
    }

    /**
     * redirect page login
     */
    @GetMapping("/login")
    public ModelAndView admLogin() {
        return new ModelAndView("loginAdm/loginAdm");
    }


    /**
     * method for sing in adm
     */
    @PostMapping("/sing-in")
    public ModelAndView admSingIn(@Valid LoginSingInRequest login, BindingResult bindingResult, @Param("remember") String remember, HttpServletResponse response) throws UnsupportedEncodingException {
        if (bindingResult.hasErrors()) {
            return new ModelAndView("loginAdm/loginAdm");
        } else if (login.getEmail().length() < 8) {
            ModelAndView mv = new ModelAndView("loginAdm/loginAdm");
            mv.addObject("usernameLength", Boolean.TRUE);
            return mv;
        } else if (!login.getEmail().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            ModelAndView mv = new ModelAndView("loginAdm/loginAdm");
            mv.addObject("emailInvalid", Boolean.TRUE);
            return mv;
        } else if (login.getPassword().length() < 8) {
            ModelAndView mv = new ModelAndView("loginAdm/loginAdm");
            mv.addObject("passwordLength", Boolean.TRUE);
            return mv;
        } else if (loginService.findLoginByEmail(login.getEmail()) == null) {
            ModelAndView mv = new ModelAndView("loginAdm/loginAdm");
            mv.addObject("userInvalid", Boolean.TRUE);
            return mv;
        } else if ((loginService.findLoginByEmail(login.getEmail()) != null) && !(getPasswordEncoder().matches(login.getPassword(), loginService.findLoginByEmail(login.getEmail()).getPassword()))) {
            ModelAndView mv = new ModelAndView("loginAdm/loginAdm");
            mv.addObject("userInvalid", Boolean.TRUE);
            return mv;
        } else if ((loginService.findLoginByEmail(login.getEmail()) != null) && (getPasswordEncoder().matches(login.getPassword(), loginService.findLoginByEmail(login.getEmail()).getPassword())) && !loginService.findLoginByEmail(login.getEmail()).getRole().equals(Roles.ROLE_ADMIN)) {
            ModelAndView mv = new ModelAndView("loginAdm/loginAdm");
            mv.addObject("notAccess", Boolean.TRUE);
            return mv;
        } else {
            int timeAuthenticated = 60 * 60 * 24;
            if (remember != null) {
                timeAuthenticated = timeAuthenticated * 30;
            }
            CookieService.setCookie(response, ADM_AUTHENTICATED, String.valueOf(loginService.findLoginByEmail(login.getEmail()).getId()), timeAuthenticated);
            return new ModelAndView("redirect:/adm/home");
        }
    }


    /**
     * redirect page home
     */
    @GetMapping("/home")
    public ModelAndView admHome(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ModelAndView mv = new ModelAndView("homeAdm/homeAdm");
        admAuthenticated(response, request, mv);
        return mv;
    }


    /**
     * method redirect page add section
     */
    @GetMapping("/adicionarSessao")
    public ModelAndView adicionarSessao() {
        return new ModelAndView("adicionarSessao/adicionarSessao");
    }


    /**
     * method save section
     */
    @PostMapping("/salvarSessao")
    public ModelAndView salvarSessao(@Valid SectionRequest sectionRequest, BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                return new ModelAndView("adicionarSessao/adicionarSessao");
            } else if (sectionService.findSectionByName(sectionRequest.getName().toLowerCase()) != null) {
                ModelAndView mv = new ModelAndView("adicionarSessao/adicionarSessao");
                mv.addObject("sectionExist", Boolean.TRUE);
                mv.addObject("sectionName", sectionRequest.getName());
                return mv;
            } else {
                sectionRequest.setName(sectionRequest.getName().substring(0, 1).toUpperCase() + sectionRequest.getName().substring(1));
                Section section = sectionService.addSection(sectionRequest);
                ModelAndView mv = new ModelAndView("redirect:/adm/sessao/" + section.getId());
                mv.addObject("msg", "Sessão criada com sucesso!");
                return mv;
            }
        } catch (Exception e) {
            return new ModelAndView("redirect:/adm/home");
        }
    }

    /**
     * method remove section
     */
    @GetMapping("/excluirSessao/{id}")
    public ModelAndView excluirSessao(@PathVariable String id) throws UnsupportedEncodingException {
        try {
            try {
                if (sectionService.findSectionById(UUID.fromString(id)) == null) {
                    ModelAndView mv = new ModelAndView("redirect:/adm/home");
                    mv.addObject("msg", "Erro ao excluir sessão , pois não existe!");
                }
            } catch (Exception e) {
                ModelAndView mv = new ModelAndView("redirect:/adm/home");
                mv.addObject("msg", "Erro ao excluir sessão , pois não existe!");
                return mv;
            }
            sectionService.deleteSection(UUID.fromString(id));
            ModelAndView mv = new ModelAndView("redirect:/adm/listarSessao");
            mv.addObject("msg", "Sessão excluida com sucesso!");
            return mv;
        } catch (Exception e) {
            ModelAndView mv = new ModelAndView("redirect:/adm/home");
            mv.addObject("msg", "Erro ao excluir sessão , pois há receita usando ela!");
            return mv;
        }

    }


    /**
     * method redirect page view section
     */
    @GetMapping("/sessao/{id}")
    public ModelAndView exibirSessao(@PathVariable String id) throws UnsupportedEncodingException {
        try {
            ModelAndView mv = new ModelAndView("exibirSessao/exibirSessao");
            Section section = sectionService.findSectionById(UUID.fromString(id));
            mv.addObject("section", sectionService.findSectionById(UUID.fromString(id)));
            mv.addObject("title", "Sessão: " + section.getName());
            return mv;
        } catch (Exception e) {
            ModelAndView mv = new ModelAndView("redirect:/adm/home");
            mv.addObject("msg", "Essa sessão não existe !");
            return mv;
        }

    }


    /**
     * method redirect page edit section
     */
    @GetMapping("/editarSessao/{id}")
    public ModelAndView editarSessao(@PathVariable String id) throws UnsupportedEncodingException {
        try {
            Section section = sectionService.findSectionById(UUID.fromString(id));
            ModelAndView mv = new ModelAndView("editarSessao/editarSessao");
            mv.addObject("sectionRequest", section);
            mv.addObject("sectionId", section.getId());
            return mv;
        } catch (Exception e) {
            ModelAndView mv = new ModelAndView("redirect:/adm/home");
            mv.addObject("msg", "Essa sessão não existe !");
            return mv;
        }
    }


    /**
     * method edit section
     */
    @PostMapping("/sessao/editar/{id}")
    public ModelAndView sessaoEditar(@Valid SectionRequest sectionRequest, BindingResult bindingResult, @PathVariable String id) throws UnsupportedEncodingException {
        try {
            if (bindingResult.hasErrors()) {
                ModelAndView mv = new ModelAndView("editarSessao/editarSessao");
                mv.addObject("sectionId", UUID.fromString(id));
                return mv;
            } else {
                try {
                    sectionService.replaceSection(sectionRequest, UUID.fromString(id));
                    Section section = sectionService.findSectionById(UUID.fromString(id));
                    ModelAndView mv = new ModelAndView("redirect:/adm/sessao/" + section.getId());
                    mv.addObject("msg", "Sessão editada com sucesso!");
                    return mv;
                } catch (Exception e) {
                    ModelAndView mv = new ModelAndView("redirect:/adm/home");
                    mv.addObject("msg", "Essa sessão não existe !");
                    return mv;
                }

            }
        } catch (Exception e) {
            return new ModelAndView("redirect:/adm/home");
        }

    }

    /**
     * method for list all sections
     * *
     */
    @GetMapping("/listarSessao")
    public ModelAndView listarSessao(@RequestParam("page") Optional<Integer> page) {
        try {
            ModelAndView mv = new ModelAndView("listarSessao/listarSessao");
            int currentPage = page.orElse(1) - 1;
            if (currentPage < 0) {
                currentPage = 0;
            }
            Page<Section> pageSection = sectionService.findAllSectionPageable(PageRequest.of(currentPage, 6, Sort.by("name")));
            if (pageSection.getTotalElements() > 0 && currentPage >= pageSection.getTotalPages()) {
                currentPage = pageSection.getTotalPages() - 1;
                pageSection = sectionService.findAllSectionPageable(PageRequest.of(currentPage, 6, Sort.by("name")));
            }
            mv.addObject("currentPage", currentPage + 1);
            mv.addObject("list", pageSection);
            PaginationService.paginationListSection(pageSection, currentPage, mv);
            return mv;
        } catch (Exception e) {
            return new ModelAndView("redirect:/adm/home");
        }

    }

    /**
     * method search section by name
     */
    @GetMapping("/buscarSessao")
    public ModelAndView buscarSessao(@Valid SectionSearch sectionSearch, BindingResult bindingResult, @RequestParam("page") Optional<Integer> page, HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        try {
            if (bindingResult.hasErrors()) {
                ModelAndView mv = new ModelAndView("homeAdm/homeAdm");
                admAuthenticated(response, request, mv);
                return mv;
            } else {
                ModelAndView mv = new ModelAndView("buscarSessao/buscarSessao");
                int currentPage = page.orElse(1) - 1;
                if (currentPage < 0) {
                    currentPage = 0;
                }
                Page<Section> pageSection = sectionService.findSectionByNamePageable(sectionSearch.getName(), PageRequest.of(currentPage, 6, Sort.by("name")));
                if (pageSection.getTotalElements() > 0 && currentPage >= pageSection.getTotalPages()) {
                    currentPage = pageSection.getTotalPages() - 1;
                    pageSection = sectionService.findSectionByNamePageable(sectionSearch.getName().toLowerCase(), PageRequest.of(currentPage, 6, Sort.by("name")));
                }

                mv.addObject("name", sectionSearch.getName());
                if (pageSection.getTotalElements() == 0) {
                    mv.addObject("msg", Boolean.TRUE);
                }
                mv.addObject("currentPage", currentPage + 1);
                mv.addObject("list", pageSection);
                PaginationService.paginationListSection(pageSection, currentPage, mv);
                return mv;
            }
        } catch (Exception e) {
            return new ModelAndView("redirect:/adm/home");
        }
    }

    /**
     * method list all users
     */
    @GetMapping("/listarUsuarios")
    public ModelAndView listarUsuarios(@RequestParam("page") Optional<Integer> page) {
        try {
            ModelAndView mv = new ModelAndView("listarUsuarios/listarUsuarios");
            int currentPage = page.orElse(1) - 1;
            if (currentPage < 0) {
                currentPage = 0;
            }
            Page<Login> pageLogin = loginService.findAllLogin(PageRequest.of(currentPage, 6, Sort.by("name")));
            if (pageLogin.getTotalElements() > 0 && currentPage >= pageLogin.getTotalPages()) {
                currentPage = pageLogin.getTotalPages() - 1;
                pageLogin = loginService.findAllLogin(PageRequest.of(currentPage, 6, Sort.by("name")));
            }
            mv.addObject("currentPage", currentPage + 1);
            mv.addObject("list", pageLogin);
            PaginationService.paginationListLogin(pageLogin, currentPage, mv);
            return mv;
        } catch (Exception e) {
            return new ModelAndView("redirect:/adm/home");

        }

    }


    /**
     * method logout adm
     */
    @GetMapping("/logout")
    public ModelAndView admLogout(HttpServletResponse response, HttpSession session) throws UnsupportedEncodingException {
        CookieService.setCookie(response, ADM_AUTHENTICATED, "", 0);
        session.invalidate();
        return new ModelAndView("redirect:/");
    }

    /**
     * redirect page create revenue
     */
    @GetMapping("/adicionarReceita")
    public ModelAndView adicionarReceita() {
        ModelAndView mv = new ModelAndView("adicionarReceita/adicionarReceita");
        listSection(mv);
        return mv;
    }

    /**
     * method save revenue
     */
    @PostMapping("/salvarReceita")
    public ModelAndView salvarReceita(@Valid RevenueRequest revenueRequest, BindingResult bindingResult, @Param("file") MultipartFile file, HttpServletRequest request) throws UnsupportedEncodingException {
        try {
            if (bindingResult.hasErrors()) {
                ModelAndView mv = new ModelAndView("adicionarReceita/adicionarReceita");
                if (file.isEmpty()) {
                    mv.addObject("fileNull", Boolean.TRUE);
                }
                listSection(mv);
                return mv;
            } else if (file.isEmpty()) {
                ModelAndView mv = new ModelAndView("adicionarReceita/adicionarReceita");
                mv.addObject("fileNull", Boolean.TRUE);
                listSection(mv);
                return mv;
            } else if (revenueService.findRevenueByName(revenueRequest.getName()) != null) {
                ModelAndView mv = new ModelAndView("adicionarReceita/adicionarReceita");
                mv.addObject("revenueExist", Boolean.TRUE);
                listSection(mv);
                return mv;
            } else {
                LocalDate date = LocalDate.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                revenueRequest.setDatePublication(date.format(formatter));
                Login loginById = loginService.findLoginById(UUID.fromString(CookieService.getCookie(request, ADM_AUTHENTICATED)));
                revenueRequest.setAuthor(loginById);
                String details = commonMarkService.convertToHtml(revenueRequest.getDetails());
                String ingredients = commonMarkService.convertToHtml(revenueRequest.getIngredients());
                String preparation = commonMarkService.convertToHtml(revenueRequest.getPreparation());
                revenueRequest.setDetails(details);
                revenueRequest.setIngredients(ingredients);
                revenueRequest.setPreparation(preparation);
                Revenue revenue = revenueService.saveRevenue(revenueRequest);
                revenue.setImage(revenue.getId() + file.getOriginalFilename());
                revenueRequest.fromRevenue(revenue);
                revenueService.replaceRevenue(revenueRequest, revenue.getId());
                UploadImage.uploadImage(file, String.valueOf(revenue.getId()));
                ModelAndView mv = new ModelAndView("redirect:/adm/exibirReceita/" + String.valueOf(revenue.getId()));
                mv.addObject("msg", "Receita criada com sucesso!");
                return mv;
            }
        } catch (Exception e) {
            return new ModelAndView("redirect:/adm/home");
        }

    }

    @GetMapping("/exibirReceita/{id}")
    public ModelAndView exibirReceita(@PathVariable String id) {
        try {
            ModelAndView mv = new ModelAndView("exibirReceitaAdm/exibirReceitaAdm");
            Revenue revenue = revenueService.findRevenueById(UUID.fromString(id));
            mv.addObject("revenue", revenue);
            return mv;
        } catch (Exception e) {
            ModelAndView mv = new ModelAndView("redirect:/adm/home");
            mv.addObject("msg", "Essa receita não existe !");
            return mv;
        }

    }

    @GetMapping("/listarReceita")
    public ModelAndView listarReceita(@RequestParam("page") Optional<Integer> page) {
        try {
            ModelAndView mv = new ModelAndView("listarReceita/listarReceita");
            int currentPage = page.orElse(1) - 1;
            if (currentPage < 0) {
                currentPage = 0;
            }
            Page<Revenue> listRevenue = revenueService.findAllRevenue(PageRequest.of(currentPage, 6, Sort.by("name")));
            if (listRevenue.getTotalElements() > 0 && currentPage >= listRevenue.getTotalPages()) {
                currentPage = listRevenue.getTotalPages() - 1;
                listRevenue = revenueService.findAllRevenue(PageRequest.of(currentPage, 6, Sort.by("name")));
            }
            mv.addObject("currentPage", currentPage + 1);
            mv.addObject("list", listRevenue);
            PaginationService.paginationListRevenue(listRevenue, currentPage, mv);
            return mv;
        } catch (Exception e) {
            return new ModelAndView("redirect:/adm/home");
        }

    }

    @GetMapping("/editarReceita/{id}")
    public ModelAndView editarReceita(@PathVariable String id, RevenueRequest revenueRequest) {
        try {
            ModelAndView mv = new ModelAndView("editarReceita/editarReceita");
            listSection(mv);
            Revenue revenue = revenueService.findRevenueById(UUID.fromString(id));
            revenue.setDetails(commonMarkService.convertToMarkdown(revenue.getDetails()));
            revenue.setIngredients(commonMarkService.convertHtmlToMarkdown(revenue.getIngredients()));
            revenue.setPreparation(commonMarkService.convertToMarkdown(revenue.getPreparation()));
            revenueRequest.fromRevenue(revenue);
            mv.addObject("revenueId", revenue.getId());
            return mv;
        } catch (Exception e) {
            ModelAndView mvHome = new ModelAndView("redirect:/adm/home");
            mvHome.addObject("msg", "Essa receita não existe !");
            return mvHome;
        }
    }


    @PostMapping("/receita/editar/{id}")
    public ModelAndView receitaEditar(HttpServletRequest request, @Valid RevenueRequest revenueRequest, BindingResult bindingResult, @Param("file") MultipartFile file, @PathVariable String id) throws UnsupportedEncodingException {
        try {
            if (bindingResult.hasErrors()) {
                ModelAndView mv = new ModelAndView("adicionarReceita/adicionarReceita");
                listSection(mv);
                return mv;
            } else {
                LocalDate date = LocalDate.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                revenueRequest.setDatePublication(date.format(formatter));
                Login loginById = loginService.findLoginById(UUID.fromString(CookieService.getCookie(request, ADM_AUTHENTICATED)));
                revenueRequest.setAuthor(loginById);
                String details = commonMarkService.convertToHtml(revenueRequest.getDetails());
                String ingredients = commonMarkService.convertToHtml(revenueRequest.getIngredients());
                String preparation = commonMarkService.convertToHtml(revenueRequest.getPreparation());
                revenueRequest.setDetails(details);
                revenueRequest.setIngredients(ingredients);
                revenueRequest.setPreparation(preparation);
                if (!file.isEmpty()) {
                    UploadImage.deleteImage(revenueService.findRevenueById(UUID.fromString(id)).getImage());
                    revenueRequest.setImage(id + file.getOriginalFilename());
                    UploadImage.uploadImage(file, String.valueOf(id));
                } else {
                    revenueRequest.setImage(revenueService.findRevenueById(UUID.fromString(id)).getImage());
                }
                revenueService.replaceRevenue(revenueRequest, UUID.fromString(id));
                ModelAndView mv = new ModelAndView("redirect:/adm/exibirReceita/" + String.valueOf(id));
                mv.addObject("msg", "Receita editada com sucesso!");
                return mv;
            }
        } catch (Exception e) {
            return new ModelAndView("redirect:/adm/home");
        }

    }

    @GetMapping("/excluirReceita/{id}")
    public ModelAndView excluirReceita(@PathVariable String id) {
        try {
            try {
                ModelAndView mv = new ModelAndView("redirect:/adm/listarReceita");
                Revenue revenueById = revenueService.findRevenueById(UUID.fromString(id));
                UploadImage.deleteImage(revenueById.getImage());
                revenueService.deleteRevenue(revenueById.getId());
                mv.addObject("msg", "Receita removida com sucesso!");
                return mv;
            } catch (Exception e) {
                ModelAndView mvHome = new ModelAndView("redirect:/adm/home");
                mvHome.addObject("msg", "Essa receita não existe !");
                return mvHome;
            }
        } catch (Exception e) {
            return new ModelAndView("redirect:/adm/home");
        }
    }

    @GetMapping("/buscarReceita")
    public ModelAndView buscarReceita(@Valid RevenueSearch revenueSearch, BindingResult bindingResult, @RequestParam("page") Optional<Integer> page, HttpServletResponse response, HttpServletRequest request) {
        try {
            if (bindingResult.hasErrors()) {
                ModelAndView mv = new ModelAndView("homeAdm/homeAdm");
                admAuthenticated(response, request, mv);
                return mv;
            } else {
                ModelAndView mv = new ModelAndView("buscaReceitaAdm/buscaReceitaAdm");
                int currentPage = page.orElse(1) - 1;
                if (currentPage < 0) {
                    currentPage = 0;
                }
                Page<Revenue> pageRevenue = revenueService.findRevenueBySectionOrName(revenueSearch.getName(), PageRequest.of(currentPage, 6, Sort.by("name")));
                if (pageRevenue.getTotalElements() > 0 && currentPage >= pageRevenue.getTotalPages()) {
                    currentPage = pageRevenue.getTotalPages() - 1;
                    pageRevenue = revenueService.findRevenueByName(revenueSearch.getName(), PageRequest.of(currentPage, 6, Sort.by("name")));
                }

                mv.addObject("name", revenueSearch.getName());
                if (pageRevenue.getTotalElements() == 0) {
                    mv.addObject("msg", Boolean.TRUE);
                }
                mv.addObject("currentPage", currentPage + 1);
                mv.addObject("list", pageRevenue);
                PaginationService.paginationListRevenue(pageRevenue, currentPage, mv);

                return mv;
            }
        } catch (Exception e) {
            return new ModelAndView("redirect:/adm/home");
        }
    }


    @ModelAttribute(value = "loginSingInRequest")
    public LoginSingInRequest loginSingInRequest() {
        return new LoginSingInRequest();
    }

    @ModelAttribute(value = "sectionRequest")
    public SectionRequest sectionRequest() {
        return new SectionRequest();
    }

    @ModelAttribute(value = "revenueSearch")
    public RevenueSearch revenueSearch() {
        return new RevenueSearch();
    }

    @ModelAttribute(value = "sectionSearch")
    public SectionSearch sectionSearch() {
        return new SectionSearch();
    }

    @ModelAttribute(value = "revenueRequest")
    public RevenueRequest revenueRequest() {
        return new RevenueRequest();
    }
}
