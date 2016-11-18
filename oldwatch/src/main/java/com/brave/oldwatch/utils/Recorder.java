package com.brave.oldwatch.utils;

import android.media.MediaRecorder;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * Created by Brave on 2016/11/18.
 */

public class Recorder {

    private MediaRecorder myRecorder;
    private String path;
    private String paths = path;
    private File saveFilePath;


    public Recorder(){



    }

    public void start(){
        try {
            myRecorder = new MediaRecorder();
            myRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            myRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
            myRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                try {
                    path = Environment.getExternalStorageDirectory().getCanonicalPath().toString() + "/OldWatch";
                    File files = new File(path);
                    if (!files.exists()) {
                        files.mkdir();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            paths = path+ "/" + System.currentTimeMillis()+ ".amr";
            saveFilePath = new File(paths);
            myRecorder.setOutputFile(saveFilePath.getAbsolutePath());
            saveFilePath.createNewFile();
            myRecorder.prepare();
            myRecorder.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public File stop(){
        if (saveFilePath.exists() && saveFilePath != null) {
            myRecorder.stop();
            myRecorder.reset();
        }
        return saveFilePath;
    }



}
