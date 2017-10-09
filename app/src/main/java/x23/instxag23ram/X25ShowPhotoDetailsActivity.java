package x23.instxag23ram;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.net.URLEncoder;

public class X25ShowPhotoDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private boolean mUserHasLiked;
    private int mLikesCount, mCommentsCount, mPosition;
    private String mImgUrl, mId, mAccessToken;
    private X25ImageLoader x25ImageLoader;

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
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.likeIG) {
            mUserHasLiked = !mUserHasLiked;
            setLikeImage();
            mLikesCount += mUserHasLiked ? 1 : -1;
            setLikeCountText();
            setPhotoLike();
        }
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

    private void setPhotoLike() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    X23JSONParser jp = new X23JSONParser();
                    if (mUserHasLiked) {
                        String url = "https://api.instagram.com/v1/media/" + mId + "/likes";
                        String postData = URLEncoder.encode("access_token", "UTF-8") + "=" +
                                URLEncoder.encode(mAccessToken, "UTF-8");
                        String response = jp.getStringFromUrlByPost(url, postData, "POST");
                        Log.d("X25ShowPhoto", response);
                    } else {
                        String url = "https://api.instagram.com/v1/media/" + mId + "/likes?access_token=" + mAccessToken;
                        String response = jp.getStringFromUrlByPost(url, null, "DELETE");
                        Log.d("X25ShowPhoto", response);
                    }
                } catch (Exception io) {
                    Log.d("X25ShowPhoto", io.toString());
                }
            }
        }).start();
    }
}
