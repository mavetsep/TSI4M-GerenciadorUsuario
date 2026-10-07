package br.org.edu.ifrn.GerenciadorUsuario.service;

import br.org.edu.ifrn.GerenciadorUsuario.dto.*;
import br.org.edu.ifrn.GerenciadorUsuario.exception.*;
import br.org.edu.ifrn.GerenciadorUsuario.model.Usuario;
import br.org.edu.ifrn.GerenciadorUsuario.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listar() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscar(Long id) {
        return toResponse(buscarEntidade(id));
    }

    @Transactional(readOnly = true)
    public NomeUsuarioResponse buscarNome(Long id) {
        Usuario usuario = buscarEntidade(id);
        return new NomeUsuarioResponse(usuario.getId(), usuario.getNome());
    }

    @Transactional
    public UsuarioResponse criar(UsuarioRequest request) {
        String login = normalizarLogin(request.usuario());
        if (repository.existsByUsuarioIgnoreCase(login)) {
            throw new UsuarioDuplicadoException("O nome de usuário já está cadastrado");
        }
        Usuario usuario = new Usuario(
                request.nome().trim(),
                login,
                passwordEncoder.encode(request.senha()),
                request.perfil());
        return toResponse(repository.save(usuario));
    }

    @Transactional
    public UsuarioResponse atualizar(Long id, UsuarioUpdateRequest request) {
        Usuario usuario = buscarEntidade(id);
        String login = normalizarLogin(request.usuario());
        if (repository.existsByUsuarioIgnoreCaseAndIdNot(login, id)) {
            throw new UsuarioDuplicadoException("O nome de usuário já está cadastrado");
        }
        usuario.setNome(request.nome().trim());
        usuario.setUsuario(login);
        usuario.setPerfil(request.perfil());
        if (request.senha() != null && !request.senha().isBlank()) {
            usuario.setSenha(passwordEncoder.encode(request.senha()));
        }
        return toResponse(repository.save(usuario));
    }

    @Transactional
    public void excluir(Long id) {
        Usuario usuario = buscarEntidade(id);
        repository.delete(usuario);
    }

    private Usuario buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado: " + id));
    }

    private String normalizarLogin(String usuario) {
        return usuario.trim().toLowerCase();
    }

    private UsuarioResponse toResponse(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(), usuario.getNome(), usuario.getUsuario(), usuario.getPerfil());
    }
}
