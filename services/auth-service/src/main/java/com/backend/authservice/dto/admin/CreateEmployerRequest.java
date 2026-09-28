package com.backend.authservice.dto.admin;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateEmployerRequest {

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    private String email;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 8, max = 100, message = "Mật khẩu phải từ 8 đến 100 ký tự")
    private String password;

    @NotBlank(message = "Họ và tên không được để trống")
    @Size(min = 2, max = 100, message = "Họ và tên từ 2 đến 100 ký tự")
    private String fullName;

    private String phone;

    @NotBlank(message = "Tên công ty không được để trống")
    @Size(min = 2, max = 150, message = "Tên công ty từ 2 đến 150 ký tự")
    private String companyName;

    private String companyIndustry;
    private String companyDescription;
    private String companyLogoUrl;
    private String companyWebsiteUrl;
    private String companyAddress;
    private String companySize;
}
