package com.paranoia.engine.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "cenarios_caos")
public class CenarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fragilidade_id", nullable = false)
    private FragilidadeEntity fragilidadeOrigem;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_cenario", nullable = false)
    private TipoCenario tipoCenario;

    @Column(name = "descricao_simulacao", columnDefinition = "TEXT")
    private String descricaoSimulacao;

    @Column(name = "resultado_esperado", columnDefinition = "TEXT")
    private String resultadoEsperado;

    @Enumerated(EnumType.STRING)
    @Column(name = "severidade_estimada", nullable = false)
    private SeveridadeEstimada severidadeEstimada;

    protected CenarioEntity() {}

    public CenarioEntity(String nome, FragilidadeEntity fragilidadeOrigem,
                         TipoCenario tipoCenario, String descricaoSimulacao,
                         String resultadoEsperado, SeveridadeEstimada severidadeEstimada) {
        this.nome = nome;
        this.fragilidadeOrigem = fragilidadeOrigem;
        this.tipoCenario = tipoCenario;
        this.descricaoSimulacao = descricaoSimulacao;
        this.resultadoEsperado = resultadoEsperado;
        this.severidadeEstimada = severidadeEstimada;
    }

    public CenarioCaos toDto() {
        return new CenarioCaos(
            id, nome, fragilidadeOrigem.getId(),
            tipoCenario, descricaoSimulacao,
            resultadoEsperado, severidadeEstimada);
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public FragilidadeEntity getFragilidadeOrigem() { return fragilidadeOrigem; }
    public TipoCenario getTipoCenario() { return tipoCenario; }
    public String getDescricaoSimulacao() { return descricaoSimulacao; }
    public String getResultadoEsperado() { return resultadoEsperado; }
    public SeveridadeEstimada getSeveridadeEstimada() { return severidadeEstimada; }
}
