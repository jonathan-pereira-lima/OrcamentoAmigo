package minhaAplicacao;


import java.util.Date;


class Transacao {
    private Date dataVencimento;
    private String descricao;
    private String categoria;
    private double valor;
    private double total;

    public Transacao(Date dataVencimento, String descricao, String categoria, double valor, double total) {
        this.dataVencimento = dataVencimento;
        this.descricao = descricao;
        this.categoria = categoria;
        this.valor = valor;
        this.total = total;
    }

    public Date getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(Date dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}