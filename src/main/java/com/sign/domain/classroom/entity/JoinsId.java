package com.sign.domain.classroom.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class JoinsId implements Serializable {
    private Long member;
    private Long room;

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
}
