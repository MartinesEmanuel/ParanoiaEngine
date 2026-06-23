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
@Table(name = "fragilidades")
public class FragilidadeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FragilidadeType tipo;

    @Column(name = "classe_origem", nullable = false)
    private String classeOrigem;

    @Column(name = "metodo_origem")
    private String metodoOrigem;

    @Column(nullable = false)
    private int linha;

    @Column(columnDefinition = "TEXT")
    private String trechoCodigo;

    @Column(name = "descricao_tecnica", columnDefinition = "TEXT")
    private String descricaoTecnica;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analise_id")
    private AnaliseEntity analise;

    protected FragilidadeEntity() {}

    public static FragilidadeEntity from(FragilidadePoint point) {
        FragilidadeEntity e = new FragilidadeEntity();
        e.tipo = point.tipo();
        e.classeOrigem = point.classeOrigem();
        e.metodoOrigem = point.metodoOrigem();
        e.linha = point.linha();
        e.trechoCodigo = point.trechoCodigo();
        e.descricaoTecnica = point.descricaoTecnica();
        return e;
    }

    public Long getId() { return id; }
    public FragilidadeType getTipo() { return tipo; }
    public String getClasseOrigem() { return classeOrigem; }
    public String getMetodoOrigem() { return metodoOrigem; }
    public int getLinha() { return linha; }
    public String getTrechoCodigo() { return trechoCodigo; }
    public String getDescricaoTecnica() { return descricaoTecnica; }
    public AnaliseEntity getAnalise() { return analise; }

    public void setAnalise(AnaliseEntity analise) { this.analise = analise; }
}
