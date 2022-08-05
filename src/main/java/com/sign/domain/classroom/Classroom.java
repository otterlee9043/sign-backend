package com.sign.domain.classroom;

import com.sign.domain.member.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Classroom {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CLASSROOM_ID")
    private Long id;

    private String roomName;

    @OneToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member host;

    @Column(unique = true)
    private String roomCode;

    @ManyToMany(mappedBy = "joiningRooms")
    private Set<Member> joiningMembers = new HashSet<Member>();

    public Classroom (String roomName, Member host, String roomCode){
        this.roomName = roomName;
        this.host = host;
        this.roomCode = roomCode;
    }
}
