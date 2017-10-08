package x23.instxag23ram;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class X25ShowPhotoDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private boolean mUserHasLiked;
    private int mLikesCount, mCommentsCount, mPosition;
    private String mImgUrl;
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
}
