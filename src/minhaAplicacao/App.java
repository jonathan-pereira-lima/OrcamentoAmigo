package minhaAplicacao;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;



public class App {
    public static void main(String[] args) {
        Conta conta = new Conta();
        carregarDados(conta);

        int opcao;
        do {
            opcao = exibirMenu();
            switch (opcao) {
                case 1:
                    cadastrarTransacao(conta);
                    break;
                case 2:
                    listarTransacoes(conta);
                    break;
                case 3:
                    editarTransacao(conta);
                    break;
                case 4:
                    excluirTransacao(conta);
                    break;
                case 0:
                    salvarDados(conta);
                    JOptionPane.showMessageDialog(null, "Saindo do programa.");
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Opção inválida!");
            }
        } while (opcao != 0);
    }

    private static void excluirTransacao(Conta conta) {
        String input = JOptionPane.showInputDialog("Digite o índice da transação que deseja excluir:");
        if (input != null && !input.isEmpty()) {
            try {
                int indice = Integer.parseInt(input);
                conta.excluirTransacao(indice);
                JOptionPane.showMessageDialog(null, "Transação excluída com sucesso!");
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Índice de transação inválido!");
            }
        }
    }


	private static int exibirMenu() {
        String input = JOptionPane.showInputDialog("Escolha uma opção:\n1. Cadastrar Transação\n2. Listar Transações\n3. Editar Transação\n4. Excluir Transação\n0. Sair");
        if (input != null && !input.isEmpty()) {
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                // Se a entrada não for um número válido, retorna -1 (opção inválida)
            }
        }
        return -1;
    }

    private static void carregarDados(Conta conta) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("dados.txt"));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                Date dataVencimento = new SimpleDateFormat("dd/MM/yyyy").parse(parts[0]);
                String descricao = parts[1];
                String categoria = parts[2];
                // Substitui vírgulas por pontos no valor
                String valorStr = parts[3].replace(',', '.');
                double valor = Double.parseDouble(valorStr);
                // Substitui vírgulas por pontos no total
                String totalStr = parts[4].replace(',', '.');
                double total = Double.parseDouble(totalStr);

                Transacao transacao = new Transacao(dataVencimento, descricao, categoria, valor, total);
                conta.cadastrarTransacao(transacao);
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void salvarDados(Conta conta) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("dados.txt"));
            for (Transacao transacao : conta.listarTransacoesOrdenadas()) {
                String dataVencimentoStr = new SimpleDateFormat("dd/MM/yyyy").format(transacao.getDataVencimento());
                String descricao = transacao.getDescricao();
                String categoria = transacao.getCategoria();
                // Formata valor e total com duas casas decimais
                String valorStr = String.format("%.2f", transacao.getValor());
                String totalStr = String.format("%.2f", transacao.getTotal());

                String line = dataVencimentoStr + "," + descricao + "," + categoria + "," + valorStr + "," + totalStr;
                writer.write(line);
                writer.newLine();
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void cadastrarTransacao(Conta conta) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date dataVencimento;
        try {
            dataVencimento = dateFormat.parse(JOptionPane.showInputDialog("Data de Vencimento (dd/MM/yyyy):"));
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(null, "Data de Vencimento inválida!");
            return;
        }

        String descricao = JOptionPane.showInputDialog("Descrição da Transação:");
        if (descricao == null || descricao.isEmpty()) {
            JOptionPane.showMessageDialog(null, "A descrição não pode estar vazia.");
            return;
        }

        String[] categorias = {"Receita", "Despesa"};
        String categoria = (String) JOptionPane.showInputDialog(null, "Escolha a Categoria:", "Categoria", JOptionPane.QUESTION_MESSAGE, null, categorias, categorias[0]);
        if (categoria == null || categoria.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Selecione uma categoria (Receita ou Despesa).");
            return;
        }

        String valorStr = JOptionPane.showInputDialog("Valor da Transação:");
        if (valorStr == null || valorStr.isEmpty()) {
            JOptionPane.showMessageDialog(null, "O valor não pode estar vazio.");
            return;
        }

        double valor;
        try {
            valor = Double.parseDouble(valorStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Valor inválido. Insira um número válido.");
            return;
        }

        if ("Despesa".equals(categoria)) {
            valor *= -1;
        }

        Transacao transacao = new Transacao(dataVencimento, descricao, categoria, valor, 0.0);
        conta.cadastrarTransacao(transacao);

        JOptionPane.showMessageDialog(null, "Transação cadastrada com sucesso!");
    }

    private static void listarTransacoes(Conta conta) {
        List<Transacao> transacoes = conta.listarTransacoesOrdenadas();

        String[] columnNames = {"Índice", "Data de Vencimento", "Descrição", "Categoria", "Valor", "Total"};
        String[][] data = new String[transacoes.size()][6];

        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                String dataHoje = dateFormat.format(new Date());
                String dataNaTabela = (String) table.getValueAt(row, 1);

                // Verifica se o "Total" é negativo e define a cor da célula como vermelha
                String totalStr = (String) table.getValueAt(row, 5);
                double total = Double.parseDouble(totalStr.replace(',', '.'));
                if (total < 0) {
                    component.setForeground(Color.RED);
                } else {
                    component.setForeground(Color.BLACK);
                }

                if (dataNaTabela.equals(dataHoje)) {
                    component.setBackground(Color.YELLOW);
                } else {
                    component.setBackground(Color.WHITE);
                }
                return component;
            }
        });

        for (int i = 0; i < transacoes.size(); i++) {
            Transacao transacao = transacoes.get(i);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            data[i][0] = String.valueOf(i);
            data[i][1] = dateFormat.format(transacao.getDataVencimento());
            data[i][2] = transacao.getDescricao();
            data[i][3] = transacao.getCategoria();
            data[i][4] = String.format("%.2f", transacao.getValor());
            data[i][5] = String.format("%.2f", transacao.getTotal());
        }

        tableModel.setDataVector(data, columnNames);

        JOptionPane.showMessageDialog(null, scrollPane, "Transações", JOptionPane.PLAIN_MESSAGE);
    }

    private static void editarTransacao(Conta conta) {
        String input = JOptionPane.showInputDialog("Digite o índice da transação que deseja editar:");
        if (input != null && !input.isEmpty()) {
            try {
                int indice = Integer.parseInt(input);
                List<Transacao> transacoes = conta.listarTransacoesOrdenadas();
                if (indice >= 0 && indice < transacoes.size()) {
                    Transacao transacao = transacoes.get(indice);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    Date dataVencimento;
                    try {
                        dataVencimento = dateFormat.parse(JOptionPane.showInputDialog("Data de Vencimento (dd/MM/yyyy):", dateFormat.format(transacao.getDataVencimento())));
                    } catch (ParseException e) {
                        JOptionPane.showMessageDialog(null, "Data de Vencimento inválida!");
                        return;
                    }

                    String descricao = JOptionPane.showInputDialog("Descrição da Transação:", transacao.getDescricao());
                    if (descricao == null || descricao.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "A descrição não pode estar vazia.");
                        return;
                    }

                    String[] categorias = {"Receita", "Despesa"};
                    String categoria = (String) JOptionPane.showInputDialog(null, "Escolha a Categoria:", "Categoria", JOptionPane.QUESTION_MESSAGE, null, categorias, transacao.getCategoria());
                    if (categoria == null || categoria.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Selecione uma categoria (Receita ou Despesa).");
                        return;
                    }

                    String valorStr = JOptionPane.showInputDialog("Valor da Transação:", String.format("%.2f", transacao.getValor()));
                    if (valorStr == null || valorStr.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "O valor não pode estar vazio.");
                        return;
                    }

                    double valor;
                    try {
                        valor = Double.parseDouble(valorStr);
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(null, "Valor inválido. Insira um número válido.");
                        return;
                    }

                    if ("Despesa".equals(categoria)) {
                        valor *= -1;
                    }

                    transacao.setDataVencimento(dataVencimento);
                    transacao.setDescricao(descricao);
                    transacao.setCategoria(categoria);
                    transacao.setValor(valor);

                    conta.listarTransacoesOrdenadas();
                    JOptionPane.showMessageDialog(null, "Transação editada com sucesso!");
                } else {
                    JOptionPane.showMessageDialog(null, "Índice de transação inválido!");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Índice de transação inválido!");
            }
        }
    }
   
}
