package com.example.borja.practicastta;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void newTest(View view){
        Intent intent = new Intent(this, NuevoTest.class);
        startActivity(intent);
    }
    public void newEjercicio(View view){
        Intent intent = new Intent(this, NuevoEjercicio.class);
        startActivity(intent);
    }

    }
