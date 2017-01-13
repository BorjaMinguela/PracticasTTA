package com.example.borja.practicastta.model;

import com.example.borja.practicastta.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by borja on 13/01/17.
 */

public class Ejercicio {
    private int id;
    private String wording;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWording() {
        return wording;
    }

    public void setWording(String wording) {
        this.wording = wording;
    }
}
