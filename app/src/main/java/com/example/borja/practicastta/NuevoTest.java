package com.example.borja.practicastta;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
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
        int correct=4;

        RadioGroup group = (RadioGroup)findViewById(R.id.test_choices);
        View selected = group.findViewById(group.getCheckedRadioButtonId());
        View correctOption=group.getChildAt(correct);
        int choices = group.getChildCount();
        for (int i=0;i<choices;i++){
            group.getChildAt(i).setEnabled(false);
        }
        LinearLayout layout = (LinearLayout)findViewById(R.id.activity_nuevo_test);
        layout.removeView(findViewById(R.id.button_sendTest));
        group.getChildAt(correct).setBackgroundColor(Color.GREEN);
        if (selected.equals(correctOption)){
            selected.setBackgroundColor(Color.RED);
        }
        Toast.makeText(getApplicationContext(),"Corregir", Toast.LENGTH_SHORT).show();
    }
}
