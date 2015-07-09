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
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
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

    private static final String TAG = "SamTest_";

    private String URL_HOST = "http://vchoose.us/";
    private String URL_HOSTS = "https://vchoose.us/";
    private String API_VER = "api/v1/public/";

    static String json = "";
    private String url_prefix = URL_HOST+API_VER+"search.json?";
    //private String url_prefix = "http://127.0.0.1:3002/api/v1/public/search.json?";
    private String loc = "search_locations=";
    private String keyword = "search_tags=";
    private String radius = "search_radius=";
    private static final String ALLOWED_URI_CHARS = "@#&=*+-_.,:!?()/~'%";

    private String url_login = URL_HOST+"users/sign_in.json?";
    private String email = "email=";
    private String password = "password=";

    private String url_autocomplete = URL_HOST+API_VER+"autocomplete?";
    private String term = "term=";
    private String count = "count=";

    //private String url_rating = URL_HOST+API_VER+"menu_items/2972/rate/5?format=json";
    private String url_rating = URL_HOSTS+API_VER+"menu_items/";
    private String url_rating_rate = "/rate/";
    private String url_rating_format = "?format=json";

    private String url_restaurant_info = "restaurants/";
    private String url_details = "/details";

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
            Log.v(TAG + "url", "https://vchoose.us/users/sign_in.json?");
            HttpPost httpPost = new HttpPost("https://vchoose.us/users/sign_in.json?");

                List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("email", email_text));    //hard coded for testing
                nameValuePairs.add(new BasicNameValuePair("password", password_text));

                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf-8"));

            Log.v(TAG + "getRequestLine", httpPost.getRequestLine().toString());

            //Log.v(TAG + "getRequestLine", );
//                HttpEntity httpEntity = httpPost.getEntity();
//                InputStream httpcontent = httpEntity.getContent();
//                BufferedReader httpreader = new BufferedReader(new InputStreamReader(httpcontent));
//                String httpline;
//                while ((httpline = httpreader.readLine()) != null) {
//                    builder.append(httpline);
//                }
//                Log.v(TAG + "httpEntity",builder.toString());

             /*
            JSONObject obj = new JSONObject();
            obj.put("email", "867136922@qq.com");
            obj.put("password", "ty113113");
            httpPost.setEntity(new StringEntity(obj.toString(), "UTF-8"));
            */
            //httpPost = new HttpPost("http://vchoose.us/users/sign_in.json?email=867136922@qq.com&password=ty113113");

            HttpResponse response = client.execute(httpPost);

            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            //Log.v(TAG + "httpPost",httpPost.getEntity().toString());
            Log.v(TAG + "statusCode",String.valueOf(statusCode));
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
        } /*catch (JSONException e) {
            e.printStackTrace();
        }*/
        String resp = builder.toString();
        Log.v(TAG + "resp", resp);
        return resp;
    }

    public boolean submitRatingForDish(int menu_item_id, int rating, String authentication_token) {
        StringBuilder builder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        boolean result = false;

        try{
            String url = Uri.encode(url_rating+menu_item_id+url_rating_rate+rating+url_rating_format,ALLOWED_URI_CHARS);
            String url2 = Uri.encode(url_rating+menu_item_id+url_rating_rate+rating+ "?" ,ALLOWED_URI_CHARS);

            HttpPost httpPost = new HttpPost(url2);
            Log.v(TAG + "URL",url2);
            Log.v(TAG + "Auth",authentication_token);
            //httpPost.setHeader("authentication_token", authentication_token);
            httpPost.addHeader("Cookie","remember_user_token="+"BAhbB1sGaRtJIiIkMmEkMTAkSkQ4M0N0U0ZqZzMvQjlNS1RJamQ3dQY6BkVU--cfa85f5c07b8345e5d0573c3abe1aeee06684b0b");
            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(3);
            nameValuePair.add(new BasicNameValuePair("format", "json"));
            nameValuePair.add(new BasicNameValuePair("rating[rateable_type]", "MenuItem"));
            nameValuePair.add(new BasicNameValuePair("rating[review]", "adafdsfd"));
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair, "utf-8"));

            //HttpResponse response = client.execute(httpGet);
            HttpResponse response = client.execute(httpPost);
            //Log.v("the rating request",httpPost.toString());

            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            Log.v("statusCode",String.valueOf(statusCode));
            if ((statusCode == 200)|| (statusCode == 201)) {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                Log.v(TAG + "dishRating", builder.toString());
                result = true;
            } else {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                Log.v(TAG + "dishRating", builder.toString());
                result = true;
                Log.e("Error....", "Failed to download file");
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*
        JSONArray jsonArray = new JSONArray();

        String resp = builder.toString();
        try {
            JSONTokener tokener;
            tokener = new JSONTokener(resp);

            JSONObject jsonObj;
            jsonObj = new JSONObject(tokener);

            //jsonArray = new JSONArray(tokener);

            int rateable_id = jsonObj.getInt("rateable_id");
            result = true;
            //JSONObject responseObject = (JSONObject) tokener.nextValue();
        } catch (JSONException e) {}

        */
        //this api is not working.
        return result;
    }

    public ArrayList<String> getAutoComplete(String hint){
        ArrayList<String> result = new ArrayList<String>();
        StringBuilder builder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();

        try{
            String url = Uri.encode(url_autocomplete+term+hint+"&"+count+"7"+"&format=json",ALLOWED_URI_CHARS);
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
        JSONArray jsonArray = new JSONArray();

        String resp = builder.toString();
        try {
            JSONTokener tokener;
            tokener = new JSONTokener(resp);
            jsonArray = new JSONArray(tokener);

            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = (JSONObject)jsonArray.get(i);
                String object = jsonObject.getString("value");
                Log.v("String"+i,object);
                result.add(object);
            }
            //JSONObject responseObject = (JSONObject) tokener.nextValue();
        } catch (JSONException e) {}


         //this api is not working.
        return result;
    }

    public String getRestaurantInfo(String ID) {
        StringBuilder builder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        try {

            String url = Uri.encode(URL_HOST + API_VER + url_restaurant_info + ID + url_details, ALLOWED_URI_CHARS);

            Log.v(TAG + "VcJsonReader","JSON Encoded URL:\n"+url);

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
        }

        String resp = null;
        try {
            resp = builder.toString();
            //Log.v("VcJsonReader","JSON Response:\n"+resp);
            //response = (JSONObject) new JSONTokener(resp).nextValue();
            //jarray = new JSONArray( builder.toString());
        } catch (Exception e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }

        return resp;

    }

    public String getJSONFromUrl(String location, String search_keyword, String rad) {
        StringBuilder builder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        try {

            String url = buildUrl(location,search_keyword,rad);

            Log.v(TAG+"VcJsonReader","JSON Encoded URL:\n"+url);

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
