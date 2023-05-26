package com.saboremacao.blog.repository;

import com.saboremacao.blog.domain.Login;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LoginRepository extends JpaRepository<Login, UUID> {

    @Query("SELECT l FROM Login l where l.email = :email")
    Login findByEmail(@Param("email") String email);

}
