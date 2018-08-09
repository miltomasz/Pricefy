package com.plumya.pricefy.ui.results;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.plumya.pricefy.R;
import com.plumya.pricefy.data.local.model.WebsiteItem;
import com.plumya.pricefy.utils.UIUtil;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by miltomasz on 21/07/18.
 */

public class ResultsAdapter extends RecyclerView.Adapter<ResultsAdapter.ViewHolder> {

    private static final String LOG_TAG = ResultsAdapter.class.getSimpleName();
    private static final int TARGET_WIDTH = 240;
    private static final int TARGET_HEIGHT = 240;

    private Context context;
    private WebsiteItemOnClickHandler clickHandler;
    private List<WebsiteItem> websiteItems;

    public ResultsAdapter(Context context, List<WebsiteItem> websiteItems, WebsiteItemOnClickHandler clickHandler) {
        this.context = context;
        this.websiteItems = websiteItems;
        this.clickHandler = clickHandler;
    }

    @NonNull
    @Override
    public ResultsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.results_item_view, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultsAdapter.ViewHolder holder, int position) {
        WebsiteItem websiteItem = websiteItems.get(position);
        holder.mainTitleTv.setText(websiteItem.getMainTitle());
        String price = UIUtil.formatPrice(
                String.valueOf(websiteItem.getPriceFrom()),
                String.valueOf(websiteItem.getPriceTo())
        );
        holder.priceTv.setText(price);
        holder.ratingBar.setRating(websiteItem.getStars());
        Picasso.get()
                .load(websiteItem.getImageUri())
                .resize(TARGET_WIDTH, TARGET_HEIGHT)
                .centerCrop()
                .error(R.drawable.ic_baseline_photo_camera_24px)
                .into(holder.imageView);
        ViewCompat.setTransitionName(holder.imageView, String.valueOf(websiteItem.getId()));
    }

    @Override
    public int getItemCount() {
        return websiteItems.size();
    }

    public void update(List<WebsiteItem> websiteItems) {
        this.websiteItems.clear();
        this.websiteItems.addAll(websiteItems);
        notifyDataSetChanged();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.imageView)
        ImageView imageView;
        @BindView(R.id.mainTitleTv)
        TextView mainTitleTv;
        @BindView(R.id.priceTv)
        TextView priceTv;
        @BindView(R.id.ratingBar)
        RatingBar ratingBar;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            WebsiteItem websiteItem = websiteItems.get(position);
            clickHandler.onClick(websiteItem, imageView);
        }
    }

    public interface WebsiteItemOnClickHandler {
        void onClick(WebsiteItem websiteItem, ImageView imageView);
    }
}
