package com.saboremacao.blog.service;

import com.saboremacao.blog.domain.Revenue;
import com.saboremacao.blog.exception.RevenueNotFoundException;
import com.saboremacao.blog.repository.RevenueRepository;
import com.saboremacao.blog.request.RevenueRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RevenueService {

    private final RevenueRepository revenueRepository;

    //add revenue
    public Revenue saveRevenue(RevenueRequest revenueRequest) {
        return revenueRepository.save(revenueRequest.build());
    }

    //find by name
    public Page<Revenue> findRevenueByName(String name, Pageable pageable) {
        return revenueRepository.findAllRevenueByName(name, pageable);
    }

    //find revenue by login
    public Page<Revenue> findAllRevenueInLogin(UUID id, Pageable pageable) {
        return revenueRepository.findRevenuesByLoginId(id, pageable);
    }

    //find by section
    public Page<Revenue> findRevenueBySection(String name, Pageable pageable) {
        return revenueRepository.findAllRevenueBySection(name, pageable);
    }

    //find all
    public Page<Revenue> findAllRevenue(Pageable pageable) {
        return revenueRepository.findAll(pageable);
    }


    //find by section
    public Page<Revenue> findRevenueBySectionOrName(String name, Pageable pageable) {
        List<String> names = Arrays.asList(name.split(" "));
        Page<Revenue> revenues = revenueRepository.findAllRevenueBySectionOrName(name, pageable);
        if (revenues.getTotalElements() == 0) {
            for (int i = 0; i < names.size(); i++) {
                if (!(names.get(i).equalsIgnoreCase("de"))) {
                    revenues = revenueRepository.findAllRevenueBySectionOrName(names.get(i), pageable);
                }
                if (revenues.getTotalElements() > 0) {
                    return revenues;
                }
            }
        }
        return revenues;
    }

    //find by name
    public Revenue findRevenueByName(String name) {
        return revenueRepository.findRevenueByName(name);
    }

    //find by id
    public Revenue findRevenueById(UUID id) {
        return revenueRepository.findById(id).orElseThrow(() -> new RevenueNotFoundException("Revenue not found by id"));
    }

    //delete revenue
    public void deleteRevenue(UUID id) {
        revenueRepository.delete(findRevenueById(id));
    }

    //replace revenue
    public void replaceRevenue(RevenueRequest revenueRequest, UUID id) {
        revenueRepository.save(revenueRequest.build(revenueRequest, id));
    }
}
