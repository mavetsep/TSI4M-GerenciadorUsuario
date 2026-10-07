package br.org.edu.ifrn.GerenciadorUsuario.dto;

import br.org.edu.ifrn.GerenciadorUsuario.model.Perfil;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UsuarioUpdateRequest(
        @NotBlank @Size(max = 120) String nome,
        @NotBlank @Size(max = 80) String usuario,
        @Size(min = 5, max = 100) String senha,
        @NotNull Perfil perfil
) {
}
