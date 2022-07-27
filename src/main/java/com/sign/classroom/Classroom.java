package com.sign.classroom;

import com.sign.member.Member;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
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
    private List<Member> joiningMembers;
}
