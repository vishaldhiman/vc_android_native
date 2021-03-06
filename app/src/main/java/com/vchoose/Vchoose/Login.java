package com.vchoose.Vchoose;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.gc.materialdesign.views.ButtonRectangle;
import com.vchoose.Vchoose.util.InternalSorage;
import com.vchoose.Vchoose.util.User;
import com.vchoose.Vchoose.util.VcJsonReader;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.InputStream;

public class Login extends ActionBarActivity {

    EditText email;
    EditText password;
    String email_text;
    String password_text;
    boolean loginResult;

    LoginButton fbLoginButton;
    CallbackManager callbackManager;
    AccessTokenTracker accessTokenTracker;

    ButtonRectangle loginButton;
    ButtonRectangle registerButton;

    final String TAG = "XiaoGuoTest_";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AccessToken accessToken = AccessToken.getCurrentAccessToken();

        SharedPreferences sp1=this.getSharedPreferences("Login", 0);
        String login_status;
        String facebookLogin = "false";
        boolean loggedin = false;
        if(sp1 != null) {
            login_status = sp1.getString("login_status", null);
            facebookLogin = sp1.getString("facebookLogin", null);
            if(login_status != null){
                if(login_status.equals("true")){
                    loggedin = true;
                }
            }
        }
        if(sp1==null|| !loggedin) {
            //initialize facebook and content

            setContentView(R.layout.activity_login);
            //initialize statues
            loginResult = false;

            // enter the email hint(from last time of login)
            email = (EditText)findViewById(R.id.email);
            if(User.getUser_name()!=null)
                email.setText(User.getUser_name());
            password = (EditText)findViewById(R.id.password);
            email.setText("a@a.com");
            password.setText("abcd1234");

            //login dialog
            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.dialog_login);
            dialog.setTitle("Login Success");
            final ImageView imageViewPhoto = (ImageView) dialog.findViewById(R.id.user_photo);
            final TextView textViewWelcome = (TextView) dialog.findViewById(R.id.user_welcome);

            //facebook login
            fbLoginButton = (LoginButton) this.findViewById(R.id.facebook_login);
            fbLoginButton.setReadPermissions("user_friends");

            accessTokenTracker = new AccessTokenTracker() {
                @Override
                protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken newAccessToken) {
                    if (newAccessToken == null) {
                        Log.v(TAG + "TokenC", "Log off");
                        User.login_status = false;
                        User.setUser_name(null);
                        User.setUser_photo(null);
                        Intent resultIntent = new Intent();
                        setResult(Activity.RESULT_CANCELED, resultIntent);
                        finish();
                    }
                }
            };
            accessTokenTracker.startTracking();

            callbackManager = CallbackManager.Factory.create();
            fbLoginButton.registerCallback(callbackManager,
                    new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult loginResult) {
                            // App code
                            AccessToken a = loginResult.getAccessToken();
                            Profile p = Profile.getCurrentProfile();
                            User.login_status = true;
                            User.setFacebookLogin(true);
                            User.setAuth_token(a.getToken());
                            User.setUser_name(p.getName());

                            Log.v(TAG + "token", a.getToken());
                            Log.v(TAG + "name", p.getName());
                            Log.v(TAG + "", p.getProfilePictureUri(50, 50).toString());
                            try {
                                //image downloaded and stored
                                new DownloadImageTask(imageViewPhoto).execute(p.getProfilePictureUri(400, 400).toString()).get();
                                //name stored
                                User.setUser_name(p.getName());
                                textViewWelcome.setText("Welcome " + p.getName());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            SharedPreferences sp=getSharedPreferences("Login", 0);
                            SharedPreferences.Editor Ed=sp.edit();
                            Ed.putString("login_status", "true");
                            Ed.putString("User_name", p.getName());
                            Ed.putString("facebookLogin", "true");
                            Ed.putString("Auth_token", a.getToken());
                            Ed.putString("Photo_Dir", InternalSorage.save(User.getUser_photo(), Login.this));
                            Ed.commit();

                            //show the dialog and automatically quit
                            new CountDownTimer(2000, 400) {
                                @Override
                                public void onTick(long millisUntilFinished) {
                                    // TODO Auto-generated method stub
                                    dialog.show();
                                }

                                @Override
                                public void onFinish() {
                                    // TODO Auto-generated method stub
                                    dialog.dismiss();

                                    Intent resultIntent = new Intent();
                                    setResult(Activity.RESULT_OK, resultIntent);
                                    finish();
                                }
                            }.start();

                        }

                        @Override
                        public void onCancel() {
                            // App code

                        }

                        @Override
                        public void onError(FacebookException exception) {
                            // App code
                            Toast toast = Toast.makeText(getApplicationContext(), "Log in failed", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    });

            //vchoose login
            loginButton = (ButtonRectangle) findViewById(R.id.loginButton);
            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getLogin(v);
                }
            });

            //register
            registerButton = (ButtonRectangle) findViewById(R.id.register);
            registerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Login.this, Register.class);
                    startActivity(intent);
                }
            });
        } else { //for logging out
            setContentView(R.layout.activity_login_account);
            TextView hello = (TextView)findViewById(R.id.hello_text);
            Button logoutButton = (Button)findViewById(R.id.logout);
            fbLoginButton = (LoginButton) this.findViewById(R.id.facebook_login);
            hello.setText(User.getUser_name());

            if( facebookLogin.equals("false") ) {//normal logout
                //delete the facebook login button
                ViewGroup layout = (ViewGroup) fbLoginButton.getParent();
                if(null!=layout)
                    layout.removeView(fbLoginButton);

                logoutButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        User.setUser_photo(null);
                        User.setAuth_token(null);
                        User.login_status = false;

                        SharedPreferences sp=getSharedPreferences("Login", 0);
                        SharedPreferences.Editor Ed=sp.edit();
                        Ed.putString("login_status", "false");
                        Ed.putString("facebookLogin", "false");
                        Ed.putString("Auth_token", "null");
                        Ed.putString("Photo_Dir", "null");
                        Ed.putString("User_name", " ");
                        Ed.commit();

                        Intent resultIntent = new Intent();
                        setResult(Activity.RESULT_CANCELED, resultIntent);
                        finish();
                    }
                });
            } else {//facebook logout
                //delete the normal login button
                ViewGroup layout = (ViewGroup) logoutButton.getParent();
                if(null!=layout)
                    layout.removeView(logoutButton);

                accessTokenTracker = new AccessTokenTracker() {
                    @Override
                    protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken newAccessToken) {
                        if (newAccessToken == null) {
                            Log.v(TAG + "TokenC", "Log off");
                            User.login_status = false;
                            User.setUser_name(null);
                            User.setUser_photo(null);
                            User.setAuth_token(null);

                            SharedPreferences sp=getSharedPreferences("Login", 0);
                            SharedPreferences.Editor Ed=sp.edit();
                            Ed.putString("login_status", "false");
                            Ed.putString("facebookLogin", "true");
                            Ed.putString("Auth_token", "null");
                            Ed.putString("Photo_Dir", "null");
                            Ed.putString("User_name", " ");
                            Ed.commit();

                            Intent resultIntent = new Intent();
                            setResult(Activity.RESULT_CANCELED, resultIntent);
                            finish();
                        }
                    }
                };
                accessTokenTracker.startTracking();
            }
        }
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

            SharedPreferences sp=getSharedPreferences("Login", 0);
            SharedPreferences.Editor Ed=sp.edit();
            Ed.putString("login_status", "true");
            Ed.putString("User_name", email_text);
            Ed.putString("facebookLogin", "false");
            Ed.putString("Auth_token", User.getAuth_token());
            Ed.putString("Photo_Dir", InternalSorage.save(BitmapFactory.decodeResource(getApplication().getResources(),R.drawable.com_facebook_profile_picture_blank_square), Login.this));
            Ed.commit();

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
                User.setUser_photo(BitmapFactory.decodeResource(getApplication().getResources(),R.drawable.blank_user));
                String auth_token = responseObject.getString("auth_token");
                User.setAuth_token(auth_token);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(accessTokenTracker!=null)
            accessTokenTracker.stopTracking();
    }

    class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            /*
            Log.v(TAG + "ImageWidth", String.valueOf(mIcon11.getWidth()));
            Log.v(TAG + "ImageHeight", String.valueOf(mIcon11.getHeight()));
            */
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
            User.setUser_photo(result);
            InternalSorage.save(result, Login.this);
            //store the user photo
        }
    }
}
