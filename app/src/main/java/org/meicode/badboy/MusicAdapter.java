package org.meicode.badboy;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MusicViewHolder> {

    private List<MusicFiles> musicList;
    private Context context;
    private OnMusicClickListener listener;

    public interface OnMusicClickListener {
        void onMusicClick(MusicFiles music);
    }

    public MusicAdapter(List<MusicFiles> musicList, Context context, OnMusicClickListener listener) {
        this.musicList = musicList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.music_items, parent, false);
        return new MusicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicViewHolder holder, int position) {
        MusicFiles music = musicList.get(position);

        // Bind data
        holder.fileName.setText(music.getTitle());
        byte[] image =getAlbumArt(musicList.get(position).getPath());
        holder.itemView.setOnClickListener(v -> listener.onMusicClick(music));

        if(image != null){
            Glide.with(context).asBitmap()
                    .load(image)
                    .circleCrop()
                    .into(holder.albumArt);
        }else {
            Glide.with(context)
                    .load(R.drawable.ic_launcher_background)
                    .circleCrop()
                    .into(holder.albumArt);
        }

        // Optional: If you are using the menuMore for options
        if (holder.menuMore != null) {
            holder.menuMore.setOnClickListener(v -> {
                // Handle menu options here
            });
        }
    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }

    static class MusicViewHolder extends RecyclerView.ViewHolder {
        TextView fileName;
        ImageView albumArt, menuMore;

        public MusicViewHolder(@NonNull View itemView) {
            super(itemView);
            fileName = itemView.findViewById(R.id.music_file_name);
            albumArt = itemView.findViewById(R.id.music_img);
            // Ensure this exists in the layout or set it null-safe
        }
    }
    private byte[] getAlbumArt(String uri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        byte[] art = null;
        try {
            retriever.setDataSource(uri);
            art = retriever.getEmbeddedPicture();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                retriever.release();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return art;
    }
}

