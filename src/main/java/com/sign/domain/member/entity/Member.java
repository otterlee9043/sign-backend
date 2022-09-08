package com.sign.domain.member.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sign.domain.classroom.entity.Room;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
public class Member {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_ID")
    private Long id;

    @Column(unique = true)
    private String username;

    private String password;

    @Column(unique = true)
    private String email;

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
}
