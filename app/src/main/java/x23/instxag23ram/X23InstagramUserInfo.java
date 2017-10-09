package x23.instxag23ram;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashMap;

import static x23.instxag23ram.X23Credentials.TAG_BIO;
import static x23.instxag23ram.X23Credentials.TAG_CODE;
import static x23.instxag23ram.X23Credentials.TAG_COUNTS;
import static x23.instxag23ram.X23Credentials.TAG_FOLLOWED_BY;
import static x23.instxag23ram.X23Credentials.TAG_FOLLOWS;
import static x23.instxag23ram.X23Credentials.TAG_FULL_NAME;
import static x23.instxag23ram.X23Credentials.TAG_ID;
import static x23.instxag23ram.X23Credentials.TAG_MEDIA;
import static x23.instxag23ram.X23Credentials.TAG_META;
import static x23.instxag23ram.X23Credentials.TAG_PROFILE_PICTURE;
import static x23.instxag23ram.X23Credentials.TAG_USERNAME;
import static x23.instxag23ram.X23Credentials.TAG_WEBSITE;

/**
 * Created by JITENDRA KUMARS on 10/8/17.
 */


public class X23InstagramUserInfo {

    private static final String X23_SHARED = "Instagram_Preferences";
    private SharedPreferences sharedPref;
    private Editor editor;

    public X23InstagramUserInfo(Context context) {
        sharedPref = context.getSharedPreferences(X23_SHARED, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
    }

    public void initUserInfo(HashMap<String, String> mUserInfo) {
        mUserInfo.put(TAG_ID, sharedPref.getString(TAG_ID, null));
        mUserInfo.put(TAG_PROFILE_PICTURE, sharedPref.getString(TAG_PROFILE_PICTURE, null));
        mUserInfo.put(TAG_USERNAME, sharedPref.getString(TAG_USERNAME, null));
        mUserInfo.put(TAG_BIO, sharedPref.getString(TAG_BIO, null));

        mUserInfo.put(TAG_WEBSITE, sharedPref.getString(TAG_WEBSITE, null));
        mUserInfo.put(TAG_COUNTS, sharedPref.getString(TAG_COUNTS, null));
        mUserInfo.put(TAG_FOLLOWS, sharedPref.getString(TAG_FOLLOWS, null));
        mUserInfo.put(TAG_FOLLOWED_BY, sharedPref.getString(TAG_FOLLOWED_BY, null));

        mUserInfo.put(TAG_MEDIA, sharedPref.getString(TAG_MEDIA, null));
        mUserInfo.put(TAG_FULL_NAME, sharedPref.getString(TAG_FULL_NAME, null));
        mUserInfo.put(TAG_META, sharedPref.getString(TAG_META, null));
        mUserInfo.put(TAG_CODE, sharedPref.getString(TAG_CODE, null));
    }

    public void initByUserInfo(HashMap<String, String> mUserInfo) {
        editor.putString(TAG_ID, mUserInfo.get(TAG_ID));
        editor.putString(TAG_PROFILE_PICTURE, mUserInfo.get(TAG_PROFILE_PICTURE));
        editor.putString(TAG_USERNAME, mUserInfo.get(TAG_USERNAME));
        editor.putString(TAG_BIO, mUserInfo.get(TAG_BIO));

        editor.putString(TAG_WEBSITE, mUserInfo.get(TAG_WEBSITE));
        editor.putString(TAG_COUNTS, mUserInfo.get(TAG_COUNTS));
        editor.putString(TAG_FOLLOWS, mUserInfo.get(TAG_FOLLOWS));
        editor.putString(TAG_FOLLOWED_BY, mUserInfo.get(TAG_FOLLOWED_BY));

        editor.putString(TAG_MEDIA, mUserInfo.get(TAG_MEDIA));
        editor.putString(TAG_FULL_NAME, mUserInfo.get(TAG_FULL_NAME));
        editor.putString(TAG_META, mUserInfo.get(TAG_META));
        editor.putString(TAG_CODE, mUserInfo.get(TAG_CODE));

        editor.commit();
    }

    public void storeAccessToken(String id, String profilePic, String username, String bio,
                                 String website, String count, String follows, String followedBy,
                                 String media, String fName, String meta, String code) {
        editor.putString(TAG_ID, id);
        editor.putString(TAG_PROFILE_PICTURE, profilePic);
        editor.putString(TAG_USERNAME, username);
        editor.putString(TAG_BIO, bio);

        editor.putString(TAG_WEBSITE, website);
        editor.putString(TAG_COUNTS, count);
        editor.putString(TAG_FOLLOWS, follows);
        editor.putString(TAG_FOLLOWED_BY, followedBy);

        editor.putString(TAG_MEDIA, media);
        editor.putString(TAG_FULL_NAME, fName);
        editor.putString(TAG_META, meta);
        editor.putString(TAG_CODE, code);

        editor.commit();
    }

    /**
     * Reset access token and user name
     */
    public void resetUserInfo() {
        editor.putString(TAG_ID, null);
        editor.putString(TAG_PROFILE_PICTURE, null);
        editor.putString(TAG_USERNAME, null);
        editor.putString(TAG_BIO, null);

        editor.putString(TAG_WEBSITE, null);
        editor.putString(TAG_COUNTS, null);
        editor.putString(TAG_FOLLOWS, null);
        editor.putString(TAG_FOLLOWED_BY, null);

        editor.putString(TAG_MEDIA, null);
        editor.putString(TAG_FULL_NAME, null);
        editor.putString(TAG_META, null);
        editor.putString(TAG_CODE, null);
        editor.commit();
    }

}
