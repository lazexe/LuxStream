package ua.com.it_man.LuxStream;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String LUX_VIDEO_STREAM_ADDRESS =
            "http://stream1.luxnet.ua/luxstudio/smil:luxstudio.stream.smil/playlist.m3u8";

    private static final int FADE_IN_TIME = 2000;
    private static final int FADE_OUT_TIME = 100;

    private static final String IS_PLAYING_KEY = "IS_PLAYING";

    private VideoView videoView;
    private ImageButton statusImageButton;
    private Button rateButton;
    private Button shareButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ua.com.it_man.LuxStream.R.layout.activity_main);
        videoView = (VideoView) findViewById(ua.com.it_man.LuxStream.R.id.video_view);
        statusImageButton = (ImageButton) findViewById(R.id.status_button);
        rateButton = (Button) findViewById(R.id.rate_button);
        shareButton = (Button) findViewById(R.id.share_button);
        initActionButtonClickListener();
        initTouchListener();
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                switch (what) {
                    case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                        Toast.makeText(getApplicationContext(), "Unknown error!", Toast.LENGTH_SHORT)
                                .show();
                        return true;
                    case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                        Toast.makeText(getApplicationContext(), "Server died!", Toast.LENGTH_SHORT)
                                .show();
                        return true;
                    default:
                        return false;
                }
            }
        });
        restoreStreamerState(savedInstanceState);
    }


    @Override
    public void onClick(View v) {
        handleScreenTouch(v);
    }

    private void initActionButtonClickListener() {
        statusImageButton.setOnClickListener(this);
        rateButton.setOnClickListener(this);
        shareButton.setOnClickListener(this);
    }


    private void initTouchListener() {
        videoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                handleScreenTouch(v);
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
        statusImageButton.setImageResource(R.drawable.ic_action_pause);
        statusImageButton.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!videoView.isPlaying()) {
            showStatusButton();
        }
    }

    private void handleScreenTouch(View view) {
        if (videoView.isPlaying()) {
            videoView.pause();
            showStatusButton();
            showView(rateButton);
            showView(shareButton);
        } else {
            int clickedId = view.getId();
            if (clickedId != R.id.video_view && clickedId != R.id.status_button) {
                if (clickedId == R.id.share_button) {
                    shareApplication();
                }
            } else {
                startStreamVideo();
                hideStatusButton();
                hideView(rateButton);
                hideView(shareButton);
            }
        }
    }

    private void shareApplication() {
        String shareMessage = getString(R.string.share_message);
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
        shareIntent.setType("text/plain");
        startActivity(shareIntent);
    }

    private void showStatusButton() {
        statusImageButton.setImageResource(R.drawable.ic_action_pause);
        showView(statusImageButton);
    }

    private void hideStatusButton() {
        statusImageButton.setImageResource(R.drawable.ic_action_play);
        hideView(statusImageButton);
    }

    private void startStreamVideo() {
        Uri videoUri = Uri.parse(LUX_VIDEO_STREAM_ADDRESS);
        videoView.setVideoURI(videoUri);
        videoView.start();
    }

    private void showView(final View view) {
        view.animate().setDuration(FADE_OUT_TIME).alpha(1f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void hideView(final View view) {
        view.animate().setDuration(FADE_IN_TIME).alpha(0f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setVisibility(View.INVISIBLE);
                    }
                });
    }
}