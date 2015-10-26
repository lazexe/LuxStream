package ua.com.it_man.LuxStream;

import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity {

    private static final String LUX_VIDEO_STREAM_ADDRESS =
            "http://stream1.luxnet.ua/luxstudio/smil:luxstudio.stream.smil/playlist.m3u8";

    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ua.com.it_man.LuxStream.R.layout.activity_main);
        videoView = (VideoView) findViewById(ua.com.it_man.LuxStream.R.id.video_view);
        initClickListener();
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                switch (what) {
                    case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                        Toast.makeText(getApplicationContext(), "Unknown error!", Toast.LENGTH_SHORT).show();
                        return true;
                    case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                        Toast.makeText(getApplicationContext(), "Server died!", Toast.LENGTH_SHORT).show();
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    private void initClickListener() {
        videoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (videoView.isPlaying()) {
                    videoView.pause();
                } else {
                    videoView.start();
                }
                return false;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Uri videoUri = Uri.parse(LUX_VIDEO_STREAM_ADDRESS);
        videoView.setVideoURI(videoUri);
        videoView.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        videoView.stopPlayback();
        videoView.suspend();
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoView.resume();
    }
}