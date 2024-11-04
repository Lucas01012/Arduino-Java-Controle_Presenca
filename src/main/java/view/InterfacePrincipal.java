package view;

import dao.ArduinoDAO;
import model.GraficoTela;
import model.Registro;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class InterfacePrincipal  extends javax.swing.JFrame {

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private ArduinoDAO arduinoDAO;

    public InterfacePrincipal() {
        this.arduinoDAO = new ArduinoDAO();
        setTitle("Controle de Presença - Registros");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        searchField = new JTextField(20);
        JButton searchButton = new JButton("Buscar");
        JButton deleteButton = new JButton("Deletar");
        JButton graphButton = new JButton("Gráfico");

        topPanel.add(searchField);
        topPanel.add(searchButton);
        topPanel.add(deleteButton);
        topPanel.add(graphButton);
        add(topPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"ID", "Comando", "Data/Hora"}, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buscarRegistros();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deletarRegistros();
            }
        });

        graphButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                abrirGraficoTela();
            }
        });
    }

    public void addRecord(int id, String command, String dateTime) {
        tableModel.addRow(new Object[]{id, command, dateTime});
    }

    private void buscarRegistros(){
        tableModel.setRowCount(0);
        List<Registro> registros  = arduinoDAO.getAllRecords();
        for (Registro registro : registros) {
            addRecord(registro.getId(), registro.getComando(), registro.getDataHora());
        }
    }

    private void deletarRegistros(){
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            arduinoDAO.deleteCommand(id);
            tableModel.removeRow(selectedRow);
        }else {
            JOptionPane.showMessageDialog(null, "Selecione um registro para deletar");
        }
    }
    private void abrirGraficoTela(){
        GraficoTela graficoTela = new GraficoTela();
        graficoTela.setVisible(true);
    }
}
