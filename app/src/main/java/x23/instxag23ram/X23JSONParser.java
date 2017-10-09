package x23.instxag23ram;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class X23JSONParser {
    // constructor
    public X23JSONParser() {

    }

    public JSONObject getJSONFromUrlByGet(String url) {
        InputStream is = null;
        JSONObject jObj = null;
        String json = "";
        // Making HTTP request
        try {
            URL urlObj = new URL(url);
            //https://api.instagram.com/v1/users/5811825526/media/recent/?access_token=5811825526.19ac6ed.4e8b5a395d5a4697b40fab9fde99a856
            HttpURLConnection urlConnection = (HttpURLConnection) urlObj.openConnection();
            try {
                is = new BufferedInputStream(urlConnection.getInputStream());

                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();
                json = sb.toString();
            } finally {
                urlConnection.disconnect();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }

        // try parse the string to a JSON object
        try {
            jObj = new JSONObject(json);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }
        // return JSON String
        return jObj;

    }

    public String getStringFromUrlByPost(String url, String postData, String requestMode) {
        URL myUrl = null;
        HttpURLConnection conn = null;
        String response = "";

        try {
            myUrl = new URL(url);
            conn = (HttpURLConnection) myUrl.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod(requestMode);
            conn.setDoInput(true);
            conn.setDoOutput(true);

            if (postData != null) {
                OutputStream os = conn.getOutputStream();

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                bufferedWriter.write(postData);
                bufferedWriter.flush();
                bufferedWriter.close();
                os.close();
            }
            InputStream inputStream = conn.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                response += line;
            }
            bufferedReader.close();
            inputStream.close();
            conn.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }
}
