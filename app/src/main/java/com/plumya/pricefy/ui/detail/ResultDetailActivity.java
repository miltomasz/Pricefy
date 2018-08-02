package com.plumya.pricefy.ui.detail;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.plumya.pricefy.R;
import com.plumya.pricefy.data.local.model.WebsiteItem;
import com.plumya.pricefy.di.Injector;
import com.plumya.pricefy.ui.results.ResultsActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.xml.sax.XMLReader;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ResultDetailActivity extends AppCompatActivity {

    private static final String LOG_TAG = ResultDetailActivity.class.getSimpleName();
    private ResultDetailActivityViewModel viewModel;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.itemImage) ImageView imageView;
    @BindView(R.id.mainTitleTv) TextView mainTitleTv;
    @BindView(R.id.ratingBar) RatingBar ratingBar;
    @BindView(R.id.reviewsTv) TextView reviewsTv;
    @BindView(R.id.fitTv) TextView fitTv;
    @BindView(R.id.sizeTv) TextView sizeTv;
    @BindView(R.id.colorTv) TextView colorTv;
    @BindView(R.id.featuresList) TextView features;
    @BindView(R.id.featuresCardView) CardView featuresCardView;
    @BindView(R.id.app_bar) AppBarLayout appBarLayout;
    @BindView(R.id.toolbar_layout) CollapsingToolbarLayout toolbarLayout;
    @BindView(R.id.moreDataProgressBar) ProgressBar progressBar;
    @BindView(R.id.picassoProgressBar) ProgressBar picassoProgressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_detail);
        ButterKnife.bind(this);

        initializeToolbar();
        initializeAppBarLayout();

        Intent intent = getIntent();
        long itemId = intent.getLongExtra(ResultsActivity.ITEM_ID, -1);
        String websiteItemUri = intent.getStringExtra(ResultsActivity.ITEM_DETAILS_URI);

        ResultDetailActivityViewModelFactory factory =
                Injector.provideResultsDetailViewModelFactory(getApplicationContext());
        viewModel = ViewModelProviders.of(this, factory).get(ResultDetailActivityViewModel.class);
        viewModel.setWebsiteItemId(itemId, websiteItemUri);
        viewModel.getWebsiteItem().observe(this, new WebsiteItemObserver());
    }

    private void initializeToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void initializeAppBarLayout() {
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (Math.abs(verticalOffset) - appBarLayout.getTotalScrollRange() == 0) {
                    // Collapsed
                    toolbarLayout.setTitle(getResources().getString(R.string.app_name));
                } else {
                    //Expanded
                    toolbarLayout.setTitle("");
                }
            }
        });
    }

    private void showProgressBar(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private class UlTagHandler implements Html.TagHandler {
        @Override
        public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
            if (tag.equals("ul") && !opening) output.append("\n");
            if (tag.equals("li") && opening) output.append("\n\t • ");
        }
    }

    private class WebsiteItemObserver implements Observer<WebsiteItem> {
        @Override
        public void onChanged(@Nullable WebsiteItem websiteItem) {
            Log.d(LOG_TAG, "Website item changed: " + websiteItem);
            if (websiteItem != null) {
                Picasso.get()
                        .load(websiteItem.getImageUri())
                        .error(R.drawable.ic_launcher_background)
                        .into(imageView, new Callback() {
                            @Override
                            public void onSuccess() {
                                picassoProgressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError(Exception e) {
                            }
                        });
                mainTitleTv.setText(websiteItem.getMainTitle());
                ratingBar.setRating(websiteItem.getStars());
                if (websiteItem.isDetailsLoaded()) {
                    displayText(reviewsTv, "Reviews: ", websiteItem.getReviews());
                    displayText(fitTv, "Fit: ", websiteItem.getFit());
                    displayText(sizeTv, "Size: ", websiteItem.getSize());
                    displayText(colorTv, "Color: ", websiteItem.getColor());
                    if (!TextUtils.isEmpty(websiteItem.getFeatures())) {
                        featuresCardView.setVisibility(View.VISIBLE);
                        features.setText(Html.fromHtml(
                                websiteItem.getFeatures(), null, new UlTagHandler()
                        ));
                    } else {
                        featuresCardView.setVisibility(View.GONE);
                    }
                    showProgressBar(false);
                } else {
                    showProgressBar(true);
                }
            }
        }

        private void displayText(TextView tv, String preFix, String text) {
            if (!TextUtils.isEmpty(text)) {
                tv.setVisibility(View.VISIBLE);
                tv.setText(preFix + text);
            } else {
                tv.setVisibility(View.GONE);
            }
        }
    }
}
