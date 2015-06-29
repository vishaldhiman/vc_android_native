package com.vchoose.Vchoose;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.vchoose.Vchoose.R;
import com.vchoose.Vchoose.util.VcJsonReader;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static android.content.pm.PackageManager.NameNotFoundException;

public class Login extends ActionBarActivity {

    EditText email;
    EditText password;
    String email_text;
    String password_text;

    String auth_token;

    LoginButton loginButton;
    CallbackManager callbackManager;

    final String TAG = "XiaoGuoTest_";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());


        setContentView(R.layout.activity_login);


        loginButton = (LoginButton) this.findViewById(R.id.facebook_login);
        loginButton.setReadPermissions("user_friends");

        AccessToken a = AccessToken.getCurrentAccessToken();
        if(a == null){
            Log.v(TAG+"myToken", "null");
        } else {
            Log.v(TAG+"myToken", a.toString());
        }

        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        AccessToken a = loginResult.getAccessToken();
                        Profile p = Profile.getCurrentProfile();
                        Log.v(TAG + "token", a.getUserId().toString());
                        Log.v(TAG + "name", p.getName().toString());
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });
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
        Thread t = new Thread(new Runnable() {
            public void run() {
                post(email_text,password_text);
            }
        });
        t.start();

        try {
            t.join();
        } catch (InterruptedException e){}
        Intent resultIntent = new Intent();
        resultIntent.putExtra("AuthenticationToken", auth_token);
        setResult(Activity.RESULT_OK, resultIntent);

        Toast toast = Toast.makeText(getApplicationContext(), "Log in success", Toast.LENGTH_SHORT);
        toast.show();
        finish();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
