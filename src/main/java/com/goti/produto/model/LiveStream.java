package com.goti.produto.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "livestreams")
public class LiveStream {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String titulo;

    @Column(length = 1000)
    private String descricao;

    private BigDecimal precoIngresso;
    private Integer capacidadeMaxima;
    private Integer vagasOcupadas;

    @Version
    private Long versao; // Optimistic Locking para isolar disputas de vagas

    public LiveStream() {}

    public LiveStream(String titulo, String descricao, BigDecimal precoIngresso, Integer capacidadeMaxima) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.precoIngresso = precoIngresso;
        this.capacidadeMaxima = capacidadeMaxima;
        this.vagasOcupadas = 0;
    }

    public boolean temVagas(int quantidade) {
        return (vagasOcupadas + quantidade) <= capacidadeMaxima;
    }

    public void ocuparVagas(int quantidade) {
        this.vagasOcupadas += quantidade;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public BigDecimal getPrecoIngresso() { return precoIngresso; }
    public void setPrecoIngresso(BigDecimal precoIngresso) { this.precoIngresso = precoIngresso; }
    public Integer getCapacidadeMaxima() { return capacidadeMaxima; }
    public void setCapacidadeMaxima(Integer capacidadeMaxima) { this.capacidadeMaxima = capacidadeMaxima; }
    public Integer getVagasOcupadas() { return vagasOcupadas; }
    public void setVagasOcupadas(Integer vagasOcupadas) { this.vagasOcupadas = vagasOcupadas; }
    public Long getVersao() { return versao; }
}