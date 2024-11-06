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
import java.util.*;

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

    public List<ComandoPorDia> getComandosMes() {
        List<ComandoPorDia> registros = new ArrayList<>();
        String sql = "SELECT MONTH(data_hora) AS mes, COUNT(*) AS total " +
                "FROM comandos " +
                "WHERE data_hora >= NOW() - INTERVAL 12 MONTH " +
                "GROUP BY mes " +
                "ORDER BY mes";

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int mes = resultSet.getInt("mes");
                int total = resultSet.getInt("total");
                String nomeMes = getNomeMes(mes);
                registros.add(new ComandoPorDia(nomeMes, total));
            }

        } catch (SQLException e) {
            System.err.println("Erro SQL: " + e.getMessage());
            e.printStackTrace();
        }

        return registros;
    }


    private String getNomeMes(int mesAno){
        switch (mesAno){
            case 1: return "Janeiro";
            case 2: return "Fevereiro";
            case 3: return "Março";
            case 4: return "Abril";
            case 5: return "Maio";
            case 6: return "Junho";
            case 7: return "Julho";
            case 8: return "Agosto";
            case 9: return "Setembro";
            case 10: return "Outubro";
            case 11: return "Novembro";
            case 12: return "Dezembro";
            default: return "";
        }
    }


    public List<ComandoPorDia> getComandosUltimos7Dias() {
        List<ComandoPorDia> registros = new ArrayList<>();
        String sql = "SELECT DAYOFWEEK(data_hora) AS dia_da_semana, COUNT(*) AS total " +
                "FROM comandos " +
                "WHERE WEEK(data_hora) = WEEK(CURDATE()) " +
                "AND YEAR(data_hora) = YEAR(CURDATE()) " +
                "GROUP BY DAYOFWEEK(data_hora) " +
                "ORDER BY DAYOFWEEK(data_hora)";

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

    public List<Registro> getComandosDiaEspecifico(java.sql.Date date) {
        List<Registro> registros = new ArrayList<>();
        String sql = "SELECT id , comando, data_hora FROM comandos WHERE DATE(data_hora) = ?";

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setDate(1, date);

            try (ResultSet resultSet = statement.executeQuery()) {
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
