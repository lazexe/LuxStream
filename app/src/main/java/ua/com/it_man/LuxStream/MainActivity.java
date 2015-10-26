package ua.com.it_man.LuxStream;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity {

    private static final String LUX_VIDEO_STREAM_ADDRESS =
            "http://stream1.luxnet.ua/luxstudio/smil:luxstudio.stream.smil/playlist.m3u8";

    private static final int FADE_IN_TIME = 2000;
    private static final int FADE_OUT_TIME = 100;

    private static final String IS_PLAYING_KEY = "IS_PLAYING";

    private VideoView videoView;
    private ImageButton statusButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ua.com.it_man.LuxStream.R.layout.activity_main);
        videoView = (VideoView) findViewById(ua.com.it_man.LuxStream.R.id.video_view);
        statusButton = (ImageButton) findViewById(R.id.status_button);
        initStatusButtonClickListener();
        initTouchListener();
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
        restoreStreamerState(savedInstanceState);
    }

    private void initStatusButtonClickListener() {
        statusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleScreenTouch();
            }
        });
    }

    private void initTouchListener() {
        videoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                handleScreenTouch();
                return false;
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        videoView.stopPlayback();
        videoView.suspend();
        outState.putBoolean(IS_PLAYING_KEY, videoView.isPlaying());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        restoreStreamerState(savedInstanceState);
    }

    private void restoreStreamerState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            boolean isPlaying = savedInstanceState.getBoolean(IS_PLAYING_KEY, true);
            if (isPlaying)
                startStreamVideo();
        }
        statusButton.setImageResource(R.drawable.ic_action_pause);
        statusButton.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!videoView.isPlaying()) {
            showStatusButton();
        }
    }

    private void handleScreenTouch() {
        if (videoView.isPlaying()) {
            videoView.pause();
            showStatusButton();
        } else {
            startStreamVideo();
            hideStatusButton();
        }
    }

    private void showStatusButton() {
        statusButton.setImageResource(R.drawable.ic_action_pause);
        statusButton.animate().setDuration(FADE_OUT_TIME).alpha(1f).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                statusButton.setVisibility(View.VISIBLE);
            }
        });
    }

    private void hideStatusButton() {
        statusButton.setImageResource(R.drawable.ic_action_play);
        statusButton.animate().setDuration(FADE_IN_TIME).alpha(0f).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                statusButton.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void startStreamVideo() {
        Uri videoUri = Uri.parse(LUX_VIDEO_STREAM_ADDRESS);
        videoView.setVideoURI(videoUri);
        videoView.start();
    }
}