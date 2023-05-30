package com.sign.domain.classroom.entity;

import com.sign.domain.member.entity.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.*;

@Getter
@Entity
@ToString
@NoArgsConstructor
public class Room {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CLASSROOM_ID")
    private Long id;

    @NotNull
    private String name;

    @Column(unique = true)
    private String code;

    @OneToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member host;

    @OneToMany(mappedBy = "room", cascade = CascadeType.REMOVE)
    private Set<Joins> joined = new HashSet<>();

    @Builder
    public Room (String name, Member host, String code){
        this.name = name;
        this.host = host;
        this.code = code;
    }

    public void updateRoom(String name){
        this.name = name;
    }
}
