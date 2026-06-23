package com.paranoia.engine.service;

import com.paranoia.engine.model.CenarioCaos;
import com.paranoia.engine.model.CenarioEntity;
import com.paranoia.engine.model.CenarioRaw;
import com.paranoia.engine.model.CenarioRawList;
import com.paranoia.engine.model.FragilidadeEntity;
import com.paranoia.engine.model.FragilidadePoint;
import com.paranoia.engine.model.SeveridadeEstimada;
import com.paranoia.engine.model.TipoCenario;
import com.paranoia.engine.repository.CenarioRepository;
import com.paranoia.engine.repository.FragilidadeRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CenarioIaService {

    private static final Logger log = LoggerFactory.getLogger(CenarioIaService.class);

    private static final String SYSTEM_PROMPT = """
        You are a Chaos Engineering expert analyzing Java/Spring Boot code fragility points.

        Given a fragility point (code snippet + technical description), generate exactly 3
        realistic and SPECIFIC chaos scenarios that could exploit that fragility.

        For each scenario, provide:
        - nome: Short descriptive name (max 60 chars, in Portuguese like Portuguese-speaking Brazilian engineers)
        - tipoCenario: One of CONCORRENCIA, FALHA_REDE, FALHA_BANCO, TIMEOUT
        - descricaoSimulacao: Exactly what should be simulated and how (technical, specific, referencing actual variables/methods from the code)
        - resultadoEsperado: What should happen if the code is correct (specific, not generic)
        - severidadeEstimada: BAIXA, MEDIA, ALTA, or CRITICA

        CRITICAL RULES:
        - Scenarios MUST be REALISTIC and SPECIFIC to the EXACT code shown
        - Reference specific variables, methods, or lines from the code snippet
        - Do NOT generate generic scenarios that could apply to any code
        - Return ONLY valid JSON, no additional text
        """;

    private final ChatClient chatClient;
    private final FragilidadeRepository fragilidadeRepository;
    private final CenarioRepository cenarioRepository;

    public CenarioIaService(ChatClient chatClient,
                            FragilidadeRepository fragilidadeRepository,
                            CenarioRepository cenarioRepository) {
        this.chatClient = chatClient;
        this.fragilidadeRepository = fragilidadeRepository;
        this.cenarioRepository = cenarioRepository;
    }

    public List<CenarioCaos> gerarCenarios(List<FragilidadePoint> pontos) {
        List<CenarioCaos> todosCenarios = new ArrayList<>();

        for (FragilidadePoint ponto : pontos) {
            FragilidadeEntity fragEntity = fragilidadeRepository.save(
                FragilidadeEntity.from(ponto));

            try {
                todosCenarios.addAll(gerarCenarios(fragEntity));
            } catch (Exception e) {
                log.warn("Falha ao gerar cenários para {}#{}: {}",
                    fragEntity.getClasseOrigem(),
                    fragEntity.getMetodoOrigem(),
                    e.getMessage());
            }
        }

        return todosCenarios;
    }

    public List<CenarioCaos> gerarCenarios(FragilidadeEntity fragEntity) {
        List<CenarioCaos> cenarios = new ArrayList<>();

        try {
            String userPrompt = montarPrompt(fragEntity);
            CenarioRawList raw = chatClient.prompt()
                .system(SYSTEM_PROMPT)
                .user(userPrompt)
                .call()
                .entity(CenarioRawList.class);

            if (raw == null || raw.cenarios() == null) {
                log.warn("Resposta nula ou vazia para {}#{}",
                    fragEntity.getClasseOrigem(), fragEntity.getMetodoOrigem());
                return cenarios;
            }

            for (CenarioRaw r : raw.cenarios()) {
                CenarioEntity entity = new CenarioEntity(
                    r.nome(),
                    fragEntity,
                    TipoCenario.valueOf(r.tipoCenario()),
                    r.descricaoSimulacao(),
                    r.resultadoEsperado(),
                    SeveridadeEstimada.valueOf(r.severidadeEstimada()));
                entity = cenarioRepository.save(entity);
                cenarios.add(entity.toDto());
            }

        } catch (Exception e) {
            log.warn("Falha ao gerar cenários para {}#{}: {}",
                fragEntity.getClasseOrigem(),
                fragEntity.getMetodoOrigem(),
                e.getMessage());
        }

        return cenarios;
    }

    private String montarPrompt(FragilidadeEntity frag) {
        return String.format("""
            Fragility point:
            - Type: %s
            - Class: %s
            - Method: %s
            - Line: %d

            Code snippet:
            ```
            %s
            ```

            Technical description:
            %s

            Generate 3 specific chaos scenarios for this exact code.
            """,
            frag.getTipo(),
            frag.getClasseOrigem(),
            frag.getMetodoOrigem() != null ? frag.getMetodoOrigem() : "(class-level)",
            frag.getLinha(),
            frag.getTrechoCodigo() != null ? frag.getTrechoCodigo() : "(not available)",
            frag.getDescricaoTecnica() != null ? frag.getDescricaoTecnica() : "(not available)");
    }
}
