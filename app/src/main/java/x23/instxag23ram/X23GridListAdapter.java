package x23.instxag23ram;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

public class X23GridListAdapter extends BaseAdapter {
    // private Context context;
    private ArrayList<String> imageThumbList;
    private ArrayList<String> imageThumbLikeCountList;
    private ArrayList<String> imageThumbCommentsCountList;

    private LayoutInflater inflater;
    private X25ImageLoader x25ImageLoader;

    public X23GridListAdapter(Context context, ArrayList<String> imageThumbList) {
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.imageThumbList = imageThumbList;
        this.x25ImageLoader = new X25ImageLoader(context);
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
        holder.ivPhoto = (ImageView) view.findViewById(R.id.igPhoto);
//        try {
//            URL url = new URL(imageThumbList.get(2));
//            Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
//            holder.ivPhoto.setImageBitmap(bmp);
//        } catch(Exception e) {}
        x25ImageLoader.DisplayImage(imageThumbList.get(position), holder.ivPhoto);
        return view;
    }

    private class Holder {
        private ImageView ivPhoto;
    }

}
