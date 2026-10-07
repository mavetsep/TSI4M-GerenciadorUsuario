package br.org.edu.ifrn.GerenciadorUsuario;

import br.org.edu.ifrn.GerenciadorUsuario.model.*;
import br.org.edu.ifrn.GerenciadorUsuario.repository.UsuarioRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UsuarioApiIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired UsuarioRepository repository;

    @BeforeEach
    void limpar() {
        repository.deleteAll();
    }

    @Test
    void deveCriarUsuarioEBuscarNomePorId() throws Exception {
        String resposta = mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Paulo Ricardo",
                                  "usuario": "paulo",
                                  "senha": "12345",
                                  "perfil": "VENDEDOR"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.senha").doesNotExist())
                .andReturn().getResponse().getContentAsString();

        Long id = Long.valueOf(resposta.replaceAll(".*\\"id\\":(\\d+).*", "$1"));

        mockMvc.perform(get("/api/usuarios/{id}/nome", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.nome").value("Paulo Ricardo"));
    }

    @Test
    void deveRetornar404ParaUsuarioInexistente() throws Exception {
        mockMvc.perform(get("/api/usuarios/999/nome"))
                .andExpect(status().isNotFound());
    }
}
