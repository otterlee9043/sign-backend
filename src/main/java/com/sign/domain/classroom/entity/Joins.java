package com.sign.domain.classroom.entity;

import com.sign.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;


@Getter
@Entity
@NoArgsConstructor
@IdClass(JoinsId.class)
public class Joins extends BaseEntity {
    @Id
    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @Id
    @ManyToOne
    @JoinColumn(name = "ROOM_ID")
    private Room room;
    @NotNull
    private LocalDateTime enteredAt;

    public Joins(Member member, Room room) {
        this.member = member;
        this.room = room;
        this.enteredAt = LocalDateTime.now();
    }

    public void updateEnteredTime(){
        this.enteredAt = LocalDateTime.now();
    }
}
