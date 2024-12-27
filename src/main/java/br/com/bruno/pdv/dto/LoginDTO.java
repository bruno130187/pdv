package br.com.bruno.pdv.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTO {

    @NotBlank(message = "O campo Login é obrigatório.")
    private String username;

    @NotBlank(message = "O campo senha é obrigatório.")
    private String password;

}