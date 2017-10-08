package x23.instxag23ram;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

public class X23JSONParser {

    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";

    // constructor
    public X23JSONParser() {

    }

//	public JSONObject getJSONFromUrlByPost(String url,
//			List<NameValuePair> nameValuePairs) {
//
//		// Making HTTP request
//		try {
//			// defaultHttpClient
//			DefaultHttpClient httpClient = new DefaultHttpClient();
//			HttpPost httpPost = new HttpPost(url);
//			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
//			HttpResponse httpResponse = httpClient.execute(httpPost);
//			HttpEntity httpEntity = httpResponse.getEntity();
//			is = httpEntity.getContent();
//
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		} catch (ClientProtocolException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//		try {
//			BufferedReader reader = new BufferedReader(new InputStreamReader(
//					is, "iso-8859-1"), 8);
//			StringBuilder sb = new StringBuilder();
//			String line = null;
//			while ((line = reader.readLine()) != null) {
//				sb.append(line + "\n");
//			}
//			is.close();
//			json = sb.toString();
//		} catch (Exception e) {
//			Log.e("Buffer Error", "Error converting result " + e.toString());
//		}
//
//		// try parse the string to a JSON object
//		try {
//			jObj = new JSONObject(json);
//		} catch (JSONException e) {
//			Log.e("JSON Parser", "Error parsing data " + e.toString());
//		}
//
//		// return JSON String
//		return jObj;
//
//	}

    public JSONObject getJSONFromUrlByGet(String url) {

        // Making HTTP request
        try {
            // defaultHttpClient
//			DefaultHttpClient httpClient = new DefaultHttpClient();
//			HttpGet httpGet = new HttpGet(url);
//
//			HttpResponse httpResponse = httpClient.execute(httpGet);
//			HttpEntity httpEntity = httpResponse.getEntity();

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
}
