package x23.instxag23ram;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * Created by JITENDRA KUMAR on 10/7/17.
 */

public class X23InstagramSession {

    private static final String X23_SHARED = "Instagram_Preferences";
    private static final String X23_API_USERNAME = "username";
    private static final String X23_API_ID = "id";
    private static final String X23_API_NAME = "name";
    private static final String X23_API_ACCESS_TOKEN = "access_token";
    private SharedPreferences sharedPref;
    private Editor editor;

    public X23InstagramSession(Context context) {
        sharedPref = context.getSharedPreferences(X23_SHARED, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
    }

    public void storeAccessToken(String accessToken, String id, String username, String name) {
        editor.putString(X23_API_ID, id);
        editor.putString(X23_API_NAME, name);
        editor.putString(X23_API_ACCESS_TOKEN, accessToken);
        editor.putString(X23_API_USERNAME, username);
        editor.commit();
    }

    public void storeAccessToken(String accessToken) {
        editor.putString(X23_API_ACCESS_TOKEN, accessToken);
        editor.commit();
    }

    /**
     * Reset access token and user name
     */
    public void resetAccessToken() {
        editor.putString(X23_API_ID, null);
        editor.putString(X23_API_NAME, null);
        editor.putString(X23_API_ACCESS_TOKEN, null);
        editor.putString(X23_API_USERNAME, null);
        editor.commit();
    }

    /**
     * Get user name
     *
     * @return User name
     */
    public String getUsername() {
        return sharedPref.getString(X23_API_USERNAME, null);
    }

    /**
     * @return
     */
    public String getId() {
        return sharedPref.getString(X23_API_ID, null);
    }

    /**
     * @return
     */
    public String getName() {
        return sharedPref.getString(X23_API_NAME, null);
    }

    /**
     * Get access token
     *
     * @return Access token
     */
    public String getAccessToken() {
        return sharedPref.getString(X23_API_ACCESS_TOKEN, null);
    }

}