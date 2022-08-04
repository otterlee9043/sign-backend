package com.sign.member;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Getter
@Setter
public class MemberSignupForm {

    @Size(min = 6, max = 25)
    private String username;

    private String password;

    @Column(unique = true)
    private String email;
    @ManyToMany
}
