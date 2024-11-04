package model;

import com.fazecast.jSerialComm.SerialPort;
import dao.ArduinoDAO;

import java.io.InputStream;
import java.util.Scanner;

public class ConexaoArduino {

    private final ArduinoDAO arduinoDAO;
    private SerialPort comPort;
    private volatile boolean running = true;

    public ConexaoArduino(ArduinoDAO arduinoDAO) {
        this.arduinoDAO = arduinoDAO;
    }


    public void iniciarConexao() {
        SerialPort[] availablePorts = SerialPort.getCommPorts();
        if (availablePorts.length == 0) {
            System.out.println("Nenhuma porta serial detectada.");
            return;
        }

        System.out.println("Portas seriais disponíveis:");
        for (int i = 0; i < availablePorts.length; i++) {
            System.out.println(i + ": " + availablePorts[i].getSystemPortName());
        }

        Scanner scanner = new Scanner(System.in);
        System.out.print("Selecione o índice da porta serial a ser utilizada: ");
        int portIndex = scanner.nextInt();

        comPort = availablePorts[portIndex];
        System.out.println("Tentando abrir a porta serial...");

        int baudRate = 9600;
        comPort.setComPortParameters(baudRate, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);

        if (comPort.openPort()) {
            System.out.println("Porta serial " + comPort.getSystemPortName() + " aberta com sucesso.");
        } else {
            System.out.println("Falha ao abrir a porta serial.");
            return;
        }

        comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 5000, 0);

        new Thread(() -> {
            try (InputStream inputStream = comPort.getInputStream()) {
                System.out.println("Iniciando leitura dos dados do Arduino...");
                byte[] readBuffer = new byte[1024];
                boolean firstRead = true;

                while (running && comPort.isOpen()) {
                    if (inputStream.available() > 0) {
                        int numBytes = inputStream.read(readBuffer);
                        if (numBytes > 0) {
                            String data = new String(readBuffer, 0, numBytes).trim();

                            if (firstRead) {
                                firstRead = false;
                                continue;
                            }

                            System.out.println("Dado recebido do Arduino: " + data);
                            arduinoDAO.insertCommand(data);
                        }
                    }
                    Thread.sleep(100);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (comPort.isOpen()) {
                    comPort.closePort();
                    System.out.println("Porta serial fechada.");
                }
            }
        }).start();
    }

    public void pararLeitura() {
        running = false;
        if (comPort != null && comPort.isOpen()) {
            comPort.closePort();
        }
    }
}

