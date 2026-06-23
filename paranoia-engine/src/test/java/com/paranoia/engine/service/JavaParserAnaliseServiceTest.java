package com.paranoia.engine.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.paranoia.engine.model.FragilidadePoint;
import com.paranoia.engine.model.FragilidadeType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

class JavaParserAnaliseServiceTest {

    private final JavaParserAnaliseService service = new JavaParserAnaliseService();

    @TempDir
    Path tempDir;

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
            if (is == null) throw new IOException("Fixture não encontrada: " + nome);
            Files.copy(is, fixtureDir.resolve(nome));
        }
    }

    // ─── TRANSACAO ───────────────────────────────────────────────────────────

    @Test
    void deveDetectarMetodosTransacionais() {
        List<FragilidadePoint> pontos = service.analisar(fixtureDir.toString());

        List<FragilidadePoint> transacoes = pontos.stream()
            .filter(p -> p.tipo() == FragilidadeType.TRANSACAO)
            .toList();

        assertThat(transacoes)
            .as("Deveria encontrar métodos @Transactional")
            .isNotEmpty();

        assertThat(transacoes)
            .extracting(FragilidadePoint::metodoOrigem)
            .contains("debitar", "creditar");
    }

    // ─── CHAMADA_EXTERNA ─────────────────────────────────────────────────────

    @Test
    void deveDetectarRestTemplate() {
        List<FragilidadePoint> pontos = service.analisar(fixtureDir.toString());

        List<FragilidadePoint> chamadas = pontos.stream()
            .filter(p -> p.tipo() == FragilidadeType.CHAMADA_EXTERNA)
            .toList();

        assertThat(chamadas)
            .as("Deveria encontrar chamadas externas")
            .isNotEmpty();

        assertThat(chamadas)
            .filteredOn(p -> p.classeOrigem().contains("ContaService"))
            .extracting(FragilidadePoint::metodoOrigem)
            .contains("debitar");
    }

    @Test
    void deveDetectarJpaRepositoryMethods() {
        List<FragilidadePoint> pontos = service.analisar(fixtureDir.toString());

        List<FragilidadePoint> chamadas = pontos.stream()
            .filter(p -> p.tipo() == FragilidadeType.CHAMADA_EXTERNA)
            .toList();

        assertThat(chamadas)
            .filteredOn(p -> p.classeOrigem().contains("TransacaoRepository"))
            .isNotEmpty();

        assertThat(chamadas)
            .filteredOn(p -> p.classeOrigem().contains("TransacaoRepository"))
            .extracting(FragilidadePoint::metodoOrigem)
            .contains("findByContaId", "findByValorGreaterThan");
    }

    // ─── ESTADO_COMPARTILHADO ────────────────────────────────────────────────

    @Test
    void deveDetectarEstadoCompartilhadoMutavel() {
        List<FragilidadePoint> pontos = service.analisar(fixtureDir.toString());

        List<FragilidadePoint> estados = pontos.stream()
            .filter(p -> p.tipo() == FragilidadeType.ESTADO_COMPARTILHADO)
            .toList();

        assertThat(estados)
            .as("Deveria encontrar estado compartilhado mutável")
            .isNotEmpty();

        assertThat(estados)
            .extracting(FragilidadePoint::descricaoTecnica)
            .anyMatch(d -> d.contains("saldo"));
    }

    @Test
    void deveIgnorarCamposFinais() {
        List<FragilidadePoint> pontos = service.analisar(fixtureDir.toString());

        List<FragilidadePoint> estados = pontos.stream()
            .filter(p -> p.tipo() == FragilidadeType.ESTADO_COMPARTILHADO)
            .toList();

        assertThat(estados)
            .extracting(FragilidadePoint::descricaoTecnica)
            .noneMatch(d -> d.contains("constante"));
    }

    // ─── INTEGRAÇÃO ──────────────────────────────────────────────────────────

    @Test
    void deveReportarOsTresTipos() {
        List<FragilidadePoint> pontos = service.analisar(fixtureDir.toString());

        assertThat(pontos)
            .extracting(FragilidadePoint::tipo)
            .contains(
                FragilidadeType.TRANSACAO,
                FragilidadeType.CHAMADA_EXTERNA,
                FragilidadeType.ESTADO_COMPARTILHADO
            );
    }

    // ─── VALIDAÇÕES ──────────────────────────────────────────────────────────

    @Test
    void deveLancarExcecaoParaDiretorioInvalido() {
        assertThrows(IllegalArgumentException.class,
            () -> service.analisar("/caminho/inexistente"));
    }

    @Test
    void deveRetornarVazioParaDiretorioSemJava() throws IOException {
        Path vazio = tempDir.resolve("vazio");
        Files.createDirectories(vazio);
        List<FragilidadePoint> pontos = service.analisar(vazio.toString());
        assertThat(pontos).isEmpty();
    }
}
