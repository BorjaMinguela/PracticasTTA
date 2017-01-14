package com.example.borja.practicastta.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by borja on 23/12/16.
 */

public class Test {
    private String wording;
    private List<Opcion> opciones=new ArrayList<>();


    public List<Opcion> getOpciones() {
        return opciones;
    }

    public void addOpcion(Opcion opcion) {
        this.opciones.add(opcion);
    }

    public String getWording() {
        return wording;
    }

    public void setWording(String wording) {
        this.wording = wording;
    }

    public int getCorrect(){
        int correct = -1;
        for(int i=0;i<opciones.size();i++){
            if(opciones.get(i).isCoorecta())
                correct=i;
        }
        return correct;
    }

}

