package x23.instxag23ram;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class X23ShowPhotosActivity extends AppCompatActivity {

    public static final String TAG_DATA = "data";
    public static final String TAG_IMAGES = "images";
    public static final String TAG_THUMBNAIL = "thumbnail";
    public static final String TAG_URL = "url";
    private static int WHAT_ERROR = 1;
    private GridView gvAllImages;
    private HashMap<String, String> userInfo;
    private ArrayList<String> imageThumbList = new ArrayList<String>();
    private int WHAT_FINALIZE = 0;
    private ProgressDialog pd;
    private String mAccessToken;
    private Handler handler = new Handler(new Handler.Callback() {

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
        gvAllImages = (GridView) findViewById(R.id.showPhotosIG);
        userInfo = (HashMap<String, String>) getIntent().getSerializableExtra(
                "userInfo");
        mAccessToken = getIntent().getStringExtra(
                "session");
        getAllMediaImages();
    }

    private void setImageGridAdapter() {
        gvAllImages.setAdapter(new X23GridListAdapter(getContext(), imageThumbList));
        gvAllImages.invalidateViews();
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
                                    + userInfo.get(X23MainActivity.TAG_ID)
                                    + "/media/recent/?access_token="
                                    + mAccessToken);
                    JSONArray data = jsonObject.getJSONArray(TAG_DATA);
                    for (int data_i = 0; data_i < data.length(); data_i++) {
                        JSONObject data_obj = data.getJSONObject(data_i);

                        JSONObject images_obj = data_obj
                                .getJSONObject(TAG_IMAGES);

                        JSONObject thumbnail_obj = images_obj
                                .getJSONObject(TAG_THUMBNAIL);

                        // String str_height =
                        // thumbnail_obj.getString(TAG_HEIGHT);
                        //
                        // String str_width =
                        // thumbnail_obj.getString(TAG_WIDTH);

                        String str_url = thumbnail_obj.getString(TAG_URL);
                        imageThumbList.add(str_url);
                    }

                    System.out.println("jsonObject::" + jsonObject);

                } catch (Exception exception) {
                    exception.printStackTrace();
                    what = WHAT_ERROR;
                }
                // pd.dismiss();
                handler.sendEmptyMessage(what);
            }
        }).start();
    }
}
