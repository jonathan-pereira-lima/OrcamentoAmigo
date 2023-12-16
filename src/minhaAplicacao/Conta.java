package minhaAplicacao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

class Conta {
    private List<Transacao> transacoes = new ArrayList<>();
    private double totalAcumulado = 0.0;

    public void cadastrarTransacao(Transacao transacao) {
        // Atualiza o total acumulado
        totalAcumulado += transacao.getValor();
        transacao.setTotal(totalAcumulado);

        transacoes.add(transacao);
        
    }
    
    public void excluirTransacao(int indice) {
        if (indice >= 0 && indice < transacoes.size()) {
            transacoes.remove(indice);
        }
    }


    public List<Transacao> listarTransacoesOrdenadas() {
        // Ordena as transações com base na data de vencimento em ordem crescente
        // Quando houver despesas e receitas na mesma data, as despesas aparecerão primeiro
        Collections.sort(transacoes, new Comparator<Transacao>() {
            @Override
            public int compare(Transacao t1, Transacao t2) {
                int dataComparison = t1.getDataVencimento().compareTo(t2.getDataVencimento());
                if (dataComparison != 0) {
                    return dataComparison;
                }

                if (t1.getCategoria().equals("Despesa") && t2.getCategoria().equals("Receita")) {
                    return -1;
                } else if (t1.getCategoria().equals("Receita") && t2.getCategoria().equals("Despesa")) {
                    return 1;
                }

                return 0;
            }
        });
        return transacoes;
    }
}
