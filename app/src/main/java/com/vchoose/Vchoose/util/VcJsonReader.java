package com.vchoose.Vchoose.util;

import android.net.Uri;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;

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

    public String buildUrl(String location, String search_keyword, String rad) throws URISyntaxException {
        //String encoded_url = URLEncoder.encode(loc+location+"&"+keyword+search_keyword+"&"+radius+rad,"UTF-8");
        //String encoded_url = URIUtils.
        //return url_prefix+encoded_url;
        //URI uri = new URI("http",url_prefix,loc+location+"&"+keyword+search_keyword+"&"+radius+rad,null);

        return Uri.encode(url_prefix+loc+location+"&"+keyword+search_keyword+"&"+radius+rad,ALLOWED_URI_CHARS);

        //return uri.toASCIIString();
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
