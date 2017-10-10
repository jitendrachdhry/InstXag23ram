package x23.instxag23ram;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URLEncoder;

import x23.instxag23ram.core.X23JSONParser;
import x23.instxag23ram.core.X25ImageLoader;

import static x23.instxag23ram.X23MainActivity.X26_ERROR;

public class X25ShowPhotoDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    public static int X26_UPDATE_DONE = 2;
    private boolean mUserHasLiked;
    private int mLikesCount, mCommentsCount, mPosition;
    private String mImgUrl, mId, mAccessToken;
    private X25ImageLoader x25ImageLoader;
    private ProgressDialog mAddDeleteLikeSpinner;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == X26_ERROR) {
                Toast.makeText(getContext(), getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
            } else if (msg.what == X26_UPDATE_DONE) {
                mUserHasLiked = !mUserHasLiked;
                setLikeImage();
                mLikesCount += mUserHasLiked ? 1 : -1;
                setLikeCountText();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_x25_show_photo_details);
        mUserHasLiked = (getIntent().getStringExtra("UserHasLiked").equalsIgnoreCase("true") == true) ?
                true : false;
        mLikesCount = getIntent().getIntExtra("likesCount", 0);
        mCommentsCount = getIntent().getIntExtra("commentsCount", 0);
        mPosition = getIntent().getIntExtra("position", 0);
        mImgUrl = getIntent().getStringExtra("url");
        mId = getIntent().getStringExtra("id");
        mAccessToken = getIntent().getStringExtra("access_token");

        x25ImageLoader = new X25ImageLoader(X25ShowPhotoDetailsActivity.this, false);
        setLikeImageViewListener();
        setPhotoLayoutDetails();
        setLikeImageViewListener();
        updateImage();
        mAddDeleteLikeSpinner = new ProgressDialog(X25ShowPhotoDetailsActivity.this);
        mAddDeleteLikeSpinner.setCancelable(false);
        mAddDeleteLikeSpinner.setMessage(getString(R.string.updating));
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.likeIG) {
            setPhotoLike();
        }
    }

    private X25ShowPhotoDetailsActivity getContext() {
        return X25ShowPhotoDetailsActivity.this;
    }

    private void setPhotoLayoutDetails() {
        setLikeImage();
        setLikeCountText();
        setCommentsCountText();
    }

    private void updateImage() {
        ImageView iv = (ImageView) findViewById(R.id.igPhoto);
        x25ImageLoader.DisplayImage(mImgUrl, iv);
    }

    private void setLikeImageViewListener() {
        ImageView tv = (ImageView) findViewById(R.id.likeIG);
        tv.setOnClickListener(X25ShowPhotoDetailsActivity.this);
    }

    private void setLikeImage() {
        ImageView iv = (ImageView) findViewById(R.id.likeIG);
        if (mUserHasLiked)
            iv.setImageResource(R.drawable.user_liked);
        else
            iv.setImageResource(R.drawable.like_small);
    }

    private void setLikeCountText() {
        TextView tv = (TextView) findViewById(R.id.likeCountTxt);
        tv.setText(Integer.toString(mLikesCount));
    }

    private void setCommentsCountText() {
        TextView tv = (TextView) findViewById(R.id.commentsCountTxt);
        tv.setText(Integer.toString(mCommentsCount));
    }

    @Override
    public void onBackPressed() {
        // Inform Activity if user has changed his like.
        Intent returnIntent = new Intent();
        returnIntent.putExtra("position", mPosition);
        returnIntent.putExtra("likesCount", mLikesCount);
        if (mUserHasLiked)
            returnIntent.putExtra("userHasLiked", "true");
        else
            returnIntent.putExtra("userHasLiked", "false");
        setResult(Activity.RESULT_OK, returnIntent);
        super.onBackPressed();
    }

    //Start a new thread for Instagram like POST API callback.
    private void setPhotoLike() {
        mAddDeleteLikeSpinner.show();

        new Thread(new Runnable() {

            @Override
            public void run() {
                String response = null;
                try {
                    //https://www.instagram.com/developer/endpoints/likes/#post_likes
                    X23JSONParser jp = new X23JSONParser();
                    if (!mUserHasLiked) {
                        String url = "https://api.instagram.com/v1/media/" + mId + "/likes";
                        String postData = URLEncoder.encode("access_token", "UTF-8") + "=" +
                                URLEncoder.encode(mAccessToken, "UTF-8");
                        response = jp.getStringFromUrlByPost(url, postData, "POST");
                        Log.d("X25ShowPhoto", response);
                    } else {
                        String url = "https://api.instagram.com/v1/media/" + mId + "/likes?access_token=" + mAccessToken;
                        response = jp.getStringFromUrlByPost(url, null, "DELETE");
                        Log.d("X25ShowPhoto", response);
                    }
                } catch (Exception io) {
                    Log.d("X25ShowPhoto", io.toString());
                }
                mAddDeleteLikeSpinner.dismiss();
                if (response != null && response.contains("200")) {
                    mHandler.sendMessage(mHandler.obtainMessage(X26_UPDATE_DONE, 1, 0));
                } else
                    mHandler.sendMessage(mHandler.obtainMessage(X26_ERROR, 1, 0));
            }
        }).start();
    }
}
