package com.example.borja.practicastta;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
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

        String []opciones={"Versión de la app","Listado de componentes","Opciones del menú","42","La buena ;)"};
//        Test test=new Test();
//        test.setWording("La buena ;)");

        for(int i=0;i<opciones.length;i++) {
            RadioGroup group = (RadioGroup) findViewById(R.id.test_choices);
            RadioButton radio = new RadioButton(this);
            radio.setText(opciones[i]);
            radio.setOnClickListener(this);
            group.addView(radio);
            Toast.makeText(getApplicationContext(), "Creado!", Toast.LENGTH_SHORT).show();
        }

    }
    @Override
    public void onClick(View v){
        Toast.makeText(getApplicationContext(),"onCLick!", Toast.LENGTH_SHORT).show();
        findViewById(R.id.button_sendTest).setVisibility(View.VISIBLE);
    }

    public void corregir(View view){
        int correct=3;

        RadioGroup group = (RadioGroup)findViewById(R.id.test_choices);
        View selected=group.findViewById(group.getCheckedRadioButtonId());
        int selectedNum = group.indexOfChild(selected);
        View correctOption=group.getChildAt(correct);
        int choices = group.getChildCount();
        for (int i=0;i<choices;i++){
            group.getChildAt(i).setEnabled(false);
        }
        LinearLayout layout = (LinearLayout)findViewById(R.id.activity_nuevo_test);
        layout.removeView(findViewById(R.id.button_sendTest));
        group.getChildAt(correct).setBackgroundColor(Color.GREEN);
        if (selectedNum!=correct){
            selected.setBackgroundColor(Color.RED);
            Toast.makeText(getApplicationContext(),"Has fallado!", Toast.LENGTH_SHORT).show();
            //if(advise !=null && !advise.isEmpty){
                findViewById(R.id.button_verAyuda).setVisibility(View.VISIBLE);
            //}
        }
        else{
            Toast.makeText(getApplicationContext(),"Correcto!", Toast.LENGTH_SHORT).show();
        }

    }

    public void verAyuda(View view){
        WebView web = new WebView(this);
        String advise ="Ayuda <b>muy</b> util";
        web.loadData(advise,"text/html",null);
        web.setBackgroundColor(Color.TRANSPARENT);
        web.setLayerType(WebView.LAYER_TYPE_SOFTWARE,null);
        LinearLayout layout = (LinearLayout)findViewById(R.id.activity_nuevo_test);
        layout.removeView(findViewById(R.id.button_sendTest));
        layout.addView(web);
    }
}
