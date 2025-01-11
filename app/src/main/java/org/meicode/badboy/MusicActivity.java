package org.meicode.badboy;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

public class MusicActivity extends AppCompatActivity {
    public  static final int REQUEST_CODE=1;
    private RecyclerView recyclerView;
    private MusicAdapter adapter;
    private List<MusicFiles> musicList;
    private MediaPlayer mediaPlayer;
    private TextView songName, songArtist, durationPlayed, durationTotal;
    private SeekBar seekBar;
    private ImageButton btnPlayPause, btnPrevious, btnNext;
    private static final int REQUEST_PERMISSION = 1;
    private int currentSongIndex = -1;
    private ImageView imageView;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Set up the toolbar with back button
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Music Screen");
            SpannableString title = new SpannableString("Music Screen");
            title.setSpan(new ForegroundColorSpan(Color.WHITE), 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            getSupportActionBar().setTitle(title);
        }
        recyclerView = findViewById(R.id.recyclerView);
        songName = findViewById(R.id.songName);
        songArtist = findViewById(R.id.song_artist);
        imageView = findViewById(R.id.imageView);
        seekBar = findViewById(R.id.seekBar);
        btnPlayPause = findViewById(R.id.btnPlayPause);
        btnPrevious = findViewById(R.id.btnPrevious);
        btnNext = findViewById(R.id.btnNext);
        durationPlayed = findViewById(R.id.durationPlayed);
        durationTotal = findViewById(R.id.durationTotal);

        mediaPlayer = new MediaPlayer();

        // Request permissions to read external storage
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
        } else {
            loadMusic();
        }

        // Play/Pause button functionality
        btnPlayPause.setOnClickListener(v -> {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                btnPlayPause.setImageResource(android.R.drawable.ic_media_play);
            } else {
                mediaPlayer.start();
                btnPlayPause.setImageResource(android.R.drawable.ic_media_pause);
                updateSeekBar();
            }
        });

        // Previous and Next buttons
        btnPrevious.setOnClickListener(v -> playPreviousSong());
        btnNext.setOnClickListener(v -> playNextSong());

        // Set up SeekBar listener
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                handler.removeCallbacksAndMessages(null); // Pause SeekBar updates
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
                updateSeekBar(); // Resume updates
            }
        });



    }
    private void loadMusic() {
        musicList = new ArrayList<>();
        ContentResolver contentResolver = getContentResolver();
        android.database.Cursor cursor = contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.ALBUM},
                null, null, null
        );

        if (cursor != null && cursor.moveToFirst()) {
            int titleColumn = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int artistColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int dataColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            int albumColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);

            do {
                String title = cursor.getString(titleColumn);
                String artist = cursor.getString(artistColumn);
                String path = cursor.getString(dataColumn);
                String album = cursor.getString(albumColumn);
                musicList.add(new MusicFiles(title, artist, path, album));
            } while (cursor.moveToNext());
            cursor.close();
        }

        adapter = new MusicAdapter(musicList, this, music -> playMusic(music));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void playMusic(MusicFiles music) {
         try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(music.getPath());
            mediaPlayer.prepare();
            mediaPlayer.start();

            songName.setText(music.getTitle());
            songArtist.setText(music.getArtist());

            // Set album art
            Bitmap albumArt = music.getAlbumArt();
            if (albumArt != null) {
                imageView.setImageBitmap(albumArt);
            } else {
                imageView.setImageResource(R.drawable.ic_launcher_background); // Default image
            }

            btnPlayPause.setImageResource(android.R.drawable.ic_media_pause);
            currentSongIndex = musicList.indexOf(music);

            // Set total duration and initialize SeekBar
            int totalDuration = mediaPlayer.getDuration();
            durationTotal.setText(formatDuration(totalDuration));
            seekBar.setMax(totalDuration);
            seekBar.setProgress(0);
            updateSeekBar();

            // Set OnCompletionListener to play the next song
            mediaPlayer.setOnCompletionListener(mp -> playNextSong());

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error playing music", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateSeekBar() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    int currentPosition = mediaPlayer.getCurrentPosition();
                    seekBar.setProgress(currentPosition);
                    durationPlayed.setText(formatDuration(currentPosition));
                    handler.postDelayed(this, 1000);
                }
            }
        }, 1000);
    }

    private String formatDuration(int duration) {
        int minutes = (duration / 1000) / 60;
        int seconds = (duration / 1000) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    private void playNextSong() {
        if (currentSongIndex < musicList.size() - 1) {
            currentSongIndex++;
        } else {
            currentSongIndex = 0; // Loop back to the first song
        }
        playMusic(musicList.get(currentSongIndex));
    }

    private void playPreviousSong() {
        if (currentSongIndex <= 0) {
            Toast.makeText(this, "No previous song available", Toast.LENGTH_SHORT).show();
            return;
        }
        currentSongIndex--;
        playMusic(musicList.get(currentSongIndex));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            btnPlayPause.setImageResource(android.R.drawable.ic_media_play);
        }
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        handler.removeCallbacksAndMessages(null);
    }
}