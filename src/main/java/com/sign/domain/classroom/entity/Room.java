package com.sign.domain.classroom.entity;

import com.sign.domain.member.entity.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Getter
@Entity
@NoArgsConstructor
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ROOM_ID")
    private Long id;

    @NotNull
    private String name;

    @Column(unique = true)
    private String code;

    @Min(value = 1)
    @Max(value = 100)
    private Integer capacity;

    @ManyToOne
    @JoinColumn(name = "MEMBER_ID", updatable = false)
    private Member host;

    @OneToMany(mappedBy = "room", cascade = CascadeType.REMOVE)
    private Set<Joins> joined = new HashSet<>();

    @Builder
    public Room(String name, Member host, String code, Integer capacity) {
        this.name = name;
        this.host = host;
        this.code = code;
        this.capacity = capacity;
    }

    public void updateRoom(String name) {
        this.name = name;
    }
}
