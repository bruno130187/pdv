package br.com.bruno.pdv.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private Long id;

    @NotBlank(message = "Nome obrigatório.")
    private String name;

    @NotBlank(message = "Username obrigatório.")
    private String username;

    @NotBlank(message = "Password obrigatório.")
    private String password;

    private boolean isEnabled;

}
