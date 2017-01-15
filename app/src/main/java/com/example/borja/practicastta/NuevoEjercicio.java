package com.example.borja.practicastta;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.borja.practicastta.model.Ejercicio;
import com.example.borja.practicastta.model.ProgressTask;
import com.example.borja.practicastta.model.RestClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class NuevoEjercicio extends AppCompatActivity {
    final int READ_REQUEST_CODE=1;
    final int VIDEO_REQUEST_CODE=2;
    final int AUDIO_REQUEST_CODE=3;
    final int PICTURE_REQUEST_CODE=4;

    Uri mediaUri;

    private RestClient rest;


    public Ejercicio getEjercicio(int id) throws IOException,JSONException{
        JSONObject json = rest.getJson(String.format("getExercise?id=%d",id));
        Ejercicio ejercicio= new Ejercicio();
        ejercicio.setId(json.getInt("id"));
        ejercicio.setWording(json.getString("wording"));
        return ejercicio;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_ejercicio);
        rest= new RestClient(getString(R.string.server_url));
        rest.setHttpBasicAuth("12345678A","tta");
        if(RestClient.getConnectivity(this)) {
            try {
                new ProgressTask<Ejercicio>(this) {
                    @Override
                    protected Ejercicio work() throws Exception {
                        return getEjercicio(1);
                    }

                    @Override
                    protected void onFinish(Ejercicio result) {
                        TextView textView = (TextView) findViewById(R.id.enunciadoEjercicio);
                        String wording =result.getWording();
                        //Toast.makeText(context, wording, Toast.LENGTH_SHORT).show();
                        textView.setText(result.getWording());

                    }
                }.execute();
                //Ejercicio ejercicio = this.getEjercicio(1);
                //TextView textView = (TextView) findViewById(R.id.enunciadoEjercicio);
                //textView.setText(ejercicio.getWording());
            } catch (Exception e) {
                Toast.makeText(this, R.string.error_ejercicio, Toast.LENGTH_SHORT).show();
                Toast.makeText(this, e.toString(),Toast.LENGTH_LONG).show();
            }
        }
        else
            Toast.makeText(this, R.string.no_internet,Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mediaUri != null) {
            outState.putString("cameraImageUri", mediaUri.toString());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey("cameraImageUri")) {
            mediaUri = Uri.parse(savedInstanceState.getString("cameraImageUri"));
        }
    }


    public void sacarFoto(View view){
        if(!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA))
            Toast.makeText(this,R.string.no_camera,Toast.LENGTH_SHORT).show();
        else{
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if(intent.resolveActivity(getPackageManager())!=null){
                File dir= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                try{
                    File file=File.createTempFile("tta",".jpg",dir);
                    mediaUri= Uri.fromFile(file);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT,mediaUri);
                    startActivityForResult(intent,PICTURE_REQUEST_CODE);
                }catch (IOException e){

                }
            }
            else
                Toast.makeText(this, R.string.no_app,Toast.LENGTH_SHORT).show();
        }
    }

    public void grabarVideo(View view){
        if(!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA))
            Toast.makeText(this,R.string.no_camera,Toast.LENGTH_SHORT).show();
        else{
            Intent intent=new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            if(intent.resolveActivity(getPackageManager())!=null)
                startActivityForResult(intent,VIDEO_REQUEST_CODE);
            else
                Toast.makeText(this, R.string.no_app,Toast.LENGTH_SHORT).show();

        }
    }

    public void grabarAudio(View view){
        if(!getPackageManager().hasSystemFeature(PackageManager.FEATURE_MICROPHONE))
            Toast.makeText(this, R.string.no_micro,Toast.LENGTH_SHORT).show();
        else{
            Intent intent=new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
            if(intent.resolveActivity(getPackageManager())!=null)
                startActivityForResult(intent,AUDIO_REQUEST_CODE);
            else
                Toast.makeText(this, R.string.no_app,Toast.LENGTH_SHORT).show();

        }
    }

    public void subirFichero(View view){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent,READ_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode, Intent data){
        if(resultCode != Activity.RESULT_OK)
            return;
        switch (requestCode){
            case READ_REQUEST_CODE:
            case VIDEO_REQUEST_CODE:
            case AUDIO_REQUEST_CODE:
                tratarFichero(data);
                break;
            case PICTURE_REQUEST_CODE:
                tratarMedia(mediaUri);
                Toast.makeText(this, R.string.foto_sacada,Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void tratarMedia(final Uri uri){
        final String nombre = uri.getLastPathSegment();
        if(RestClient.getConnectivity(this)) {
            try {
                new ProgressTask<Integer>(this) {
                    @Override
                    protected Integer work() throws Exception {
                        return uploadFile(getContentResolver().openInputStream(uri),nombre);
                    }

                    @Override
                    protected void onFinish(Integer result) {
                        int response=result;
                        Log.i("Respuesta",Integer.toString(response));
                    }
                }.execute();
            } catch (Exception e) {
            }
        }
        else
            Toast.makeText(this, R.string.no_internet,Toast.LENGTH_SHORT).show();
    }

    public void tratarFichero(Intent data){
        final Uri uri=data.getData();
        Toast.makeText(this,uri.toString(),Toast.LENGTH_SHORT).show();
        Cursor cursor = null;
        String size=null;
        try{
            cursor = this.getContentResolver().query(uri,null,null,null,null);
            if (cursor != null && cursor.moveToFirst()) {
                final String nombre=cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                int sizeIndex=cursor.getColumnIndex(OpenableColumns.SIZE);
                if(!cursor.isNull(sizeIndex)){
                    size=cursor.getString(sizeIndex);
                }
                else{
                    size="Unknown";
                }
                Toast.makeText(this,nombre, Toast.LENGTH_SHORT).show();
                Toast.makeText(this,size+" Bytes", Toast.LENGTH_SHORT).show();
                if(RestClient.getConnectivity(this)) {
                    try {
                        new ProgressTask<Integer>(this) {
                            @Override
                            protected Integer work() throws Exception {
                                return uploadFile(getContentResolver().openInputStream(uri),nombre);
                            }

                            @Override
                            protected void onFinish(Integer result) {
                                int response=result;
                                Log.i("Respuesta",Integer.toString(response));
                            }
                        }.execute();
                    } catch (Exception e) {
                    }
                }
                else
                    Toast.makeText(this, R.string.no_internet,Toast.LENGTH_SHORT).show();
            }
        }finally {
            cursor.close();
        }
    }

    public int uploadFile(InputStream is,String fileName){
        rest= new RestClient(getString(R.string.server_url));
        rest.setHttpBasicAuth("12345678A","tta");
        try {
            int response=rest.postFile("postExercise?user=1&id=1",is,fileName);
            return response;
        }
        catch (Exception e){
            String error= e.toString();
            Log.e("error file",error);
            return -1;
        }

    }
}
