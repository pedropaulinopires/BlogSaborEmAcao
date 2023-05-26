package com.saboremacao.blog.repository;

import com.saboremacao.blog.domain.Revenue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RevenueRepository extends JpaRepository<Revenue, UUID> {

    @Query("SELECT r FROM Revenue r where lower(r.name) LIKE CONCAT('%', LOWER(:name), '%') ")
    Page<Revenue> findAllRevenueByName(@Param("name") String name, Pageable pageable);

    @Query("SELECT r FROM Revenue r where lower(r.section.name) LIKE CONCAT('%', LOWER(:name), '%') ")
    Page<Revenue> findAllRevenueBySection(@Param("name") String name, Pageable pageable);

    @Query("SELECT r FROM Revenue r WHERE LOWER(r.name) LIKE CONCAT('%', LOWER(:name), '%')  OR   LOWER(r.section.name) LIKE CONCAT('%', LOWER(:name), '%')")
    Page<Revenue> findAllRevenueBySectionOrName(@Param("name") String name, Pageable pageable);

    @Query("SELECT r FROM Revenue r WHERE r.name = :name ")
    Revenue findRevenueByName(@Param("name") String name);

    @Query("SELECT r FROM Revenue r JOIN r.logins l WHERE l.id = :loginId")
    Page<Revenue> findRevenuesByLoginId(@Param("loginId") UUID loginId, Pageable pageable);


}
