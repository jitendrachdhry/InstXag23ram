package x23.instxag23ram;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static x23.instxag23ram.X23Credentials.TAG_COMMENTS;
import static x23.instxag23ram.X23Credentials.TAG_COUNT;
import static x23.instxag23ram.X23Credentials.TAG_DATA;
import static x23.instxag23ram.X23Credentials.TAG_ID;
import static x23.instxag23ram.X23Credentials.TAG_IMAGES;
import static x23.instxag23ram.X23Credentials.TAG_LIKES;
import static x23.instxag23ram.X23Credentials.TAG_STAND_RESO;
import static x23.instxag23ram.X23Credentials.TAG_THUMBNAIL;
import static x23.instxag23ram.X23Credentials.TAG_URL;
import static x23.instxag23ram.X23Credentials.TAG_USER_HAS_LIKED;

public class X23ShowPhotosActivity extends AppCompatActivity {
    static final int SHOW_PHOTO_DETAIL_RESULT_REQUEST = 1;  // The request code to know if user like/unlike photo.
    private static int WHAT_ERROR = 1;
    private GridView mGVPhotos;
    private HashMap<String, String> mUserInfo;
    private ArrayList<String> mImageThumbList = new ArrayList<String>();
    private ArrayList<String> mImageMediaIdList = new ArrayList<String>();
    private ArrayList<String> mImageList = new ArrayList<String>();
    private ArrayList<String> mImageLikeCountList = new ArrayList<String>();
    private ArrayList<String> mImageCommentsCountList = new ArrayList<String>();
    private ArrayList<String> mImageUserHasLikedList = new ArrayList<String>();
    private int WHAT_FINALIZE = 0;
    private ProgressDialog pd;
    private String mAccessToken;
    private Handler mHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            if (pd != null && pd.isShowing())
                pd.dismiss();
            if (msg.what == WHAT_FINALIZE) {
                setImageGridAdapter();
            } else {
                Toast.makeText(getContext(), "Check your network.",
                        Toast.LENGTH_SHORT).show();
            }
            return false;
        }
    });

    private Context getContext() {
        return X23ShowPhotosActivity.this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_x23_show_photos);
        mGVPhotos = (GridView) findViewById(R.id.showPhotosIG);
        mUserInfo = (HashMap<String, String>) getIntent().getSerializableExtra(
                "userInfo");
        mAccessToken = getIntent().getStringExtra(
                "session");
        getAllMediaImages();

        mGVPhotos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Intent lInt = new Intent(X23ShowPhotosActivity.this, X25ShowPhotoDetailsActivity.class);
                lInt.putExtra("UserHasLiked", mImageUserHasLikedList.get(position));
                lInt.putExtra("commentsCount", Integer.parseInt(mImageCommentsCountList.get(position)));
                lInt.putExtra("likesCount", Integer.parseInt(mImageLikeCountList.get(position)));
                lInt.putExtra("position", position);
                lInt.putExtra("url", mImageList.get(position));
                lInt.putExtra("id", mImageMediaIdList.get(position));
                lInt.putExtra("access_token", mAccessToken);

                startActivityForResult(lInt, SHOW_PHOTO_DETAIL_RESULT_REQUEST);
            }
        });

    }

    private void setImageGridAdapter() {
        mGVPhotos.setAdapter(new X23GridListAdapter(getContext(), mImageThumbList, mImageList, mImageLikeCountList,
                mImageCommentsCountList, mImageUserHasLikedList));
        mGVPhotos.invalidateViews();
    }

    private void getAllMediaImages() {
        pd = ProgressDialog.show(getContext(), "", "Loading images...");
        new Thread(new Runnable() {

            @Override
            public void run() {
                int what = WHAT_FINALIZE;
                try {
                    // URL url = new URL(mTokenUrl + "&code=" + code);
                    X23JSONParser jsonParser = new X23JSONParser();

//https://api.instagram.com/v1/users/5811825526/media/recent/?access_token=5811825526.19ac6ed.4e8b5a395d5a4697b40fab9fde99a856
                    JSONObject jsonObject = jsonParser
                            .getJSONFromUrlByGet("https://api.instagram.com/v1/users/"
                                    + mUserInfo.get(TAG_ID)
                                    + "/media/recent/?access_token="
                                    + mAccessToken);
                    JSONArray data = jsonObject.getJSONArray(TAG_DATA);
                    for (int data_i = 0; data_i < data.length(); data_i++) {
                        JSONObject data_obj = data.getJSONObject(data_i);

                        JSONObject images_obj = data_obj.getJSONObject(TAG_IMAGES);
                        JSONObject thumbnail_obj = images_obj.getJSONObject(TAG_THUMBNAIL);
                        JSONObject images_url_obj = images_obj.getJSONObject(TAG_STAND_RESO);
                        JSONObject like_obj = data_obj.getJSONObject(TAG_LIKES);
                        JSONObject comments_obj = data_obj.getJSONObject(TAG_COMMENTS);

                        String str_url = thumbnail_obj.getString(TAG_URL);
                        mImageThumbList.add(str_url);

                        str_url = data_obj.getString(TAG_ID);
                        mImageMediaIdList.add(str_url);

                        str_url = data_obj.getString(TAG_USER_HAS_LIKED);
                        mImageUserHasLikedList.add(str_url);

                        str_url = images_url_obj.getString(TAG_URL);
                        mImageList.add(str_url);

                        str_url = like_obj.getString(TAG_COUNT);
                        mImageLikeCountList.add(str_url);

                        str_url = comments_obj.getString(TAG_COUNT);
                        mImageCommentsCountList.add(str_url);

                    }
//                    System.out.println("jsonObject::" + jsonObject);

                } catch (Exception exception) {
                    exception.printStackTrace();
                    what = WHAT_ERROR;
                }
                mHandler.sendEmptyMessage(what);
            }
        }).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == SHOW_PHOTO_DETAIL_RESULT_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.
                int pos = data.getIntExtra("position", -1);
                if (pos >= 0) {
                    int likesCount = data.getIntExtra("likesCount", 0);
                    mImageLikeCountList.set(pos, Integer.toString(likesCount));
                    mImageUserHasLikedList.set(pos, data.getStringExtra("userHasLiked"));
                }
                mGVPhotos.invalidateViews();
            }
        }
    }
}
