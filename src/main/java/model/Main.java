package model;

import dao.ArduinoDAO;
import view.InterfacePrincipal;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        ArduinoDAO arduinoDAO = new ArduinoDAO();
        new Thread(() -> {
            ConexaoArduino conexaoArduino  = new ConexaoArduino(arduinoDAO);
            conexaoArduino.iniciarConexao();
        }).start();

    SwingUtilities.invokeLater(()->{
        InterfacePrincipal principal = new InterfacePrincipal();
        principal.setVisible(true);

    });
    }
}

