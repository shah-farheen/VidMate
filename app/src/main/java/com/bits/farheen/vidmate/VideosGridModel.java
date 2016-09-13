package com.bits.farheen.vidmate;

/**
 * Created by farheen on 9/14/16
 */
public class VideosGridModel {
    String videoPath;
    String videoMimeType;
    String videoTitle;
    long videoDuration;

    public VideosGridModel() {

    }

    public VideosGridModel(String videoPath, String videoMimeType, String videoTitle, long videoDuration) {
        this.videoPath = videoPath;
        this.videoMimeType = videoMimeType;
        this.videoTitle = videoTitle;
        this.videoDuration = videoDuration;
    }
}
