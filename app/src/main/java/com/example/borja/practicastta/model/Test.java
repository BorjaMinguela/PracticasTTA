package com.example.borja.practicastta.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by borja on 23/12/16.
 */

public class Test {
    private List<Opcion> opciones=new ArrayList<>();


    public List<Opcion> getOpciones() {
        return opciones;
    }

    public void addOpcion(Opcion opcion) {
        this.opciones.add(opcion);
    }
}

