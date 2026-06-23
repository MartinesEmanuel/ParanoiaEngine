package com.paranoia.engine.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "resultados_execucao")
public class ResultadoExecucaoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cenario_id", nullable = false)
    private Long cenarioId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_final", nullable = false)
    private StatusExecucao statusFinal;

    @Column(name = "estado_final_observado", columnDefinition = "TEXT")
    private String estadoFinalObservado;

    @Column(name = "estado_esperado", columnDefinition = "TEXT")
    private String estadoEsperado;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "logs_execucao", joinColumns = @JoinColumn(name = "resultado_id"))
    @Column(name = "mensagem", columnDefinition = "TEXT")
    private List<String> logsRelevantes = new ArrayList<>();

    protected ResultadoExecucaoEntity() {}

    public ResultadoExecucaoEntity(Long cenarioId, StatusExecucao statusFinal,
                                   String estadoFinalObservado, String estadoEsperado,
                                   List<String> logsRelevantes) {
        this.cenarioId = cenarioId;
        this.statusFinal = statusFinal;
        this.estadoFinalObservado = estadoFinalObservado;
        this.estadoEsperado = estadoEsperado;
        this.logsRelevantes = logsRelevantes != null ? logsRelevantes : new ArrayList<>();
    }

    public ResultadoExecucao toDto() {
        return new ResultadoExecucao(id, cenarioId, statusFinal,
            estadoFinalObservado, estadoEsperado, logsRelevantes);
    }

    public Long getId() { return id; }
    public Long getCenarioId() { return cenarioId; }
    public StatusExecucao getStatusFinal() { return statusFinal; }
    public String getEstadoFinalObservado() { return estadoFinalObservado; }
    public String getEstadoEsperado() { return estadoEsperado; }
    public List<String> getLogsRelevantes() { return logsRelevantes; }
}
