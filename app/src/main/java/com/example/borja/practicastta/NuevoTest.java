package com.example.borja.practicastta;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.borja.practicastta.model.AudioPlayer;
import com.example.borja.practicastta.model.Ejercicio;
import com.example.borja.practicastta.model.Opcion;
import com.example.borja.practicastta.model.ProgressTask;
import com.example.borja.practicastta.model.RestClient;
import com.example.borja.practicastta.model.Test;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

public class NuevoTest extends AppCompatActivity implements View.OnClickListener{

    private RestClient rest;
    private Test test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_test);
        rest= new RestClient(getString(R.string.server_url));
        rest.setHttpBasicAuth("12345678A","tta");
        if(RestClient.getConnectivity(this)) {
            try {
                new ProgressTask<Test>(this) {
                    @Override
                    protected Test work() throws Exception {
                        return getTest(1);
                    }

                    @Override
                    protected void onFinish(Test result) {
                        test=result;
                        TextView textView = (TextView) findViewById(R.id.enunciado);
                        textView.setText(test.getWording());
                        drawOpciones();

                    }
                }.execute();
            } catch (Exception e) {
                Toast.makeText(this, R.string.error_ejercicio, Toast.LENGTH_SHORT).show();
                Toast.makeText(this, e.toString(),Toast.LENGTH_LONG).show();
            }
        }
        else
            Toast.makeText(this, R.string.no_internet,Toast.LENGTH_SHORT).show();


    }

    public Test getTest(int id){
        try {
            Test test = new Test();
            JSONObject json = rest.getJson(String.format("getTest?id=%d",id));
            test.setWording(json.getString("wording"));
            JSONArray array = json.getJSONArray("choices");
            for(int i =0;i<array.length();i++){
                JSONObject item = array.getJSONObject(i);
                Opcion opcion= new Opcion();
                opcion.setId(item.getInt("id"));
                opcion.setEnunciado(item.optString("answer"));
                opcion.setCoorecta(item.getBoolean("correct"));
                opcion.setAdvise(item.optString("advise",null));
                if(item.optJSONObject("resourceType")!=null)
                    opcion.setAyudaType(item.getJSONObject("resourceType").optString("mime",null));
                else
                    opcion.setAyudaType("none");
                test.addOpcion(opcion);
            }
            return test;
        }
        catch (Exception e){
            return null;
        }
    }

    public int uploadResult(int choiceId){
        try {
            JSONObject json = new JSONObject();
            json.put("userId", 1);
            json.put("choiceId",choiceId);
            return rest.postJson(json,"postChoice");

        }
        catch (Exception e){
            return -1;
        }

    }

    public void drawOpciones(){
        if(test==null){
            Toast.makeText(getApplicationContext(), "test es null", Toast.LENGTH_SHORT).show();
        }
        if(test.getOpciones()==null){
            Toast.makeText(getApplicationContext(), "opciones es null", Toast.LENGTH_SHORT).show();
        }
        List<Opcion> opciones=test.getOpciones();
        for(int i=0;i<opciones.size();i++) {
            RadioGroup group = (RadioGroup) findViewById(R.id.test_choices);
            RadioButton radio = new RadioButton(this);
            radio.setText(opciones.get(i).getEnunciado());
            radio.setOnClickListener(this);
            group.addView(radio);

        }

    }
    @Override
    public void onClick(View v){
        //Toast.makeText(getApplicationContext(),"onCLick!", Toast.LENGTH_SHORT).show();
        findViewById(R.id.button_sendTest).setVisibility(View.VISIBLE);
    }

    public void corregir(View view){
        int correct=test.getCorrect();
        RadioGroup group = (RadioGroup)findViewById(R.id.test_choices);
        View selected=group.findViewById(group.getCheckedRadioButtonId());
        final int selectedNum = group.indexOfChild(selected);
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
            String advise=test.getOpciones().get(selectedNum).getAdvise();
            if(advise !=null && !advise.isEmpty()){
                findViewById(R.id.button_verAyuda).setVisibility(View.VISIBLE);
            }
        }
        else{
            Toast.makeText(getApplicationContext(),"Correcto!", Toast.LENGTH_SHORT).show();
        }

        if(RestClient.getConnectivity(this)) {
            try {
                new ProgressTask<Integer>(this) {
                    @Override
                    protected Integer work() throws Exception {
                        int response=uploadResult(selectedNum+1);
                        return response;
                    }

                    @Override
                    protected void onFinish(Integer result) {
                        int response=result;
                        Log.i("Server response",Integer.toString(response));
                    }
                }.execute();
            } catch (Exception e) {
                Toast.makeText(this, R.string.error_ejercicio, Toast.LENGTH_SHORT).show();
                Toast.makeText(this, e.toString(),Toast.LENGTH_LONG).show();
            }
        }
        else
            Toast.makeText(this, R.string.no_internet,Toast.LENGTH_SHORT).show();

    }

    public void verAyuda(View view){
        RadioGroup group = (RadioGroup)findViewById(R.id.test_choices);
        View selected=group.findViewById(group.getCheckedRadioButtonId());
        int selectedNum = group.indexOfChild(selected);
        String type=test.getOpciones().get(selectedNum).getAyudaType();
        String advise=test.getOpciones().get(selectedNum).getAdvise();
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
            case "video/mp4":
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
            case "audio/mpeg":
                AudioPlayer audio=new AudioPlayer(findViewById(R.id.activity_nuevo_test));
                try {
                    audio.setAudioUri(Uri.parse(advise));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
        layout.removeView(findViewById(R.id.button_verAyuda));

    }
}
