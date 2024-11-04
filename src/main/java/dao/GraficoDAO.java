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
            dataset.addValue(comandosPorMinuto.get(key), "Comandos", key);
        }

        return dataset;
    }


    public DefaultCategoryDataset getComandosUltimos7Dias() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        List<ComandoPorDia> comandoPorDias = arduinoDAO.getComandosUltimos7Dias();

        Map<String, Integer> totalPorDia = new LinkedHashMap<>();
        totalPorDia.put("Domingo", 0);
        totalPorDia.put("Segunda", 0);
        totalPorDia.put("Terça", 0);
        totalPorDia.put("Quarta", 0);
        totalPorDia.put("Quinta", 0);
        totalPorDia.put("Sexta", 0);
        totalPorDia.put("Sábado", 0);

        for (ComandoPorDia comandoPorDia : comandoPorDias) {
            totalPorDia.put(comandoPorDia.getDiaDaSemana(), comandoPorDia.getTotal());
        }

        for (Map.Entry<String, Integer> entry : totalPorDia.entrySet()) {
            dataset.addValue(entry.getValue(), "Comandos", entry.getKey());
        }

        return dataset;
    }
}
