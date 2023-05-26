package com.saboremacao.blog.domain;

import com.saboremacao.blog.enums.ResponseEnums;
import com.saboremacao.blog.enums.Roles;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
public class Login {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, unique = true, length = 200)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private Roles role;

    @Enumerated(EnumType.STRING)
    private ResponseEnums accountActive;

    @Enumerated(EnumType.STRING)
    private ResponseEnums isPasswordRecover;

    @Column(length = 6)
    private Integer codeVerify;

    @ManyToMany
    @JoinTable(name = "login_revenues", joinColumns = {@JoinColumn(name = "login_id", referencedColumnName = "id")}, inverseJoinColumns = {@JoinColumn(name = "revenues.id", referencedColumnName = "id")})
    private List<Revenue> revenues;


}
