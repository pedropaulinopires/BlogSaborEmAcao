package com.saboremacao.blog.service;

import com.saboremacao.blog.domain.Section;
import com.saboremacao.blog.exception.SectionNotFoundException;
import com.saboremacao.blog.repository.SectionRepository;
import com.saboremacao.blog.request.SectionRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SectionService {

    private final SectionRepository sectionRepository;

    //add section
    public Section addSection(SectionRequest sectionRequest) {
        return sectionRepository.save(sectionRequest.build());
    }

    // find all section page
    public Page<Section> findAllSectionPageable(Pageable pageable) {
        return sectionRepository.findAll(pageable);
    }

    // find all section
    public List<Section> findAllSection() {
        return sectionRepository.findAll();
    }

    //find by id
    public Section findSectionById(UUID id) {
        return sectionRepository.findById(id).orElseThrow(() -> new SectionNotFoundException("Section not found by id"));
    }

    //find by name
    public Section findSectionByName(String name) {
        return sectionRepository.findSectionByName(name);
    }


    //find by name pageable
    public Page<Section> findSectionByNamePageable(String name, Pageable pageable) {
        return sectionRepository.findSectionByNamePageable(name, pageable);
    }

    //update section
    public void replaceSection(SectionRequest sectionRequest, UUID id) {
        findSectionById(id);
        sectionRepository.save(sectionRequest.build(sectionRequest, id));
    }

    //delete section
    public void deleteSection(UUID id) {
        sectionRepository.delete(findSectionById(id));
    }

}
