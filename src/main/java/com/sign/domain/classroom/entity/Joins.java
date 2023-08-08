package com.sign.domain.classroom.entity;

import com.sign.domain.member.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;


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

    @Override
    public int hashCode() {
        return Objects.hash(member, room);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        JoinsId joinsId = (JoinsId) obj;
        return Objects.equals(member, joinsId.getMember()) &&
                Objects.equals(room, joinsId.getRoom());
    }

    public void updateEnteredTime() {
        this.enteredAt = LocalDateTime.now();
    }
}
