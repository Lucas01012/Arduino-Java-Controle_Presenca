package model;

import dao.ArduinoDAO;
import dao.GraficoDAO;
import view.InterfacePrincipal;
import org.jdatepicker.JDatePicker;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.*;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class GraficoTela extends JFrame {
    private GraficoDAO graficoDAO;
    private ArduinoDAO arduinoDAO;
    private java.sql.Date dataSelecionada;

    public GraficoTela(String tipoGraficoSelecionado, java.sql.Date dataSelecionada) {
        this.arduinoDAO = new ArduinoDAO();
        this.graficoDAO = new GraficoDAO(arduinoDAO);
        this.dataSelecionada = dataSelecionada;

        setTitle("Gráfico de Detecções");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        gerarGrafico(tipoGraficoSelecionado);
    }

    private void gerarGrafico(String tipoGrafico) {
        DefaultCategoryDataset dataset = null;
        String titulo = "";
        String eixoX = "";
        String eixoY = "";

        if ("Dia atual".equals(tipoGrafico)) {
            String dataSelecionada = "2024-11-03";
            dataset = graficoDAO.getTotalComandosPorMinuto(dataSelecionada);
            titulo = "Total de detecções";
            eixoX = "Hora/Minuto";
            eixoY = "Detecções";
        } else if ("Semana atual".equals(tipoGrafico)) {
            dataset = graficoDAO.getComandosUltimos7Dias();
            titulo = "Total de detecções nos últimos 7 dias";
            eixoY = "Detecções";
        } else if ("Mês a mês".equals(tipoGrafico)) {
            dataset = graficoDAO.getTotalComandosMes();
            titulo = "Total de detecções nos últimos 12 meses";
            eixoX = "Mês";
            eixoY = "Detecções";
        } else if ("Por dia específico".equals(tipoGrafico) && dataSelecionada != null) {
            dataset = graficoDAO.getComandosPorDiaEspecifico(dataSelecionada);
            titulo = "Total de detecções em " + dataSelecionada.toString();
            eixoX = "Hora/Minuto";
            eixoY = "Detecções";
        }

        criarGrafico(dataset, titulo, eixoX, eixoY);
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

        chart.setBackgroundPaint(Color.WHITE);

        CategoryPlot plot = chart.getCategoryPlot();
        CategoryAxis xAxis = plot.getDomainAxis();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.BLACK);
        plot.setRangeGridlinesVisible(true);
        xAxis.setCategoryLabelPositions(CategoryLabelPositions.STANDARD);
        xAxis.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 10));

        xAxis.setCategoryMargin(0.1);

        BarRenderer renderer = new BarRenderer();
        renderer.setSeriesPaint(0, new Color(54, 162, 235)); // Azul
        renderer.setMaximumBarWidth(0.10);

        plot.setRenderer(renderer);

        NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
        yAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        yAxis.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
        yAxis.setAutoRangeIncludesZero(true);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(1500, 600));


        setContentPane(chartPanel);
        revalidate();
    }
    }