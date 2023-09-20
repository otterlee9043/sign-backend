package com.sign.domain.member.entity;

import com.sign.domain.classroom.entity.Joins;
import com.sign.domain.classroom.entity.Room;
import com.sign.domain.member.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_ID")
    private Long id;

    @NotNull
    @Size(min = 2, max = 30)
    private String username;

    private String password;

    @Email
    @Column(unique = true)
    private String email;

    @Column
    private String picture;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private String provider;

    private String refreshToken;


    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @OrderBy("enteredAt desc")
    private List<Joins> joins;

    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JoinColumn(name = "MEMBER_ID")
    private List<Room> hostingRooms;

    public Member update(String username, String picture) {
        this.username = username;
        this.picture = picture;

        return this;
    }

    public Member updateRefreshToken(String newRefreshToken) {
        this.refreshToken = newRefreshToken;
        return this;
    }

    public String getRoleKey() {
        return this.role.getKey();
    }
}
