package com.paranoia.engine.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "analises")
public class AnaliseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "caminho_diretorio", nullable = false)
    private String caminhoDiretorio;

    @Column(name = "data_inicio")
    private LocalDateTime dataInicio;

    @Column(name = "data_fim")
    private LocalDateTime dataFim;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusAnalise status;

    @Column(name = "total_fragilidades")
    private int totalFragilidades;

    @Column(name = "fragilidades_processadas")
    private int fragilidadesProcessadas;

    @Column(name = "total_cenarios")
    private int totalCenarios;

    @Column(name = "cenarios_executados")
    private int cenariosExecutados;

    @OneToMany(mappedBy = "analise", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<FragilidadeEntity> fragilidades = new ArrayList<>();

    protected AnaliseEntity() {}

    public AnaliseEntity(String caminhoDiretorio) {
        this.caminhoDiretorio = caminhoDiretorio;
        this.dataInicio = LocalDateTime.now();
        this.status = StatusAnalise.EM_ANDAMENTO;
    }

    public void concluir() {
        this.status = StatusAnalise.CONCLUIDA;
        this.dataFim = LocalDateTime.now();
    }

    public void falhar() {
        this.status = StatusAnalise.FALHOU;
        this.dataFim = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getCaminhoDiretorio() { return caminhoDiretorio; }
    public LocalDateTime getDataInicio() { return dataInicio; }
    public LocalDateTime getDataFim() { return dataFim; }
    public StatusAnalise getStatus() { return status; }
    public int getTotalFragilidades() { return totalFragilidades; }
    public int getFragilidadesProcessadas() { return fragilidadesProcessadas; }
    public int getTotalCenarios() { return totalCenarios; }
    public int getCenariosExecutados() { return cenariosExecutados; }
    public List<FragilidadeEntity> getFragilidades() { return fragilidades; }

    public void setTotalFragilidades(int totalFragilidades) { this.totalFragilidades = totalFragilidades; }
    public void setFragilidadesProcessadas(int fragilidadesProcessadas) { this.fragilidadesProcessadas = fragilidadesProcessadas; }
    public void setTotalCenarios(int totalCenarios) { this.totalCenarios = totalCenarios; }
    public void setCenariosExecutados(int cenariosExecutados) { this.cenariosExecutados = cenariosExecutados; }
}
