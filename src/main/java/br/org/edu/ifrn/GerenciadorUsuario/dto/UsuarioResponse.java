package br.org.edu.ifrn.GerenciadorUsuario.dto;

import br.org.edu.ifrn.GerenciadorUsuario.model.Perfil;

public record UsuarioResponse(Long id, String nome, String usuario, Perfil perfil) {
}
