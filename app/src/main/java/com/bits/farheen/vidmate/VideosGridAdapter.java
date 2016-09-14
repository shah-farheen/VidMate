package com.bits.farheen.vidmate;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by farheen on 9/14/16
 */
public class VideosGridAdapter extends RecyclerView.Adapter<VideosGridAdapter.VideosHolder> {

    private static final String TAG = "VideosGridAdapter";
    private List<VideosGridModel> data;
    private LayoutInflater inflater;
    private Context mContext;

    public VideosGridAdapter(Context context, List<VideosGridModel> data) {
        this.data = data;
        mContext = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public VideosHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.videos_grid_adapter, parent, false);
        return new VideosHolder(view);
    }

    @Override
    public void onBindViewHolder(VideosHolder holder, int position) {
        holder.textTitle.setText(data.get(position).videoTitle);
        long videoSeconds = data.get(position).videoDuration / 1000;
        long videoMinutes = videoSeconds / 60;
        videoSeconds -= (videoMinutes * 60);
        String videoDuration = videoMinutes > 0 ? videoMinutes+"m " + videoSeconds+"s" : videoSeconds+"s";
        holder.textDuration.setText(videoDuration);

        final String videoPath = data.get(position).videoPath;
        final String mimeType = data.get(position).videoMimeType;

        holder.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "onClick: ");
                playVideo(Uri.parse(videoPath), mimeType);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class VideosHolder extends RecyclerView.ViewHolder {

        ImageView imageThumbnail;
        TextView textTitle;
        TextView textDuration;
        View rootLayout;

        public VideosHolder(View itemView) {
            super(itemView);
            imageThumbnail = (ImageView) itemView.findViewById(R.id.image_thumbnail);
            textTitle = (TextView) itemView.findViewById(R.id.text_title);
            textDuration = (TextView) itemView.findViewById(R.id.text_duration);
            rootLayout = itemView;
        }
    }

    public void playVideo(Uri videoUri, String mimeType) {
        Intent playIntent = new Intent(Intent.ACTION_VIEW)
                .setDataAndType(videoUri, mimeType);
        if (playIntent.resolveActivity(mContext.getPackageManager()) != null) {
            mContext.startActivity(playIntent);
        } else {
            Toast.makeText(mContext.getApplicationContext(), "No App to Play Video", Toast.LENGTH_SHORT).show();
        }
    }
}
