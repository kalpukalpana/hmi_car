package org.meicode.badboy;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;

import java.io.IOException;

public class MusicFiles {

    private String title;
    private String artist;
    private String path;
    private String album;

    public MusicFiles(String title, String artist, String path, String album) {
        this.title = title;
        this.artist = artist;
        this.path = path;
        this.album = album;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getPath() {
        return path;
    }

    public String getAlbum() {
        return album;
    }

    public Bitmap getAlbumArt() {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(path); // Using the music file's path to set the data source
            byte[] art = retriever.getEmbeddedPicture();
            if (art != null) {
                return BitmapFactory.decodeByteArray(art, 0, art.length); // Return album art if available
            }
        } catch (Exception e) {
            e.printStackTrace();
        }  finally {
            try {
                retriever.release();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null; // Return null if no album art is found
    }

}
