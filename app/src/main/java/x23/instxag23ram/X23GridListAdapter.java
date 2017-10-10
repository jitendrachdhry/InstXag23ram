package x23.instxag23ram;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import x23.instxag23ram.core.X25ImageLoader;

public class X23GridListAdapter extends BaseAdapter {
    // private Context context;
    private ArrayList<String> imageThumbList;
    private ArrayList<String> imageList;
    private ArrayList<String> imageLikeCountList;
    private ArrayList<String> imageCommentsCountList;
    private ArrayList<String> imageUserHasLikedList;

    private LayoutInflater inflater;
    private X25ImageLoader x25ImageLoader;

    public X23GridListAdapter(Context context, ArrayList<String> imageThumbList, ArrayList<String> imageList,
                              ArrayList<String> imageLikeCountList, ArrayList<String> imageCommentsCountList,
                              ArrayList<String> imageUserHasLikedList) {
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.imageThumbList = imageThumbList;
        this.imageList = imageList;
        this.imageLikeCountList = imageLikeCountList;
        this.imageCommentsCountList = imageCommentsCountList;
        this.imageUserHasLikedList = imageUserHasLikedList;

        this.x25ImageLoader = new X25ImageLoader(context, true);
    }

    @Override
    public int getCount() {
        return imageThumbList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = inflater.inflate(R.layout.photos_list_x23_inflater, null);
        Holder holder = new Holder();
        holder.mTPhoto = (ImageView) view.findViewById(R.id.igPhoto);
        holder.mLikes = (TextView) view.findViewById(R.id.likeCountTxt);
        holder.mComments = (TextView) view.findViewById(R.id.commentsCountTxt);

        holder.mLikes.setText(imageLikeCountList.get(position));
        holder.mComments.setText(imageCommentsCountList.get(position));

        x25ImageLoader.DisplayImage(imageThumbList.get(position), holder.mTPhoto);
        return view;
    }

    private class Holder {
        private ImageView mTPhoto;
        private TextView mComments;
        private TextView mLikes;
    }

}
