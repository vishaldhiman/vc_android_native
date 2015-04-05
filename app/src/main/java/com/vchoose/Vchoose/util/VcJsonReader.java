package com.vchoose.Vchoose.util;

import android.net.Uri;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vishaldhiman on 2/20/15.
 */
public class VcJsonReader {
    static InputStream is = null;
    static JSONArray jarray = null;

    static String json = "";
    private String url_prefix = "http://vchoose.us/api/v1/public/search.json?";
    private String loc = "search_locations=";
    private String keyword = "search_tags=";
    private String radius = "search_radius=";
    private static final String ALLOWED_URI_CHARS = "@#&=*+-_.,:!?()/~'%";

    private String url_login = "http://vchoose.us/users/sign_in.json?";
    private String email = "email=";
    private String password = "password=";

    private String url_autocomplete = "http://vchoose.us/api/v1/public/autocomplete?";
    private String term = "term=";
    private String count = "count=";

    public String buildUrl(String location, String search_keyword, String rad) throws URISyntaxException {
        //String encoded_url = URLEncoder.encode(loc+location+"&"+keyword+search_keyword+"&"+radius+rad,"UTF-8");
        //String encoded_url = URIUtils.
        //return url_prefix+encoded_url;
        //URI uri = new URI("http",url_prefix,loc+location+"&"+keyword+search_keyword+"&"+radius+rad,null);

        return Uri.encode(url_prefix+loc+location+"&"+keyword+search_keyword+"&"+radius+rad,ALLOWED_URI_CHARS);

        //return uri.toASCIIString();
    }

    public String login(String email_text, String password_text){
        StringBuilder builder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        try{
            HttpPost httpPost = new HttpPost(url_login);

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                /*hard coded for testing*/
                nameValuePairs.add(new BasicNameValuePair("email", "867136922@qq.com"));
                nameValuePairs.add(new BasicNameValuePair("password", "ty113113"));
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse response = client.execute(httpPost);


            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
            } else {
                Log.e("Error....", "Failed to download file");
            }
        }catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String resp = builder.toString();
        return resp;
    }

    public String[] getAutoComplete(String hint){
        StringBuilder builder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();

        try{
            String url = Uri.encode(url_autocomplete+term+"bu"+"&"+count+"5",ALLOWED_URI_CHARS);
            HttpGet httpGet = new HttpGet(url);
            Log.v("URL",url);

            HttpResponse response = client.execute(httpGet);

            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            Log.v("statusCode",String.valueOf(statusCode));
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
            } else {
                Log.e("Error....", "Failed to download file");
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String resp = builder.toString();
        Log.v("Result", resp);
/*
        try {
            JSONTokener tokener = new JSONTokener(resp);
            Log.v("JSONObject",tokener.toString());
            JSONObject responseObject = (JSONObject) tokener.nextValue();
            Log.v("JSONObject",responseObject.toString());
        } catch (JSONException e) {}
        */
        String[] yep = {"yes"}; //this api is not working.
        return yep;
    }

    public String getJSONFromUrl(String location, String search_keyword, String rad) {
        StringBuilder builder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        try {

            String url = buildUrl(location,search_keyword,rad);

            Log.v("VcJsonReader","JSON Encoded URL:\n"+url);

            HttpGet httpGet = new HttpGet(url);

            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
            } else {
                Log.e("Error....", "Failed to download file");
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        JSONObject response = null;
        String resp = null;
        try {

            resp = builder.toString();
            Log.v("VcJsonReader","JSON Response:\n"+resp);

            //response = (JSONObject) new JSONTokener(resp).nextValue();
            //jarray = new JSONArray( builder.toString());
        } catch (Exception e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }

        return resp;

    }
}
