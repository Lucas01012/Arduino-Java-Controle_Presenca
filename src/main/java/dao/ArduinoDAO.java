package dao;

import model.ComandoPorDia;
import model.Registro;
import util.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArduinoDAO {
    public void insertCommand(String command) {
        try (Connection connection = ConnectionFactory.getConnection()) {
            String sql = "INSERT INTO comandos (comando, data_hora, dia_da_semana) VALUES (?, NOW(), ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, command);

                DayOfWeek diaSemana = LocalDateTime.now().getDayOfWeek();
                statement.setString(2, diaSemana.toString());

                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteCommand(int id) {
        try (Connection connection = ConnectionFactory.getConnection()) {
            String sql = "DELETE FROM comandos WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, id);
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Registro> getAllRecords() {
        List<Registro> registros = new ArrayList<>();

        try (Connection connection = ConnectionFactory.getConnection()) {
            String sql = "SELECT * FROM comandos";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String comando = resultSet.getString("comando");
                    String dataHora = resultSet.getString("data_hora");
                    registros.add(new Registro(id, comando, dataHora));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return registros;
    }


    public List<ComandoPorDia> getComandosUltimos7Dias() {
        List<ComandoPorDia> registros = new ArrayList<>();
        String sql = "SELECT DAYOFWEEK(data_hora) AS dia_da_semana, COUNT(*) AS total " +
                "FROM comandos " +
                "WHERE data_hora >= NOW() - INTERVAL 7 DAY " +
                "GROUP BY dia_da_semana " +
                "ORDER BY dia_da_semana";

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int diaSemana = resultSet.getInt("dia_da_semana");
                int total = resultSet.getInt("total");

                String nomeDia = getNomeDia(diaSemana);

                registros.add(new ComandoPorDia(nomeDia, total));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return registros;
    }

    private String getNomeDia(int diaSemana) {
        switch (diaSemana) {
            case 1: return "Domingo";
            case 2: return "Segunda";
            case 3: return "Terca";
            case 4: return "Quarta";
            case 5: return "Quinta";
            case 6: return "Sexta";
            case 7: return "Sabado";
            default: return "";
        }
    }

    public Map<String, Integer> getComandosPorMinuto() {
        Map<String, Integer> comandosPorMinuto = new HashMap<>();
        String sql = "SELECT DATE_FORMAT(data_hora, '%H:%i') AS minuto, COUNT(*) AS total " +
                "FROM comandos " +
                "WHERE DATE(data_hora) = CURDATE() " +
                "GROUP BY minuto";

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String minuto = resultSet.getString("minuto");
                int total = resultSet.getInt("total");
                comandosPorMinuto.put(minuto, total);
            }

            if (comandosPorMinuto.isEmpty()) {
                System.out.println("Nenhum dado encontrado para o dia atual.");
            } else {
                System.out.println("Dados retornados: " + comandosPorMinuto);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return comandosPorMinuto;
    }
}
