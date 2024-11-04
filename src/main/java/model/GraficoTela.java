package model;

import dao.ArduinoDAO;
import dao.GraficoDAO;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import java.util.List;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class GraficoTela extends JFrame {
    private JComboBox<String> tipoGraficoComboBox;
    private JButton gerarGraficoButton;
    private GraficoDAO graficoDAO;
    private ArduinoDAO arduinoDAO;

    public GraficoTela() {
        arduinoDAO = new ArduinoDAO();
        graficoDAO = new GraficoDAO(arduinoDAO);

        setTitle("Gráfico de Comandos");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        JPanel filterPanel = new JPanel();
        tipoGraficoComboBox = new JComboBox<>(new String[]{"Total do dia", "Últimos 7 dias"});
        gerarGraficoButton = new JButton("Gerar Gráfico");

        filterPanel.add(new JLabel("Tipo de Gráfico: "));
        filterPanel.add(tipoGraficoComboBox);
        filterPanel.add(gerarGraficoButton);

        add(filterPanel, BorderLayout.NORTH);

        gerarGraficoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gerarGrafico(tipoGraficoComboBox.getSelectedItem().toString());
            }
        });

    }

    private void gerarGrafico(String tipoGrafico) {

        if ("Total do dia".equals(tipoGrafico)) {
            String dataSelecionada = "2024-11-03";
            DefaultCategoryDataset dataset = graficoDAO.getTotalComandosPorMinuto(dataSelecionada);
            criarGrafico(dataset, "Total de detecções", "Hora/Minuto", "Quantidade");
        }
        if ("Últimos 7 dias".equals(tipoGrafico)) {
            String diaSelecionado = "Monday";
            DefaultCategoryDataset dataset = graficoDAO.getComandosUltimos7Dias();
            criarGrafico(dataset, "Total de detecções no últimos 7  dias", "Dia", "Quantidade");
        }

    }

    private void criarGrafico(DefaultCategoryDataset dataset, String titulo, String eixoX, String eixoY) {
        JFreeChart chart = ChartFactory.createBarChart(
                titulo,
                eixoX,
                eixoY,
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        CategoryAxis xAxis = chart.getCategoryPlot().getDomainAxis();
        xAxis.setCategoryLabelPositions(CategoryLabelPositions.STANDARD); // Mantém as posições padrão
        xAxis.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 10));

        // Configurações do eixo Y
        NumberAxis yAxis = (NumberAxis) chart.getCategoryPlot().getRangeAxis();
        yAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        // Ajuste para o tipo de gráfico
        String tipoGrafico = (String) tipoGraficoComboBox.getSelectedItem();
        if ("Total do dia".equals(tipoGrafico)) {
            xAxis.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 10));
        } else if ("Últimos 7 dias".equals(tipoGrafico)) {
            xAxis.setTickLabelFont(new Font("SansSerif", Font.BOLD, 12));
        }

        // Criação do painel do gráfico
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 500));
        setContentPane(chartPanel);
        revalidate();
    }
}