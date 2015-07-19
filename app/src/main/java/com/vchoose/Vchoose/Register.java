package com.vchoose.Vchoose;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.vchoose.Vchoose.R;
import com.vchoose.Vchoose.util.User;
import com.vchoose.Vchoose.util.VcJsonReader;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class Register extends ActionBarActivity {
    EditText emailText;
    EditText passwordText;
    EditText repasswordText;
    Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        emailText = (EditText)findViewById(R.id.email);
        passwordText = (EditText)findViewById(R.id.password);
        repasswordText = (EditText)findViewById(R.id.repassword);
        registerButton = (Button)findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
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

    /*
    public void getRegister() {
        Thread t = new Thread(new Runnable() {
            public void run() {
                //post(email_text,password_text);
                post(email_text,password_text);
            }
        });
        t.start();

        try {
            t.join();
        } catch (InterruptedException e){}

        if(loginResult == true) {
            Toast toast = Toast.makeText(getApplicationContext(), "Log in success", Toast.LENGTH_SHORT);
            toast.show();

            Intent resultIntent = new Intent();
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), "Log in failed", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void post(String email, String password) {

        VcJsonReader jParser = new VcJsonReader();
        String response = jParser.login(email,password);
        JSONTokener tokener = new JSONTokener(response);
        Log.v(TAG + "Response", response);
        try {
            JSONObject responseObject = (JSONObject) tokener.nextValue();
            String result = responseObject.getString("success");
            if(result.equals("true")) {
                User.setAuth_token(responseObject.getString("auth_token"));
                Log.v(TAG + "Login Token", responseObject.getString("auth_token"));
                loginResult = true;
                User.login_status = true;
                User.setFacebookLogin(false);
                User.setUser_name(responseObject.getString("email"));
                User.setUser_photo(getResources().getDrawable(R.drawable.blank_user));
                String auth_token = responseObject.getString("auth_token");
                Log.v(TAG + "Login", auth_token);
                //Toast toast = Toast.makeText(getApplicationContext(), "Log in success", Toast.LENGTH_SHORT);
                //toast.show();
            } else {
                Log.v(TAG + "Login", "failed");
                loginResult = false;
            }

        } catch(JSONException e) {
            e.printStackTrace();
        }
    }
    */
}
