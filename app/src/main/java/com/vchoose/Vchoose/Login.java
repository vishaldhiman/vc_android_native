package com.vchoose.Vchoose;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.vchoose.Vchoose.R;
import com.vchoose.Vchoose.util.VcJsonReader;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class Login extends ActionBarActivity {

    EditText email;
    EditText password;
    String email_text;
    String password_text;

    String auth_token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void getLogin(View view) {
        email = (EditText)findViewById(R.id.email);
        password = (EditText)findViewById(R.id.password);
        email_text = email.getText().toString();
        password_text = password.getText().toString();
        //Log.v("email", email_text);
        //Log.v("password",password_text);
        new Thread(new Runnable() {
            public void run() {
                post(email_text,password_text);
            }
        }).start();


    }

    public void post(String email, String password) {
        VcJsonReader jParser = new VcJsonReader();
        String response = jParser.login(email,password);
        JSONTokener tokener = new JSONTokener(response);
        try {
            JSONObject responseObject = (JSONObject) tokener.nextValue();
            auth_token = responseObject.getString("auth_token");
            Log.v("Login success", auth_token);
        } catch(JSONException e) {}
    }
}
