package com.bits.farheen.vidmate;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

        if(ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    Constants.readStorageRequestCode);
        }
        else {
            queryVideoDataBase();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == Constants.readStorageRequestCode){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                queryVideoDataBase();
            }
            else {
                Toast.makeText(getApplicationContext(), "Read Permission Needed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void queryVideoDataBase(){
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
