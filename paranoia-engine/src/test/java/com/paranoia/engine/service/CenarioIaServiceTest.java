package com.paranoia.engine.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.paranoia.engine.model.CenarioCaos;
import com.paranoia.engine.model.CenarioRaw;
import com.paranoia.engine.model.CenarioRawList;
import com.paranoia.engine.model.FragilidadePoint;
import com.paranoia.engine.model.FragilidadeType;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClient.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClient.ChatClientRequest.CallResponseSpec;

import java.util.List;

@SpringBootTest
class CenarioIaServiceTest {

    @MockBean
    private ChatClient chatClient;

    @Autowired
    private CenarioIaService cenarioIaService;

    @Test
    void deveGerarCenariosComSucesso() {
        ChatClientRequest request = org.mockito.Mockito.mock(ChatClientRequest.class);
        CallResponseSpec responseSpec = org.mockito.Mockito.mock(CallResponseSpec.class);

        when(chatClient.prompt()).thenReturn(request);
        when(request.system(anyString())).thenReturn(request);
        when(request.user(anyString())).thenReturn(request);
        when(request.call()).thenReturn(responseSpec);

        CenarioRawList respostaMock = new CenarioRawList(List.of(
            new CenarioRaw("Debito concorrente na mesma conta", "CONCORRENCIA",
                "Simular 20 requisicoes simultaneas de debito via 20 threads paralelas "
                + "chamando debitar(100.0) concorrentemente antes do commit da transacao.",
                "Apenas um debito de 100 deve ser aplicado; os demais devem encontrar "
                + "saldo insuficiente ou falhar por lock otimista.",
                "CRITICA"),
            new CenarioRaw("Timeout no ledger externo durante debito", "TIMEOUT",
                "Simular timeout de 30s na chamada restTemplate.postForEntity para o ledger "
                + "enquanto a transacao @Transactional esta ativa.",
                "A transacao deve fazer rollback completo, sem aplicar o debito "
                + "nem deixar o saldo inconsistente.",
                "ALTA"),
            new CenarioRaw("Queda do banco durante creditar", "FALHA_BANCO",
                "Simular queda da conexao com PostgreSQL durante a execucao de creditar(), "
                + "apos o calculo do novo saldo mas antes do flush do JPA.",
                "A transacao deve reverter o calculo do saldo; o saldo permanece inalterado.",
                "CRITICA")
        ));

        when(responseSpec.entity(CenarioRawList.class)).thenReturn(respostaMock);

        var ponto = new FragilidadePoint(
            FragilidadeType.TRANSACAO,
            "com.exemplo.ContaService",
            "debitar",
            15,
            """
            @Transactional
            public void debitar(double valor) {
                if (saldo >= valor) {
                    saldo -= valor;
                    restTemplate.postForEntity("http://ledger/api/debito", null, Void.class);
                }
            }
            """,
            "Metodo anotado com @Transactional - pode expor a transacao a contencao");

        List<CenarioCaos> resultado = cenarioIaService.gerarCenarios(List.of(ponto));

        assertThat(resultado).hasSize(3);
        assertThat(resultado.get(0).fragilidadeOrigemId()).isNotNull();
        assertThat(resultado.get(0).tipoCenario()).isNotNull();
        assertThat(resultado.get(0).severidadeEstimada()).isNotNull();
    }

    @Test
    void deveContinuarMesmoComFalhaNoLlm() {
        ChatClientRequest request = org.mockito.Mockito.mock(ChatClientRequest.class);
        CallResponseSpec responseSpec = org.mockito.Mockito.mock(CallResponseSpec.class);

        when(chatClient.prompt()).thenReturn(request);
        when(request.system(anyString())).thenReturn(request);
        when(request.user(anyString())).thenReturn(request);
        when(request.call()).thenReturn(responseSpec);
        when(responseSpec.entity(any(Class.class))).thenThrow(new RuntimeException("LLM timeout"));

        var ponto = new FragilidadePoint(
            FragilidadeType.ESTADO_COMPARTILHADO,
            "com.exemplo.ContaService",
            "reset",
            30,
            "public void reset() { saldo = 0; }",
            "Campo nao-final modificado em metodo publico");

        List<CenarioCaos> resultado = cenarioIaService.gerarCenarios(List.of(ponto));

        assertThat(resultado).isEmpty();
    }

    @Test
    void deveGerarParaMultiplosPontos() {
        ChatClientRequest request = org.mockito.Mockito.mock(ChatClientRequest.class);
        CallResponseSpec responseSpec = org.mockito.Mockito.mock(CallResponseSpec.class);

        when(chatClient.prompt()).thenReturn(request);
        when(request.system(anyString())).thenReturn(request);
        when(request.user(anyString())).thenReturn(request);
        when(request.call()).thenReturn(responseSpec);

        CenarioRawList respostaMock = new CenarioRawList(List.of(
            new CenarioRaw("Cenario unico", "CONCORRENCIA",
                "Teste", "Esperado", "MEDIA")
        ));
        when(responseSpec.entity(CenarioRawList.class)).thenReturn(respostaMock);

        var ponto1 = new FragilidadePoint(
            FragilidadeType.TRANSACAO, "c.Classe1", "metodo1", 10, "codigo1", "desc1");
        var ponto2 = new FragilidadePoint(
            FragilidadeType.CHAMADA_EXTERNA, "c.Classe2", "metodo2", 20, "codigo2", "desc2");

        List<CenarioCaos> resultado = cenarioIaService.gerarCenarios(List.of(ponto1, ponto2));

        assertThat(resultado).hasSize(2);
    }
}
