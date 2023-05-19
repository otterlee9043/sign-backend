package com.sign.domain.member.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sign.domain.classroom.entity.Room;
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
public class Member {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_ID")
    private Long id;

    @NotNull
    @Column(unique = true)
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

    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "JOINS",
            joinColumns = @JoinColumn(name = "MEMBER_ID"),
            inverseJoinColumns = @JoinColumn(name = "CLASSROOM_ID"))
    private Set<Room> joiningRooms = new HashSet<>();

    public void addJoiningRoom(Room classroom){
        joiningRooms.add(classroom);
        classroom.getJoiningMembers().add(this);
    }

    public Member update(String username, String picture) {
        this.username = username;
        this.picture = picture;

        return this;
    }

    public String getRoleKey(){
        return this.role.getKey();
    }
}
