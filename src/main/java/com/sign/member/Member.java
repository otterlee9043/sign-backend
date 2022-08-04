package com.sign.member;

import com.sign.domain.classroom.Classroom;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@ToString
public class Member {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_ID")
    private Long id;

    @Column(unique = true)
    private String username;

    private String password;

    @Column(unique = true)
    private String email;
    @ManyToMany
    @JoinTable(name = "JOINS",
            joinColumns = @JoinColumn(name = "MEMBER_ID"),
            inverseJoinColumns = @JoinColumn(name = "CLASSROOM_ID"))
    private Set<Classroom> joiningRooms = new HashSet<>();

    public void addJoiningRoom(Classroom classroom){
        joiningRooms.add(classroom);
        classroom.getJoiningMembers().add(this);
    }
}
