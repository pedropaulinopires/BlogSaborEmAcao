package com.saboremacao.blog.service;

import com.saboremacao.blog.domain.Login;
import com.saboremacao.blog.domain.Revenue;
import com.saboremacao.blog.domain.Section;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public final class PaginationService {

    public static void paginationListSection(Page<Section> page, int currentPage, ModelAndView mv) {
        if (page.getTotalPages() > 1) {
            int currentActualPage = currentPage;
            int sizePages = page.getTotalPages();
            List<Integer> pages = new ArrayList<>();
            if (currentActualPage == 0) {
                if (sizePages == 2) {
                    for (int i = 0; i < sizePages; i++) {
                        currentActualPage = currentActualPage + 1;
                        pages.add(currentActualPage);
                    }
                } else {
                    pages.add(currentActualPage + 1);
                    pages.add(currentActualPage + 2);
                    pages.add(currentActualPage + 3);
                }

            } else if (currentActualPage == sizePages - 1) {
                if (sizePages == 2) {
                    for (int i = 0; i < sizePages; i++) {
                        pages.add(currentActualPage + 1);
                        currentActualPage = currentActualPage - 1;
                    }
                } else {
                    pages.add(currentActualPage + 1);
                    pages.add(currentActualPage);
                    pages.add(currentActualPage - 1);
                }


            } else {
                pages.add(currentActualPage + 1);
                pages.add(currentActualPage);
                pages.add(currentActualPage + 2);
            }
            Collections.sort(pages);
            mv.addObject("pages", pages);
        }
    }

    public static void paginationListLogin(Page<Login> page, int currentPage, ModelAndView mv) {
        if (page.getTotalPages() > 1) {
            int currentActualPage = currentPage;
            int sizePages = page.getTotalPages();
            List<Integer> pages = new ArrayList<>();
            if (currentActualPage == 0) {
                if (sizePages == 2) {
                    for (int i = 0; i < sizePages; i++) {
                        currentActualPage = currentActualPage + 1;
                        pages.add(currentActualPage);
                    }
                } else {
                    pages.add(currentActualPage + 1);
                    pages.add(currentActualPage + 2);
                    pages.add(currentActualPage + 3);
                }

            } else if (currentActualPage == sizePages - 1) {
                if (sizePages == 2) {
                    for (int i = 0; i < sizePages; i++) {
                        pages.add(currentActualPage + 1);
                        currentActualPage = currentActualPage - 1;
                    }
                } else {
                    pages.add(currentActualPage + 1);
                    pages.add(currentActualPage);
                    pages.add(currentActualPage - 1);
                }


            } else {
                pages.add(currentActualPage + 1);
                pages.add(currentActualPage);
                pages.add(currentActualPage + 2);
            }
            Collections.sort(pages);
            mv.addObject("pages", pages);
        }
    }


    public static void paginationListRevenue(Page<Revenue> page, int currentPage, ModelAndView mv) {
        if (page.getTotalPages() > 1) {
            int currentActualPage = currentPage;
            int sizePages = page.getTotalPages();
            List<Integer> pages = new ArrayList<>();
            if (currentActualPage == 0) {
                if (sizePages == 2) {
                    for (int i = 0; i < sizePages; i++) {
                        currentActualPage = currentActualPage + 1;
                        pages.add(currentActualPage);
                    }
                } else {
                    pages.add(currentActualPage + 1);
                    pages.add(currentActualPage + 2);
                    pages.add(currentActualPage + 3);
                }

            } else if (currentActualPage == sizePages - 1) {
                if (sizePages == 2) {
                    for (int i = 0; i < sizePages; i++) {
                        pages.add(currentActualPage + 1);
                        currentActualPage = currentActualPage - 1;
                    }
                } else {
                    pages.add(currentActualPage + 1);
                    pages.add(currentActualPage);
                    pages.add(currentActualPage - 1);
                }


            } else {
                pages.add(currentActualPage + 1);
                pages.add(currentActualPage);
                pages.add(currentActualPage + 2);
            }
            Collections.sort(pages);
            mv.addObject("pages", pages);
        }
    }
}
