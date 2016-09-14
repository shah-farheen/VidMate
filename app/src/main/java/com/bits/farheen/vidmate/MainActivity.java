package com.bits.farheen.vidmate;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    Context mContext;
    TextView textUserName;
    ImageView imageProfile;
    RecyclerView gridVideos;

    ArrayList<VideosGridModel> videoData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        textUserName = (TextView) findViewById(R.id.text_name);
        imageProfile = (ImageView) findViewById(R.id.image_profile);
        gridVideos = (RecyclerView) findViewById(R.id.grid_videos);
        SharedPreferences dataFile = getSharedPreferences(Constants.dataFile, MODE_APPEND);
        textUserName.setText(dataFile.getString(Constants.userName, "John Doe"));
        Picasso.with(mContext).load(dataFile.getString(Constants.dpUrl, null)).fit().centerCrop().into(imageProfile);

        String[] projection = {MediaStore.Video.VideoColumns.DATA,
                MediaStore.Video.VideoColumns.MIME_TYPE,
                MediaStore.Video.VideoColumns.TITLE,
                MediaStore.Video.VideoColumns.DURATION};

        Cursor videosCursor = getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                projection, null, null, null);


        if (videosCursor != null) {
            while (videosCursor.moveToNext()) {
                Log.e(TAG, "onCreate: " + videosCursor.getString(1));
                VideosGridModel model = new VideosGridModel(videosCursor.getString(0),
                        videosCursor.getString(1),
                        videosCursor.getString(2),
                        videosCursor.getLong(3));
                videoData.add(model);
            }
            videosCursor.close();
        }

        gridVideos.setLayoutManager(new GridLayoutManager(mContext, 2));
        gridVideos.setAdapter(new VideosGridAdapter(mContext, videoData));
    }
}
