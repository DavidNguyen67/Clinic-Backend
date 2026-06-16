package com.camel.clinic.dto.user;

import com.camel.clinic.entity.Role;
import com.camel.clinic.entity.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class UpdateUserDto {

    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String email;

    @Pattern(
        regexp = "^(?!\\s*$).+",
        message = "Full name must not be blank"
    )
    @Size(max = 255, message = "Full name must not exceed 255 characters")
    private String name;

    @Pattern(
        regexp = "^(\\+84|0)[35789][0-9]{8}$",
        message = "Invalid Vietnamese phone number"
    )
    private String phone;


    @JsonFormat(
        shape = JsonFormat.Shape.STRING,
        pattern = "dd/MM/yyyy",
        timezone = "Asia/Ho_Chi_Minh"
    )
    @Past(message = "Date of birth must be in the past")
    private Date dateOfBirth;

    private User.Gender gender;

    private Role.RoleName role;

    private String pathAvatar;
}