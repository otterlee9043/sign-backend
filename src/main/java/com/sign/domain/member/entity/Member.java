package com.sign.domain.member.entity;

import com.sign.domain.classroom.entity.Joins;
import com.sign.domain.member.Role;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString(of = {"id", "username", "email", "role"})
public class Member {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_ID")
    private Long id;

    @NotNull
    private String username;

    private String password;

    @NotNull
    @Column(unique = true)
    private String email;

    @Column
    private String picture;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private String provider;


    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
    private Set<Joins> joins = new HashSet<>();

    public Member update(String username, String picture) {
        this.username = username;
        this.picture = picture;

        return this;
    }

    public String getRoleKey(){
        return this.role.getKey();
    }
}
