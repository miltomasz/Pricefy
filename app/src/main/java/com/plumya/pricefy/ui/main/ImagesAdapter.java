package com.plumya.pricefy.ui.main;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.plumya.pricefy.R;
import com.plumya.pricefy.data.local.model.Image;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by miltomasz on 21/07/18.
 */

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ViewHolder> {

    private static final String LOG_TAG = ImagesAdapter.class.getSimpleName();
    private static final int TARGET_WIDTH = 240;
    private static final int TARGET_HEIGHT = 240;

    private Context context;
    private List<Image> images;

    public ImagesAdapter(Context context, List<Image> images) {
        this.context = context;
        this.images = images;
    }

    @NonNull
    @Override
    public ImagesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.images_item_view, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ImagesAdapter.ViewHolder holder, int position) {
        Image image = images.get(position);
        holder.labelsTv.setText(image.labelsToReadableString());
        holder.dateTv.setText(image.toDateString());
        Picasso.get()
                .load(new File(image.getUri()))
                .resize(TARGET_WIDTH, TARGET_HEIGHT)
                .centerCrop()
                .error(R.drawable.ic_baseline_photo_camera_24px)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public void update(List<Image> images) {
        this.images.clear();
        this.images.addAll(images);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.imageView)
        ImageView imageView;
        @BindView(R.id.labelsTv)
        TextView labelsTv;
        @BindView(R.id.dateTv)
        TextView dateTv;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }
}
