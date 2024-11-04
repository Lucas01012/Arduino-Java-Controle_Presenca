package model;

public class ComandoPorDia {
    private String diaDaSemana;
    private int total;

    public ComandoPorDia(String diaDaSemana, int total) {
        this.diaDaSemana = diaDaSemana;
        this.total = total;
    }

    public String getDiaDaSemana() {
        return diaDaSemana;
    }

    public int getTotal() {
        return total;
    }
}