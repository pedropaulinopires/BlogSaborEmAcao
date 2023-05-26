package com.saboremacao.blog.repository;

import com.saboremacao.blog.domain.Section;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SectionRepository extends JpaRepository<Section, UUID> {

    @Query("SELECT s FROM Section s WHERE LOWER(s.name) = :name")
    Section findSectionByName(@Param("name") String name);


    @Query("SELECT s FROM Section s WHERE LOWER(s.name) LIKE CONCAT('%', LOWER(:name), '%')")
    Page<Section> findSectionByNamePageable(@Param("name") String name, Pageable pageable);


}
