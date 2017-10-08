package x23.instxag23ram;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import static android.content.ContentValues.TAG;
import static x23.instxag23ram.X23Credentials.X23_IG_CALLBACK_URL;

/**
 * Created by JITENDRA KUMAR  on 10/6/17.
 */

public class X23InstagramLoginDialog extends DialogFragment {

    static final FrameLayout.LayoutParams FILL = new FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT);
    Context mContext;
    String mUrl;
    OAuthDialogFragmentListener mListener;
    private WebView mDialogWebView;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (OAuthDialogFragmentListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUrl = getArguments().getString("url");
    }

    /**
     * The system calls this to get the DialogFragment's layout, regardless
     * of whether it's being displayed as a dialog or an embedded fragment.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout to use as dialog or embedded fragment
        return inflater.inflate(R.layout.signin_x23_dialog, container, false);
    }

    /**
     * The system calls this only when creating the layout in a dialog.
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        LayoutInflater inflater = dialog.getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.signin_x23_dialog, null);
        mDialogWebView = dialogView.findViewById(R.id.signInWebView);//signInWebView);
        setUpWebView();
        Button mButtonAddCoin = dialogView.findViewById(R.id.cancel);
        mButtonAddCoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getDialog().getContext().getApplicationContext(), "You will" +
                        " receive 5 coins!", Toast.LENGTH_SHORT).show();
            }
        });


        return dialog;
    }

//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        // Use the Builder class for convenient dialog construction
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        dismiss();
//                    }});
//        builder.setTitle(R.string.login);
//        setUpWebView(builder);
//
//        // Create the AlertDialog object and return it
//         return builder.create();
//    }

    private void setUpWebView() {
        //  mDialogWebView = new WebView(getContext());
//        mDialogWebView.setVerticalScrollBarEnabled(false);
//        mDialogWebView.setHorizontalScrollBarEnabled(false);
//        //Set the user agent
//        mDialogWebView.getSettings().setUserAgentString("AndroidWebView");
//        //Clear the cache
//        mDialogWebView.clearCache(true);
//        mDialogWebView.loadUrl(mUrl);
//        mDialogWebView.setVerticalScrollBarEnabled(false);
//        mDialogWebView.setHorizontalScrollBarEnabled(false);
//        mDialogWebView.setWebViewClient(new OAuthWebViewClient());
//        mDialogWebView.getSettings().setJavaScriptEnabled(true);

        //      mDialogWebView.setLayoutParams(FILL);


        //Scroll bars should not be hidden
        mDialogWebView.setScrollbarFadingEnabled(false);
        //Disable the horizontal scroll bar
        mDialogWebView.setHorizontalScrollBarEnabled(false);
        //Enable JavaScript
        mDialogWebView.getSettings().setJavaScriptEnabled(true);
        //Set the user agent
        mDialogWebView.getSettings().setUserAgentString("AndroidWebView");
        //Clear the cache
        mDialogWebView.clearCache(true);


        mDialogWebView.setVerticalScrollBarEnabled(false);
        mDialogWebView.setHorizontalScrollBarEnabled(false);
        mDialogWebView.setWebViewClient(new OAuthWebViewClient());

        // Set WebView client
        mDialogWebView.setWebChromeClient(new WebChromeClient());
        //Make the webview load the specified URL
        mDialogWebView.loadUrl(mUrl);
    }

    public interface OAuthDialogFragmentListener {
        void onComplete(String accessToken);

        void onError(String error);
    }

    private class OAuthWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.d(TAG, "Redirecting URL " + url);

            if (url.startsWith(X23_IG_CALLBACK_URL)) {
                String urls[] = url.split("=");
                mListener.onComplete(urls[1]);
                X23InstagramLoginDialog.this.dismiss();
                return true;
            }

            view.loadUrl(url);
            return true;
            //return false;
        }

        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            Log.d(TAG, "Page error: " + description);

            super.onReceivedError(view, errorCode, description, failingUrl);
            mListener.onError(description);
            X23InstagramLoginDialog.this.dismiss();
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Log.d(TAG, "Loading URL: " + url);

            super.onPageStarted(view, url, favicon);
            // mSpinner.show();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            String title = mDialogWebView.getTitle();
            if (title != null && title.length() > 0) {
                //      mTitle.setText(title);
            }
            Log.d(TAG, "onPageFinished URL: " + url);
            view.setEnabled(true);

            //   mSpinner.dismiss();
        }

    }
}
