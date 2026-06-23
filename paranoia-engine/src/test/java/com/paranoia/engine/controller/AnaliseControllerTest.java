package com.paranoia.engine.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.paranoia.engine.model.AnaliseEntity;
import com.paranoia.engine.model.AnaliseRequest;
import com.paranoia.engine.model.AnaliseResponse;
import com.paranoia.engine.model.StatusAnalise;
import com.paranoia.engine.model.FragilidadeType;
import com.paranoia.engine.repository.AnaliseRepository;
import com.paranoia.engine.repository.FragilidadeRepository;
import com.paranoia.engine.service.OrquestradorService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class AnaliseControllerTest {

    @TempDir
    Path tempDir;

    @Autowired
    private AnaliseController analiseController;

    @Autowired
    private AnaliseRepository analiseRepository;

    @Autowired
    private FragilidadeRepository fragilidadeRepository;

    private Path fixtureDir;

    @BeforeEach
    void setUp() throws IOException {
        fixtureDir = tempDir.resolve("fixtures");
        Files.createDirectories(fixtureDir);
        copiarFixture("ContaService.java");
        copiarFixture("TransacaoRepository.java");
    }

    private void copiarFixture(String nome) throws IOException {
        try (InputStream is = getClass().getClassLoader()
                .getResourceAsStream("fixtures/" + nome)) {
            if (is == null) throw new IOException("Fixture nao encontrada: " + nome);
            Files.copy(is, fixtureDir.resolve(nome));
        }
    }

    @Test
    void deveCriarAnaliseEEncontrarFragilidades() {
        var request = new AnaliseRequest(fixtureDir.toString());
        var response = analiseController.analisar(request);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        AnaliseResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.id()).isNotNull();
        assertThat(body.status()).isEqualTo(StatusAnalise.EM_ANDAMENTO);
        assertThat(body.pontos()).isNotEmpty();

        List<FragilidadeType> tipos = body.pontos().stream()
            .map(p -> p.tipo())
            .distinct()
            .toList();
        assertThat(tipos).contains(
            FragilidadeType.TRANSACAO,
            FragilidadeType.CHAMADA_EXTERNA,
            FragilidadeType.ESTADO_COMPARTILHADO);
    }

    @Test
    void devePersistirAnaliseComFragilidades() {
        var request = new AnaliseRequest(fixtureDir.toString());
        var response = analiseController.analisar(request);

        AnaliseResponse body = response.getBody();
        assertThat(body).isNotNull();

        AnaliseEntity analise = analiseRepository.findById(body.id()).orElseThrow();
        assertThat(analise.getTotalFragilidades()).isPositive();
        assertThat(analise.getStatus()).isEqualTo(StatusAnalise.EM_ANDAMENTO);

        var frags = fragilidadeRepository.findByAnaliseId(body.id());
        assertThat(frags).hasSize(analise.getTotalFragilidades());
        assertThat(frags).allMatch(f -> f.getAnalise() != null
            && f.getAnalise().getId().equals(body.id()));
    }
}
