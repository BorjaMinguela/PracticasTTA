package com.example.borja.practicastta;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class NuevoEjercicio extends AppCompatActivity {
    final int READ_REQUEST_CODE=1;
    final int VIDEO_REQUEST_CODE=2;
    final int AUDIO_REQUEST_CODE=3;
    final int PICTURE_REQUEST_CODE=4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_ejercicio);
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
                    Uri pictureUri= Uri.fromFile(file);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT,pictureUri);
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
                tratarFichero(data);
                break;
            case VIDEO_REQUEST_CODE:
            case AUDIO_REQUEST_CODE:
            case PICTURE_REQUEST_CODE:
                Toast.makeText(this,"Foto sacada",Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void tratarFichero(Intent data){
        Uri uri=data.getData();
        Toast.makeText(this,uri.toString(),Toast.LENGTH_SHORT).show();
        Cursor cursor = null;
        String nombre=null;
        String size=null;
        try{
            cursor = this.getContentResolver().query(uri,null,null,null,null);
            if (cursor != null && cursor.moveToFirst()) {
                nombre=cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                int sizeIndex=cursor.getColumnIndex(OpenableColumns.SIZE);
                if(!cursor.isNull(sizeIndex)){
                    size=cursor.getString(sizeIndex);
                }
                else{
                    size="Unknown";
                }
                Toast.makeText(this,nombre, Toast.LENGTH_SHORT).show();
                Toast.makeText(this,size+" Bytes", Toast.LENGTH_SHORT).show();
            }
        }finally {
            cursor.close();
        }
    }
}
