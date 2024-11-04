package model;

public class Registro {
    private int id;
    private String comando;
    private String dataHora;


    public Registro(int id, String comando, String dataHora) {
        this.id = id;
        this.comando = comando;
        this.dataHora = dataHora;

    }

    public int getId() {
        return id;
    }

    public String getComando() {
        return comando;
    }

    public String getDataHora() {
        return dataHora;
    }


}