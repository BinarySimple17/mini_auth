package ru.binarysimple.auth.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class UserInfo {
    private Long id;
    private String username;
    //    private String email;
//    private String firstName;
//    private String lastName;
    private Set<String> roles;
}