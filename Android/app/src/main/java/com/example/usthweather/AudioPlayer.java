//External Storage is restricted on higher api
//package com.example.usthweather;
//
//import android.content.Context;
//import android.media.MediaPlayer;
//import android.os.Environment;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//
//public class AudioPlayer {
//        private MediaPlayer mediaPlayer;
//        private String pathToSDCard;
//
//        public AudioPlayer(Context context) {
//                pathToSDCard = Environment.getExternalStorageDirectory().toString();
//                copyFileToExternalStorage(context);
//        }
//
//        private void copyFileToExternalStorage(Context context) {
//                String filename = "music.mp3";
//                File outputFile = new File(pathToSDCard, filename);
//
//                try {
//                        InputStream inputStream = context.getResources().openRawResource(R.raw.music);
//                        OutputStream outputStream = new FileOutputStream(outputFile);
//                        byte[] buffer = new byte[8192];
//                        int length;
//                        while ((length = inputStream.read(buffer)) > 0) {
//                                outputStream.write(buffer, 0, length);
//                        }
//                        inputStream.close();
//                        outputStream.close();
//                } catch (IOException e) {
//                        e.printStackTrace();
//                }
//        }
//
//        public void playAudio() {
//                if (mediaPlayer == null) {
//                        mediaPlayer = new MediaPlayer();
//                        try {
//                                mediaPlayer.setDataSource(pathToSDCard + "/music.mp3");
//                                mediaPlayer.prepare();
//                                mediaPlayer.start();
//                        } catch (IOException e) {
//                                e.printStackTrace();
//                        }
//                } else {
//                        mediaPlayer.start();
//                }
//        }
//
//        public void stopAudio() {
//                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
//                        mediaPlayer.stop();
//                        mediaPlayer.release();
//                        mediaPlayer = null;
//                }
//        }
//}
package com.example.usthweather;

import android.content.Context;
import android.media.MediaPlayer;

public class AudioPlayer {
        private MediaPlayer mediaPlayer;
        private Context context;

        public AudioPlayer(Context context) {
                this.context = context;
        }

        public void playAudio() {
                if (mediaPlayer == null) {
                        mediaPlayer = MediaPlayer.create(context, R.raw.music);

                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                        // Release the MediaPlayer when playback is complete
                                        stopAudio();
                                        // Start playing again to loop the audio
                                        playAudio();
                                }
                        });

                        mediaPlayer.start();
                } else  {
                        mediaPlayer.start();
                }
        }

        public void stopAudio() {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        mediaPlayer = null;
                }
        }
}
