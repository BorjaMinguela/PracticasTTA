package com.example.borja.practicastta;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.borja.practicastta.model.Test;

public class NuevoTest extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_test);

        Test test=new Test();
        test.setWording("La buena ;)");
        RadioGroup group = (RadioGroup)findViewById(R.id.test_choices);
        RadioButton radio = new RadioButton(this);
        radio.setText(test.getWording());
        radio.setOnClickListener(this);
        group.addView(radio);
        Toast.makeText(getApplicationContext(),"Creado!", Toast.LENGTH_SHORT).show();

    }
    @Override
    public void onClick(View v){
        Toast.makeText(getApplicationContext(),"onCLick!", Toast.LENGTH_SHORT).show();
        findViewById(R.id.button_sendTest).setVisibility(View.VISIBLE);
    }

    public void corregir(View view){
        Toast.makeText(getApplicationContext(),"Corregir!", Toast.LENGTH_SHORT).show();
    }
}
