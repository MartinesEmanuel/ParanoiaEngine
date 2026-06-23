package com.paranoia.engine.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.paranoia.engine.execution.CenarioExecutor;
import com.paranoia.engine.model.AnaliseEntity;
import com.paranoia.engine.model.CenarioCaos;
import com.paranoia.engine.model.FragilidadeEntity;
import com.paranoia.engine.model.FragilidadePoint;
import com.paranoia.engine.model.FragilidadeType;
import com.paranoia.engine.model.SeveridadeEstimada;
import com.paranoia.engine.model.StatusAnalise;
import com.paranoia.engine.model.TipoCenario;
import com.paranoia.engine.repository.AnaliseRepository;
import com.paranoia.engine.repository.FragilidadeRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import java.util.List;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class OrquestradorServiceTest {

    @Autowired
    private OrquestradorService orquestradorService;

    @Autowired
    private AnaliseRepository analiseRepository;

    @Autowired
    private FragilidadeRepository fragilidadeRepository;

    @MockBean
    private AnaliseService analiseService;

    @MockBean
    private CenarioIaService cenarioIaService;

    @MockBean
    private CenarioExecutor cenarioExecutor;

    @Test
    void deveCriarAnaliseComFragilidadesMockadas() {
        when(analiseService.analisar(any())).thenReturn(List.of(
            new FragilidadePoint(FragilidadeType.TRANSACAO, "c.Classe", "metodo", 10,
                "codigo", "descricao"),
            new FragilidadePoint(FragilidadeType.CHAMADA_EXTERNA, "c.Repo", "findAll", 5,
                "codigo2", "descricao2")));

        AnaliseEntity analise = orquestradorService.criarAnalise("/caminho/fake");

        assertThat(analise.getId()).isNotNull();
        assertThat(analise.getStatus()).isEqualTo(StatusAnalise.EM_ANDAMENTO);
        assertThat(analise.getTotalFragilidades()).isEqualTo(2);

        List<FragilidadeEntity> frags = fragilidadeRepository.findByAnaliseId(analise.getId());
        assertThat(frags).hasSize(2);
        assertThat(frags).allMatch(f -> f.getAnalise().getId().equals(analise.getId()));
    }

    @Test
    void deveMarcarComoFalhouQuandoAnaliseLancaExcecao() {
        when(analiseService.analisar(any())).thenThrow(new RuntimeException("Erro simulado"));

        AnaliseEntity analise = orquestradorService.criarAnalise("/caminho/fake");

        assertThat(analise.getStatus()).isEqualTo(StatusAnalise.FALHOU);
        assertThat(analise.getTotalFragilidades()).isEqualTo(0);
    }

    @Test
    void pipelineDeveProcessarFragilidadesEGerarCenarios() throws Exception {
        when(analiseService.analisar(any())).thenReturn(List.of(
            new FragilidadePoint(FragilidadeType.TRANSACAO, "c.Classe", "metodo", 10,
                "codigo", "descricao")));

        AnaliseEntity analise = orquestradorService.criarAnalise("/caminho/fake");
        List<FragilidadeEntity> frags = fragilidadeRepository.findByAnaliseId(analise.getId());

        when(cenarioIaService.gerarCenarios(any(FragilidadeEntity.class))).thenReturn(List.of(
            new CenarioCaos(99L, "Cenario Teste", frags.get(0).getId(),
                TipoCenario.CONCORRENCIA, "desc", "resultado esperado",
                SeveridadeEstimada.MEDIA)));

        when(cenarioExecutor.executar(any(Long.class), any(), any())).then(invocation -> null);

        orquestradorService.executarPipeline(analise.getId());

        AnaliseEntity atualizada = aguardarConclusao(analise.getId(), 5);
        assertThat(atualizada).isNotNull();
        assertThat(atualizada.getStatus()).isEqualTo(StatusAnalise.CONCLUIDA);
        assertThat(atualizada.getFragilidadesProcessadas()).isEqualTo(1);
        assertThat(atualizada.getTotalCenarios()).isEqualTo(1);
    }

    private AnaliseEntity aguardarConclusao(Long analiseId, int timeoutSegundos) {
        long limite = System.currentTimeMillis() + (timeoutSegundos * 1000L);
        while (System.currentTimeMillis() < limite) {
            AnaliseEntity a = analiseRepository.findById(analiseId).orElse(null);
            if (a != null && a.getStatus() != StatusAnalise.EM_ANDAMENTO) {
                return a;
            }
            try { Thread.sleep(200); } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        return analiseRepository.findById(analiseId).orElse(null);
    }
}
