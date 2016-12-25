package com.example.borja.practicastta;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.borja.practicastta.model.Opcion;
import com.example.borja.practicastta.model.Test;

import java.util.List;

public class NuevoTest extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_test);

        Test test=this.getTest();
        if(test==null){
            Toast.makeText(getApplicationContext(), "test es null", Toast.LENGTH_SHORT).show();
        }
        if(test.getOpciones()==null){
            Toast.makeText(getApplicationContext(), "opciones es null", Toast.LENGTH_SHORT).show();
        }
        List<Opcion> opciones=getTest().getOpciones();
        //Test test=new Test();
        //test.setWording("La buena ;)");

        for(int i=0;i<opciones.size();i++) {
            RadioGroup group = (RadioGroup) findViewById(R.id.test_choices);
            RadioButton radio = new RadioButton(this);
            radio.setText(opciones.get(i).getEnunciado());
            radio.setOnClickListener(this);
            group.addView(radio);

        }

    }

    public Test getTest(){//Datos del servidor hardcodeados
        Test test=new Test();
        String []opciones={"Versión de la app","Listado de componentes","Opciones del menú","42","La buena ;)"};
        String []tipo={"text/html","text/html","video","correcta","text/html"};
        String []advise={"Ayuda <b>muy</b> util","https://developer.android.com/training/index.html?hl=es","http://www.androidbegin.com/tutorial/AndroidCommercial.3gp","correcta","text"};
        for (int i=0;i<opciones.length;i++){
            Opcion opcion = new Opcion();
            opcion.setEnunciado(opciones[i]);
            opcion.setAyudaType(tipo[i]);
            opcion.setAdvise(advise[i]);
            test.addOpcion(opcion);
        }

        return test;
    }
    @Override
    public void onClick(View v){
        //Toast.makeText(getApplicationContext(),"onCLick!", Toast.LENGTH_SHORT).show();
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
            String advise=getTest().getOpciones().get(selectedNum).getAdvise();
            if(advise !=null && !advise.isEmpty()){
                findViewById(R.id.button_verAyuda).setVisibility(View.VISIBLE);
            }
        }
        else{
            Toast.makeText(getApplicationContext(),"Correcto!", Toast.LENGTH_SHORT).show();
        }

    }

    public void verAyuda(View view){
        RadioGroup group = (RadioGroup)findViewById(R.id.test_choices);
        View selected=group.findViewById(group.getCheckedRadioButtonId());
        int selectedNum = group.indexOfChild(selected);
        String type=getTest().getOpciones().get(selectedNum).getAyudaType();
        String advise=getTest().getOpciones().get(selectedNum).getAdvise();
        LinearLayout layout = (LinearLayout) findViewById(R.id.activity_nuevo_test);
        switch (type){
            case "text/html":
                if (!advise.substring(0,10).contains("://")) {
                    WebView web = new WebView(this);

                    web.loadData(advise, "text/html", null);
                    web.setBackgroundColor(Color.TRANSPARENT);
                    web.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);

                    layout.removeView(findViewById(R.id.button_sendTest));
                    layout.addView(web);
                }
                else{
                    Uri uri=Uri.parse(advise);
                    Intent intent=new Intent(Intent.ACTION_VIEW,uri);
                    startActivity(intent);
                }
                break;
            case "video":
                VideoView video= new VideoView(this);
                video.setVideoURI(Uri.parse(advise));
                //ViewGroup.LayoutParams params = new ViewGroup.LayoutParams()
                MediaController controller = new MediaController(this){

                    @Override
                    public void hide(){

                    }
                    @Override
                    public boolean dispatchKeyEvent(KeyEvent event){
                        if(event.getKeyCode()==KeyEvent.KEYCODE_BACK) finish();
                        return super.dispatchKeyEvent(event);
                    }

                };
                controller.setAnchorView(video);
                video.setMediaController(controller);
                layout.addView(video);
                break;
            case "audio":
                //AudioPlayer audio=new AudioPlayer(this);
                break;
        }
        layout.removeView(findViewById(R.id.button_verAyuda));

    }
}
