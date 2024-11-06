package dao;

import model.ComandoPorDia;
import model.Registro;
import org.jfree.data.category.DefaultCategoryDataset;
import util.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class GraficoDAO {
    private ArduinoDAO arduinoDAO;

    public GraficoDAO(ArduinoDAO arduinoDAO) {
        this.arduinoDAO = arduinoDAO;
    }

    public DefaultCategoryDataset getTotalComandosPorMinuto(String dataSelecionada) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Map<String, Integer> comandosPorMinuto = arduinoDAO.getComandosPorMinuto();

        List<String> sortedKeys = new ArrayList<>(comandosPorMinuto.keySet());

        Collections.sort(sortedKeys);

        for (String key : sortedKeys) {
            dataset.addValue(comandosPorMinuto.get(key), "Total Detecções", key);
        }

        return dataset;
    }

    public Map<String, Integer> getTotalComandosPorDia() {
        Map<String, Integer> totalPorDia = new HashMap<>();

        List<Registro> registros = arduinoDAO.getAllRecords();

        for (Registro registro : registros) {
            String dia = registro.getDataHora().split(" ")[0];
            totalPorDia.put(dia, totalPorDia.getOrDefault(dia, 0) + 1);
        }

        return totalPorDia;
    }

    public DefaultCategoryDataset getTotalComandosMes() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        List<ComandoPorDia> comandosPorMes = arduinoDAO.getComandosMes();

        int totalComandos = 0;

        for (ComandoPorDia comando : comandosPorMes) {
            int totalMes = comando.getTotal();
            dataset.addValue(totalMes, "Total Detecções", comando.getDiaDaSemana());

            totalComandos += totalMes;
        }

        System.out.println("Total acumulado de detecções: " + totalComandos);

        return dataset;
    }
    public DefaultCategoryDataset getComandosUltimos7Dias() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        List<ComandoPorDia> comandoPorDias = arduinoDAO.getComandosUltimos7Dias();

        for (ComandoPorDia comandoPorDia : comandoPorDias) {
            dataset.addValue(comandoPorDia.getTotal(), "Total Detecções", comandoPorDia.getDiaDaSemana());
        }

        return dataset;
    }
    public DefaultCategoryDataset getComandosPorDiaEspecifico(java.sql.Date date) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        List<Registro> registros = arduinoDAO.getComandosDiaEspecifico(date);

        Map<String, Integer> comandosPorMinuto = new HashMap<>();

        for (Registro registro : registros) {
            String horaMinuto = registro.getDataHora().split(" ")[1].substring(0, 5);

            comandosPorMinuto.put(horaMinuto, comandosPorMinuto.getOrDefault(horaMinuto, 0) + 1);
        }

        List<String> sortedHoraMinuto = new ArrayList<>(comandosPorMinuto.keySet());
        Collections.sort(sortedHoraMinuto);

        for (String horaMinuto : sortedHoraMinuto) {
            dataset.addValue(comandosPorMinuto.get(horaMinuto), "Total Detecções", horaMinuto);
        }

        return dataset;
    }
}
