package com.happypig.picstream;

import com.happypig.picstream.PictureStreamStore.PicStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PictureStreamListAdapter extends BaseAdapter {
	private Context mContext;
	private PictureStreamStore mStreamStore;
	
    public PictureStreamListAdapter(Context context) {
        this.mContext = context;
        mStreamStore = PictureStreamStore.getInstance();
    }
    
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mStreamStore.getPicStreamNum();
	}

	@Override
	public PicStream getItem(int position) {
		// TODO Auto-generated method stub
		return mStreamStore.getPicStream(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater vi = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = convertView;
        if (view == null) {
            view = vi.inflate(R.layout.item_picture_stream_list, parent, false);
        }
        
        // Disable CropSqureTransformation, use StaggeredGridView instead
        // CropSquareTransformation cropTransform = new CropSquareTransformation();
        PicStream stream = getItem(position);

        ImageView coverImgView = (ImageView) view.findViewById(R.id.item_picture_stream_list_cover);
        
        int scaledBitmapWidth = coverImgView.getMeasuredWidth() > 0 ? coverImgView.getMeasuredWidth():mContext.getResources().getDisplayMetrics().widthPixels/2;
        Bitmap bmp = Utils.getBitmap(mContext, Uri.parse(stream.getCoverImgUri()), scaledBitmapWidth, scaledBitmapWidth);
        Bitmap cropBmp = Utils.cropImage(bmp, scaledBitmapWidth, scaledBitmapWidth);        
        coverImgView.setImageBitmap(Utils.getRoundedCornerBitmap(cropBmp));
        
        TextView nameTextView = (TextView) view.findViewById(R.id.item_picture_stream_list_name);
        nameTextView.setText(stream.getDisplayName());
        
        return view;
	}

}
