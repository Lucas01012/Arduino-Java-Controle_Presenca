package view;

import dao.ArduinoDAO;
import dao.GraficoDAO;
import model.ComandoPorDia;
import model.GraficoTela;
import model.Registro;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.List;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jdatepicker.JDatePicker;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.SqlDateModel;
import util.DateLabelFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;


public class InterfacePrincipal extends JFrame {

    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<String> filterComboBox;
    private ArduinoDAO arduinoDAO;
    private JDatePickerImpl datePicker;
    private JDialog dateDialog;

    public InterfacePrincipal() {
        this.arduinoDAO = new ArduinoDAO();
        setTitle("Controle de Presença - Registros");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();

        filterComboBox = new JComboBox<>(new String[]{"", "Por dia específico", "Dia atual", "Semana atual", "Mês a mês"});
        JButton searchButton = new JButton("Buscar");
        JButton deleteButton = new JButton("Deletar");
        JButton graphButton = new JButton("Gráfico");
        JButton exportButton = new JButton("Exportar");

        topPanel.add(filterComboBox);
        topPanel.add(searchButton);
        topPanel.add(deleteButton);
        topPanel.add(graphButton);
        topPanel.add(exportButton);
        add(topPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new Object[][]{}, new String[]{"Detecções", "Total", "Data/Hora"});
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        datePicker = criarDatePicker();
        topPanel.add(datePicker);

        dateDialog = new JDialog(this, "Selecione a Data", true);
        dateDialog.setSize(300, 300);
        dateDialog.add(datePicker);
        dateDialog.setLocationRelativeTo(this);

        filterComboBox.addActionListener(e -> {
            String selectedFilter = (String) filterComboBox.getSelectedItem();
            if ("Por dia específico".equals(selectedFilter)) {
                dateDialog.setVisible(true);
            }
        });

        searchButton.addActionListener(e -> buscarRegistros());
        deleteButton.addActionListener(e -> deletarRegistros());
        graphButton.addActionListener(e -> {
            String selectedFilter = (String) filterComboBox.getSelectedItem();

            if (selectedFilter == null || selectedFilter.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, selecione uma opção para gerar o gráfico.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if ("Por dia específico".equals(selectedFilter)) {
                Object selectedObject = datePicker.getModel().getValue();

                if (selectedObject != null && selectedObject instanceof java.util.Date) {
                    java.util.Date selectedDate = (java.util.Date) selectedObject;

                    java.sql.Date sqlDate = new java.sql.Date(selectedDate.getTime());

                    abrirTelaGrafico(selectedFilter, sqlDate);
                } else {
                    JOptionPane.showMessageDialog(this, "Por favor, selecione uma data válida.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                abrirTelaGrafico(selectedFilter, null);
            }
        });
        exportButton.addActionListener(e -> gerarExcel());
    }

    private JDatePickerImpl criarDatePicker() {
        SqlDateModel model = new SqlDateModel();
        Properties properties = new Properties();
        properties.put("text.today", "Hoje");
        properties.put("text.month", "Mês");
        properties.put("text.year", "Ano");
        JDatePanelImpl datePanel = new JDatePanelImpl(model, properties);
        JDatePickerImpl datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());

        datePicker.getJFormattedTextField().setBackground(Color.LIGHT_GRAY);
        datePicker.getJFormattedTextField().setForeground(Color.BLUE);

        customizeDatePanel(datePanel);

        return datePicker;
    }

    private void customizeDatePanel(JDatePanelImpl datePanel) {
        for (Component component : datePanel.getComponents()) {
            if (component instanceof JPanel) {
                JPanel panel = (JPanel) component;
                panel.setBackground(Color.LIGHT_GRAY);
                customizePanel(panel);
            } else if (component instanceof JButton) {
                JButton button = (JButton) component;
                button.setBackground(Color.LIGHT_GRAY);
                button.setForeground(Color.BLUE);
            }
        }
    }

    private void customizePanel(JPanel panel) {
        for (Component component : panel.getComponents()) {
            if (component instanceof JLabel) {
                JLabel label = (JLabel) component;
                label.setForeground(Color.BLACK);
            } else if (component instanceof JComponent) {
                component.setBackground(Color.WHITE);
                if (component instanceof JPanel) {
                    customizePanel((JPanel) component);
                }
            }
        }
    }



    private void buscarRegistros() {
        String selectedFilter = (String) filterComboBox.getSelectedItem();

        if (selectedFilter == null || selectedFilter.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione uma opção para buscar.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        tableModel.setRowCount(0);

        if ("Dia atual".equals(selectedFilter)) {
            configurarTabelaDiaAtual();
        } else if ("Semana atual".equals(selectedFilter)) {
            configurarTabelaSemanaAtual();
        } else if ("Mês a mês".equals(selectedFilter)) {
            configurarTabelaMesAMes();
        } else if ("Por dia específico".equals(selectedFilter)) {
            Object selectedObject = datePicker.getModel().getValue();

            if (selectedObject != null && selectedObject instanceof java.util.Date) {
                java.util.Date selectedDate = (java.util.Date) selectedObject;

                java.sql.Date sqlDate = new java.sql.Date(selectedDate.getTime());

                configurarTabelaPorDiaEspecifico(sqlDate);
            } else {
                System.out.println("Data inválida selecionada.");
            }
        }
    }


    private void configurarTabelaPorDiaEspecifico(java.sql.Date sqlDate) {
        tableModel.setColumnCount(2);
        tableModel.setColumnIdentifiers(new String[]{"Registros", "Data/Hora"});

        List<Registro> registros = arduinoDAO.getComandosDiaEspecifico(sqlDate);

        for (Registro registro : registros) {
            addRecord(registro.getComando(), registro.getDataHora(), "");        }
    }
    private void addRecord(String command, String total, String dateTime) {
        tableModel.addRow(new Object[]{command, total, dateTime});
        tableModel.fireTableDataChanged();
    }
    private void configurarTabelaDiaAtual() {
        tableModel.setColumnCount(2);
        tableModel.setColumnIdentifiers(new String[]{"Data/Hora", "Total"});

        Map<String, Integer> registrosMap = arduinoDAO.getComandosPorMinuto();
        for (Map.Entry<String, Integer> entry : registrosMap.entrySet()) {
            String minuto = entry.getKey();
            Integer total = entry.getValue();
            addRecord(minuto, String.valueOf(total), "");
        }
    }

    private void configurarTabelaSemanaAtual() {
        tableModel.setColumnCount(2);
        tableModel.setColumnIdentifiers(new String[]{"Dia da Semana", "Total"});

        List<ComandoPorDia> comandoPorDia = arduinoDAO.getComandosUltimos7Dias();
        for (ComandoPorDia comando : comandoPorDia) {
            addRecord(comando.getDiaDaSemana(), String.valueOf(comando.getTotal()), "");
        }
    }

    private void configurarTabelaMesAMes() {
        tableModel.setColumnCount(2);
        tableModel.setColumnIdentifiers(new String[]{"Mês", "Total Detecções"});

        List<ComandoPorDia> comandoPorDia = arduinoDAO.getComandosMes();
        for (ComandoPorDia comando : comandoPorDia) {
            addRecord(comando.getDiaDaSemana(), String.valueOf(comando.getTotal()), "");
        }
    }


    private void deletarRegistros() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            Object idObject = table.getValueAt(selectedRow, 0);

            if (idObject instanceof String) {
                try {
                    int id = Integer.parseInt((String) idObject);

                    arduinoDAO.deleteCommand(id);

                    tableModel.removeRow(selectedRow);
                    JOptionPane.showMessageDialog(this, "Registro excluído com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Erro ao excluir o registro. ID inválido.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Por favor, selecione um registro para excluir.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirTelaGrafico(String tipoGrafico, java.sql.Date dataSelecionada) {
        boolean hasData = false;

        if ("Por dia específico".equals(tipoGrafico)) {
            if (dataSelecionada != null) {
                hasData = !arduinoDAO.getComandosDiaEspecifico(dataSelecionada).isEmpty();
            }
        } else if ("Dia atual".equals(tipoGrafico)) {
            hasData = !arduinoDAO.getComandosPorMinuto().isEmpty();
        } else if ("Semana atual".equals(tipoGrafico)) {
            hasData = !arduinoDAO.getComandosUltimos7Dias().isEmpty();
        } else if ("Mês a mês".equals(tipoGrafico)) {
            hasData = !arduinoDAO.getComandosMes().isEmpty();
        }

        if (hasData) {
            GraficoTela graficoTela = new GraficoTela(tipoGrafico, dataSelecionada);
            graficoTela.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Não há dados para o gráfico.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }


    public void gerarExcel() {
        String selectedFilter = (String) filterComboBox.getSelectedItem();
        if (selectedFilter == null || selectedFilter.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione um filtro de dados antes de exportar.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Não há dados para exportar.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Salvar como");
        fileChooser.setSelectedFile(new File("TabelaRegistros.xlsx"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();

            if (!fileToSave.getName().toLowerCase().endsWith(".xlsx")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".xlsx");
            }

            try (Workbook workbook = new XSSFWorkbook();
                 FileOutputStream fileOut = new FileOutputStream(fileToSave)) {

                Sheet sheet = workbook.createSheet("Registros");

                Row headerRow = sheet.createRow(0);
                for (int i = 0; i < tableModel.getColumnCount(); i++) {
                    headerRow.createCell(i).setCellValue(tableModel.getColumnName(i));
                }

                for (int row = 0; row < tableModel.getRowCount(); row++) {
                    Row dataRow = sheet.createRow(row + 1);
                    for (int col = 0; col < tableModel.getColumnCount(); col++) {
                        Object value = tableModel.getValueAt(row, col);
                        dataRow.createCell(col).setCellValue(value != null ? value.toString() : "");
                    }
                }

                workbook.write(fileOut);
                JOptionPane.showMessageDialog(this, "Exportação concluída com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erro ao exportar para Excel.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
