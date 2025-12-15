package com.luxury.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDTO {
    private String email;
    private String fullName;
    private String phone;
    private String gender;
    private LocalDate birthDate;
    private  String address;
}