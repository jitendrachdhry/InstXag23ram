package x23.instxag23ram;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import static x23.instxag23ram.X23Credentials.TAG_BIO;
import static x23.instxag23ram.X23Credentials.TAG_CODE;
import static x23.instxag23ram.X23Credentials.TAG_COUNTS;
import static x23.instxag23ram.X23Credentials.TAG_DATA;
import static x23.instxag23ram.X23Credentials.TAG_FOLLOWED_BY;
import static x23.instxag23ram.X23Credentials.TAG_FOLLOWS;
import static x23.instxag23ram.X23Credentials.TAG_FULL_NAME;
import static x23.instxag23ram.X23Credentials.TAG_ID;
import static x23.instxag23ram.X23Credentials.TAG_MEDIA;
import static x23.instxag23ram.X23Credentials.TAG_META;
import static x23.instxag23ram.X23Credentials.TAG_PROFILE_PICTURE;
import static x23.instxag23ram.X23Credentials.TAG_USERNAME;
import static x23.instxag23ram.X23Credentials.TAG_WEBSITE;
import static x23.instxag23ram.X23Credentials.X23_IG_API_URL;
import static x23.instxag23ram.X23Credentials.X23_IG_AUTH_URL;
import static x23.instxag23ram.X23Credentials.X23_IG_CALLBACK_URL;
import static x23.instxag23ram.X23Credentials.X23_IG_CLIENT_ID;
import static x23.instxag23ram.X23Credentials.X23_IG_CLIENT_SECRET;
import static x23.instxag23ram.X23Credentials.X23_IG_TOKEN_URL;
import static x23.instxag23ram.X23Credentials.mCallbackUrl;
/**
 * Created by JITENDRA KUMAR on 10/7/17.
 */

public class X23MainActivity extends AppCompatActivity implements View.OnClickListener {

    //Register the app by following below link.
    //http://instagram.com/developer/
    //After registration copy client_id, client_secret and callback_rul from instagram Manage Client page.

    public static final String TAG = "X23MainActivity";

    static int X23_FETCH_INFO_DONE = 0;
    static int X26_ERROR = 1;
    private static int X23_FETCH_INFO_REQUEST = 2;
    private final int STORAGE_PERMISSION_RC = 1001;
    private String mAuthUrl;
    private String mTokenUrl;
    private String mAccessToken;
    private String mMediaId;
    private boolean isWritePermission = false;
    //Create a dialog object, that will contain the WebView
    private Dialog webViewDialog;
    private ProgressDialog mDialogSpinner;
    private ProgressDialog mConnectSpinner;
    private X23InstagramSession mSession;
    private X23InstagramUserInfo mUserInfoSP;
    private HashMap<String, String> mUserInfo;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == X26_ERROR) {
                mConnectSpinner.dismiss();
                if (msg.arg1 == 1) {
                    Toast.makeText(getContext(), getString(R.string.error_user_info), Toast.LENGTH_SHORT).show();
                } else if (msg.arg1 == 2) {
                    Toast.makeText(getContext(), getString(R.string.error_access_token), Toast.LENGTH_SHORT).show();
                }
            } else if (msg.what == X23_FETCH_INFO_REQUEST) {
                fetchUserName();
            } else if (msg.what == X23_FETCH_INFO_DONE) {
                mConnectSpinner.dismiss();
                showIGFeatureUI();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_x23_main);
        mSession = new X23InstagramSession(this);
        mUserInfoSP = new X23InstagramUserInfo(getContext());
        mUserInfo = new HashMap<String, String>();

        mAuthUrl = X23_IG_AUTH_URL
                + "?client_id="
                + X23_IG_CLIENT_ID
                + "&redirect_uri="
                + X23_IG_CALLBACK_URL
                + "&response_type=code&display=touch&scope=likes+comments+relationships";

        mTokenUrl = X23_IG_TOKEN_URL + "?client_id=" + X23_IG_CLIENT_ID + "&client_secret="
                + X23_IG_CLIENT_SECRET + "&redirect_uri=" + mCallbackUrl
                + "&grant_type=authorization_code";

        Button buttonOne = (Button) findViewById(R.id.connectIG);
        buttonOne.setOnClickListener(this);

        buttonOne = (Button) findViewById(R.id.disconnectIG);
        buttonOne.setOnClickListener(this);

        buttonOne = (Button) findViewById(R.id.showImagesIG);
        buttonOne.setOnClickListener(this);

        //No need to login again if user already signed in.
        if (mSession.getAccessToken() != null) {
            showIGFeatureUI();
            mUserInfoSP.initUserInfo(mUserInfo);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void displayWebView() {
        //a WebView object to display a web page
        WebView webView;
        //The button to launch the WebView dialog
        Button btLaunchWVD;
        //The button that closes the dialog
        Button btClose;
        //Create a new dialog
        webViewDialog = new Dialog(this);

        mDialogSpinner = new ProgressDialog(webViewDialog.getContext());
        mDialogSpinner.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialogSpinner.setMessage("Loading...");

        //Remove the dialog's title
        webViewDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Inflate the contents of this dialog with the Views defined at 'webviewdialog.xml'
        webViewDialog.setContentView(R.layout.signin_x23_dialog);
        //With this line, the dialog can be dismissed by pressing the back key
        webViewDialog.setCancelable(true);

        //Initialize the Button object with the data from the 'webviewdialog.xml' file
        btClose = webViewDialog.findViewById(R.id.cancel);
        //Define what should happen when the close button is pressed.
        btClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Dismiss the dialog
                webViewDialog.dismiss();
            }
        });

        //Initialize the WebView object with data from the 'webviewdialog.xml' file
        webView = webViewDialog.findViewById(R.id.signInWebView);
        //Scroll bars should not be hidden
        webView.setScrollbarFadingEnabled(false);
        //Disable the horizontal scroll bar
        webView.setHorizontalScrollBarEnabled(false);
        //Enable JavaScript
        webView.getSettings().setJavaScriptEnabled(true);
        //Set the user agent
        webView.getSettings().setUserAgentString("AndroidWebView");
        //Clear the cache
        webView.clearCache(true);
        //Make the webview load the specified URL
        webView.loadUrl(mAuthUrl);

        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setWebViewClient(new OAuthWebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);

        //Display the WebView dialog
        webViewDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_x23_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_exit) {
            // Exit from X23MainActivity
            finish();
            return true;
        } else if (id == R.id.action_about) {
            Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.action_about_txt), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else if (id == R.id.action_help) {
            Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.action_help_txt), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
        return super.onOptionsItemSelected(item);
    }

    X23MainActivity getContext() {
        return this;
    }

    private void getAccessToken(final String code) {


        mConnectSpinner = new ProgressDialog(getContext());
        mConnectSpinner.setCancelable(false);
        mConnectSpinner.setMessage(getString(R.string.connecting));
        mConnectSpinner.show();

        new Thread() {
            @Override
            public void run() {
                Log.i(TAG, "Getting access token");
                int what = X23_FETCH_INFO_REQUEST;
                try {
                    URL url = new URL(X23_IG_TOKEN_URL);
                    // URL url = new URL(mTokenUrl + "&code=" + code);
                    Log.i(TAG, "Opening Token URL " + url.toString());
                    HttpURLConnection urlConnection = (HttpURLConnection) url
                            .openConnection();
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setDoInput(true);
                    urlConnection.setDoOutput(true);
                    // urlConnection.connect();
                    OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
                    writer.write("client_id=" + X23_IG_CLIENT_ID + "&client_secret="
                            + X23_IG_CLIENT_SECRET + "&grant_type=authorization_code"
                            + "&redirect_uri=" + X23_IG_CALLBACK_URL + "&code=" + code);
                    writer.flush();
                    String response = X23Utils.streamToString(urlConnection.getInputStream());
                    Log.i(TAG, "response from server:" + response);
                    JSONObject jsonObj = (JSONObject) new JSONTokener(response).nextValue();

                    mAccessToken = jsonObj.getString("access_token");
                    Log.i(TAG, "Got access token: " + mAccessToken);

                    String id = jsonObj.getJSONObject("user").getString("id");
                    String user = jsonObj.getJSONObject("user").getString("username");
                    String name = jsonObj.getJSONObject("user").getString("full_name");
                    mSession.storeAccessToken(mAccessToken, id, user, name);

                } catch (Exception ex) {
                    what = X26_ERROR;
                    ex.printStackTrace();
                }

                mHandler.sendMessage(mHandler.obtainMessage(what, 1, 0));
            }
        }.start();
    }

    public void fetchUserName() {

        new Thread() {
            @Override
            public void run() {
                Log.i(TAG, "Fetching user info");
                int what = X23_FETCH_INFO_DONE;
                try {
                    URL url = new URL(X23_IG_API_URL + "/users/" + mSession.getId()
                            + "/?access_token=" + mAccessToken);

                    Log.d(TAG, "Opening URL " + url.toString());
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setDoInput(true);
                    urlConnection.connect();
                    String response = X23Utils.streamToString(urlConnection.getInputStream());

                    JSONObject jsonObj = (JSONObject) new JSONTokener(response).nextValue();
                    JSONObject data_obj = jsonObj.getJSONObject(TAG_DATA);
                    mUserInfo.put(TAG_ID, data_obj.getString(TAG_ID));
                    mUserInfo.put(TAG_PROFILE_PICTURE, data_obj.getString(TAG_PROFILE_PICTURE));
                    mUserInfo.put(TAG_USERNAME, data_obj.getString(TAG_USERNAME));
                    mUserInfo.put(TAG_BIO, data_obj.getString(TAG_BIO));
                    mUserInfo.put(TAG_WEBSITE, data_obj.getString(TAG_WEBSITE));

                    JSONObject counts_obj = data_obj.getJSONObject(TAG_COUNTS);
                    mUserInfo.put(TAG_FOLLOWS, counts_obj.getString(TAG_FOLLOWS));
                    mUserInfo.put(TAG_FOLLOWED_BY, counts_obj.getString(TAG_FOLLOWED_BY));
                    mUserInfo.put(TAG_MEDIA, counts_obj.getString(TAG_MEDIA));
                    mUserInfo.put(TAG_FULL_NAME, data_obj.getString(TAG_FULL_NAME));

                    JSONObject meta_obj = jsonObj.getJSONObject(TAG_META);
                    mUserInfo.put(TAG_CODE, meta_obj.getString(TAG_CODE));

                    mUserInfoSP.initByUserInfo(mUserInfo);
                } catch (Exception ex) {
                    what = X26_ERROR;
                    ex.printStackTrace();
                }
                mHandler.sendMessage(mHandler.obtainMessage(what, 2, 0));
            }
        }.start();

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.connectIG) {

            if (Build.VERSION.SDK_INT >= 23) {
                if (ContextCompat.checkSelfPermission(X23MainActivity.this,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(X23MainActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(X23MainActivity.this,
                            new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    android.Manifest.permission.READ_EXTERNAL_STORAGE},
                            STORAGE_PERMISSION_RC);
                } else
                    displayWebView();
            } else {
                displayWebView();
            }
        } else if (view.getId() == R.id.disconnectIG) {
            if (mSession.getAccessToken() != null) {
                mSession.resetAccessToken();
                mUserInfoSP.resetUserInfo();
                removeCookies();
                hideIGFeatureUI();
            }
        } else if (view.getId() == R.id.showImagesIG) {
            Intent lInt = new Intent(X23MainActivity.this, X23ShowPhotosActivity.class);
            lInt.putExtra("userInfo", mUserInfo);
            lInt.putExtra("session", mSession.getAccessToken());
            startActivity(lInt);
        }
    }

    private void removeCookies() {
        // - Remove Cookie for fresh login.
        // - If we do not remove Cookie, disconnect user will not work.
        // - It always try to login again for last disconnected user.
        CookieSyncManager.createInstance(getContext());
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_RC) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //permission granted start signin
                displayWebView();
            } else {
                Toast.makeText(this, "No permission to read external storage. Please give permission to connect Instagram. Thanks", Toast.LENGTH_SHORT).show();
            }
        }
    }
    //
    private void showIGFeatureUI() {
        Button bt = (Button) findViewById(R.id.connectIG);
        bt.setVisibility(View.GONE);
        LinearLayout ll = (LinearLayout) findViewById(R.id.showFeatureListIG);
        ll.setVisibility(View.VISIBLE);
    }

    private void hideIGFeatureUI() {
        Button bt = (Button) findViewById(R.id.connectIG);
        bt.setVisibility(View.VISIBLE);
        LinearLayout ll = (LinearLayout) findViewById(R.id.showFeatureListIG);
        ll.setVisibility(View.GONE);
    }

    private class OAuthWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.d(TAG, "Redirecting URL " + url);

            if (url.startsWith(X23_IG_CALLBACK_URL)) {
                String urls[] = url.split("=");
                getAccessToken(urls[1]);
                webViewDialog.dismiss();
                return true;
            }
            return false;
        }

        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            Log.d(TAG, "Login Page error: " + description);
            super.onReceivedError(view, errorCode, description, failingUrl);
            webViewDialog.dismiss();
            Toast.makeText(getContext(), getString(R.string.error_page_loading), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            Log.d(TAG, "Loading URL: " + url);
            mDialogSpinner.show();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            //String title = mDialogWebView.getTitle();
            Log.d(TAG, "onPageFinished URL: " + url);
            mDialogSpinner.dismiss();
        }

    }
}
