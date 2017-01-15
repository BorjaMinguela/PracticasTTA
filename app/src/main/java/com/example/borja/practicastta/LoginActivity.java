package com.example.borja.practicastta;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.borja.practicastta.model.ProgressTask;
import com.example.borja.practicastta.model.RestClient;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;


public class LoginActivity extends AppCompatActivity {
    private RestClient rest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    /**
     * Loguearse e ir a la sigueinte pantalla
     * @param view
     */
    public void login(View view){
        if(RestClient.getConnectivity(this)) {
            final Intent intent = new Intent(this, MainActivity.class);
            final String login = ((EditText) findViewById(R.id.login)).getText().toString();
            final String passwd = ((EditText) findViewById(R.id.pass)).getText().toString();
            try {
                new ProgressTask<Boolean>(this) {
                    @Override
                    protected Boolean work() throws Exception {
                        boolean response=log2Server(login,passwd);
                        return response;
                    }

                    @Override
                    protected void onFinish(Boolean result) {
                        if(result)
                            startActivity(intent);
                        else{
                            ((EditText) findViewById(R.id.login)).setTextColor(Color.RED);
                            ((EditText) findViewById(R.id.pass)).setTextColor(Color.RED);
                        }
                    }
                }.execute();
            } catch (Exception e) {
                Toast.makeText(this, R.string.error_ejercicio, Toast.LENGTH_SHORT).show();
                Toast.makeText(this, e.toString(),Toast.LENGTH_LONG).show();
            }
        }
        else{
            Toast.makeText(this, R.string.no_internet,Toast.LENGTH_SHORT).show();
        }

    }

    public boolean log2Server(String dni,String passwd){
        rest= new RestClient(getString(R.string.server_url));
        rest.setHttpBasicAuth(dni,passwd);
        try {
            JSONObject json = rest.getJson(String.format("getStatus?dni=%s", dni));
            int id=json.getInt("id");
            if(1==id)
                return true;
        }
        catch (Exception e){
            String error=e.toString();
            Log.e("Error JSON",error);
            return false;
        }
        return false;
    }
}
