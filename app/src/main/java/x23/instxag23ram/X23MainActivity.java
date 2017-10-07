package x23.instxag23ram;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import static x23.instxag23ram.X23Credentials.X23_IG_AUTH_URL;
import static x23.instxag23ram.X23Credentials.X23_IG_CALLBACK_URL;
import static x23.instxag23ram.X23Credentials.X23_IG_CLIENT_ID;

public class X23MainActivity extends AppCompatActivity {

    //Register the app by following below link.
    //http://instagram.com/developer/
    //After registration copy client_id, client_secret and callback_rul from instagram Manage Client page.

    public static final String TAG = "OAuthWebViewClient";
    private String mAuthUrl;
    private String mTokenUrl;
    private String mAccessToken;
    //Create a dialog object, that will contain the WebView
    private Dialog webViewDialog;
    private ProgressDialog mSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_x23_main);

        mAuthUrl = X23_IG_AUTH_URL
                + "?client_id="
                + X23_IG_CLIENT_ID
                + "&redirect_uri="
                + X23_IG_CALLBACK_URL
                + "&response_type=code&display=touch&scope=likes+comments+relationships";

        // mAuthUrl = X23_IG_CALLBACK_URL;

        Button buttonOne = (Button) findViewById(R.id.connectIG);
        buttonOne.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                displayWebVidew();

            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void displayWebVidew() {
        //a WebView object to display a web page
        WebView webView;
        //The button to launch the WebView dialog
        Button btLaunchWVD;
        //The button that closes the dialog
        Button btClose;
        //Create a new dialog
        webViewDialog = new Dialog(this);

        mSpinner = new ProgressDialog(webViewDialog.getContext());
        mSpinner.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mSpinner.setMessage("Loading...");

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

    private class OAuthWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.d(TAG, "Redirecting URL " + url);

            if (url.startsWith(X23_IG_CALLBACK_URL)) {
                String urls[] = url.split("=");
                //    mListener.onComplete(urls[1]);
                webViewDialog.dismiss();
                return true;
            }
            return false;
        }

        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            Log.d(TAG, "Page error: " + description);

            super.onReceivedError(view, errorCode, description, failingUrl);
            // mListener.onError(description);
            // InstagramLoginDialog.this.dismiss();
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            Log.d(TAG, "Loading URL: " + url);
            mSpinner.show();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            //String title = mDialogWebView.getTitle();
            Log.d(TAG, "onPageFinished URL: " + url);
            mSpinner.dismiss();
        }

    }

}
