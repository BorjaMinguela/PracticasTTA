package com.example.borja.practicastta.model;

public class Opcion{

    private int id;
    private String enunciado;
    private String ayudaType;
    private String advise;
    private boolean coorecta;


    public String getEnunciado() {
        return enunciado;
    }

    public void setEnunciado(String enunciado) {
        this.enunciado = enunciado;
    }

    public String getAyudaType() {
        return ayudaType;
    }

    public void setAyudaType(String ayudaType) {
        this.ayudaType = ayudaType;
    }

    public String getAdvise() {
        return advise;
    }

    public void setAdvise(String advise) {
        this.advise = advise;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isCoorecta() {
        return coorecta;
    }

    public void setCoorecta(boolean coorecta) {
        this.coorecta = coorecta;
    }
}
