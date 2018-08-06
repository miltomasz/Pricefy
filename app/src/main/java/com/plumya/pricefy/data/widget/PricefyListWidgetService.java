package com.plumya.pricefy.data.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.plumya.pricefy.R;
import com.plumya.pricefy.data.PricefyRepository;
import com.plumya.pricefy.data.local.model.Image;
import com.plumya.pricefy.data.network.NetworkDataSource;
import com.plumya.pricefy.di.Injector;
import com.plumya.pricefy.utils.BitmapUtil;

import java.util.List;

/**
 * Serves data for widget
 */

public class PricefyListWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new PricefyListRemoteViewsFactory(this.getApplicationContext());
    }
}

class PricefyListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private static final String LOG_TAG = PricefyListRemoteViewsFactory.class.getSimpleName();

    public static final String IMAGE_ID = "imageId";

    Context context;
    PricefyRepository repository;
    List<Image> images;

    public PricefyListRemoteViewsFactory(Context applicationContext) {
        this.context = applicationContext;
        this.repository = Injector.provideRepository(context);
    }

    @Override
    public void onCreate() {
    }

    //called on start and when notifyAppWidgetViewDataChanged is called
    @Override
    public void onDataSetChanged() {
        // Get images
        images = repository.getImagesForWidget();
    }

    @Override
    public void onDestroy() {
        images = null;
    }

    @Override
    public int getCount() {
        if (images == null) return 0;
        return images.size();
    }

    /**
     * This method acts like the onBindViewHolder method in an Adapter
     *
     * @param position The current position of the item in the ListView to be displayed
     * @return The RemoteViews object to display for the provided postion
     */
    @Override
    public RemoteViews getViewAt(int position) {
        if (images == null || images.size() == 0) return null;
        Image image = images.get(position);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_pricefy_view_item);


        Bitmap bitmap = BitmapUtil.resampleWidgetPic(image.getUri());
        views.setImageViewBitmap(R.id.widgetImageView, bitmap);
        views.setTextViewText(R.id.widgetLabelsTv, image.getLabels());
        views.setTextViewText(R.id.widgetDateTv, image.toDateString());

        Bundle extras = new Bundle();
        extras.putLong(IMAGE_ID, image.getId());
        extras.putString(NetworkDataSource.PARAMS, image.getLabels());

        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);

        setFillIntentToViews(views, fillInIntent);
        return views;
    }

    private void setFillIntentToViews(RemoteViews views, Intent fillInIntent) {
        views.setOnClickFillInIntent(R.id.main_layout_view_item, fillInIntent);
        views.setOnClickFillInIntent(R.id.widgetImageView, fillInIntent);
        views.setOnClickFillInIntent(R.id.widgetLabelsTv, fillInIntent);
        views.setOnClickFillInIntent(R.id.widgetDateTv, fillInIntent);
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}

