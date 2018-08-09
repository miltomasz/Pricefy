package com.plumya.pricefy.ui.detail;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
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
import com.plumya.pricefy.utils.LinkUtil;
import com.plumya.pricefy.utils.NetworkUtil;
import com.plumya.pricefy.utils.UIUtil;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.xml.sax.XMLReader;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ResultDetailActivity extends AppCompatActivity {

    private static final String LOG_TAG = ResultDetailActivity.class.getSimpleName();
    private static final String COM_AMAZON_M_SHOP_ANDROID_SHOPPING = "com.amazon.mShop.android.shopping";
    private static final String AMAZON_APP_PRODUCT_URI = "com.amazon.mobile.shopping://www.amazon.com/products/";

    private ResultDetailActivityViewModel viewModel;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.itemImage) ImageView imageView;
    @BindView(R.id.mainTitleTv) TextView mainTitleTv;
    @BindView(R.id.ratingBar) RatingBar ratingBar;
    @BindView(R.id.reviewsTv) TextView reviewsTv;
    @BindView(R.id.fitTv) TextView fitTv;
    @BindView(R.id.sizeTv) TextView sizeTv;
    @BindView(R.id.colorTv) TextView colorTv;
    @BindView(R.id.priceDetailTv) TextView priceDetailTv;
    @BindView(R.id.featuresList) TextView features;
    @BindView(R.id.featuresCardView) CardView featuresCardView;
    @BindView(R.id.app_bar) AppBarLayout appBarLayout;
    @BindView(R.id.toolbar_layout) CollapsingToolbarLayout toolbarLayout;
    @BindView(R.id.moreDataProgressBar) ProgressBar progressBar;
    @BindView(R.id.picassoProgressBar) ProgressBar picassoProgressBar;
    @BindView(R.id.resultDetailCoordinatorLayout) CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_detail);
        ButterKnife.bind(this);

        initializeToolbar();
        initializeAppBarLayout();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            imageView.setTransitionName(getImageTransitionName());
        }

        long itemId = getItemId();
        String websiteItemUri = getItemDetailsUri();

        ResultDetailActivityViewModelFactory factory =
                Injector.provideResultsDetailViewModelFactory(getApplicationContext());
        viewModel = ViewModelProviders.of(this, factory).get(ResultDetailActivityViewModel.class);
        viewModel.setWebsiteItemId(itemId, websiteItemUri);
        viewModel.getWebsiteItem().observe(this, new WebsiteItemObserver());
    }

    private String getItemDetailsUri() {
        Intent intent = getIntent();
        return intent.getStringExtra(ResultsActivity.ITEM_DETAILS_URI);
    }

    private long getItemId() {
        Intent intent = getIntent();
        return intent.getLongExtra(ResultsActivity.ITEM_ID, -1);
    }

    private String getImageTransitionName() {
        Intent intent = getIntent();
        return intent.getStringExtra(ResultsActivity.IMAGE_TRANSITION_NAME);
    }

    private void initializeToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void initializeAppBarLayout() {
        appBarLayout.addOnOffsetChangedListener(new AppBarLayoutOffsetListener());
    }

    private void showProgressBar(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    /**
     * 'Purchase' FAB OnClick handler. Checks if Amazon app is installed and runs it. Runs browser otherwise.
     * @param view
     */
    public void shopping(View view) {
        if (!NetworkUtil.isNetworkAvailable(this)) {
            showSnackbar(R.string.no_internet_connection);
            Intent intent = new Intent(Settings.ACTION_NETWORK_OPERATOR_SETTINGS);
            startActivity(intent);
            return;
        }
        Intent amazonIntent = getPackageManager().getLaunchIntentForPackage(COM_AMAZON_M_SHOP_ANDROID_SHOPPING);
        if (amazonIntent != null) {
            String itemDetailsUri = getItemDetailsUri();
            String[] itemPurchaseIdArray = itemDetailsUri.split("/dp/");
            if (itemPurchaseIdArray.length > 1) {
                String[] itemPurchaseExtractedArray = itemPurchaseIdArray[1].split("/");
                if (itemPurchaseExtractedArray.length > 0) {
                    String itemPurchaseId = itemPurchaseExtractedArray[0];
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(AMAZON_APP_PRODUCT_URI + itemPurchaseId +"/"));
                    startActivity(intent);
                } else {
                    toBrowser();
                }
            } else {
                toBrowser();
            }
        } else {
            toBrowser();
        }
    }

    private void toBrowser() {
        String itemDetailsUri = LinkUtil.prepareItemDetailsUrl(getItemDetailsUri());
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(itemDetailsUri));
        startActivity(browserIntent);
        if (browserIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(browserIntent);
        } else {
            showSnackbar(R.string.no_apps_found_msg);
        }
    }

    private void showSnackbar(int messageId) {
        Snackbar
                .make(coordinatorLayout, messageId, Snackbar.LENGTH_LONG)
                .show();
    }

    private class UlTagHandler implements Html.TagHandler {

        private static final String UL_TAG = "ul";
        private static final String LI_TAG = "li";

        @Override
        public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
            if (tag.equals(UL_TAG) && !opening) output.append("\n");
            if (tag.equals(LI_TAG) && opening) output.append("\n\t • ");
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
                                supportStartPostponedEnterTransition();
                            }

                            @Override
                            public void onError(Exception e) {
                                supportStartPostponedEnterTransition();
                            }
                        });
                mainTitleTv.setText(websiteItem.getMainTitle());
                ratingBar.setRating(websiteItem.getStars());
                priceDetailTv.setText(UIUtil.formatPrice(
                        String.valueOf(websiteItem.getPriceFrom()),
                        String.valueOf(websiteItem.getPriceTo()))
                );
                if (websiteItem.isDetailsLoaded()) {
                    displayText(reviewsTv, getString(R.string.details_reviews_prefix), websiteItem.getReviews());
                    displayText(fitTv, getString(R.string.details_fit_prefix), websiteItem.getFit());
                    displayText(sizeTv, getString(R.string.details_size_prefix), websiteItem.getSize());
                    displayText(colorTv, getString(R.string.details_color_prefix), websiteItem.getColor());
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

    private class AppBarLayoutOffsetListener implements AppBarLayout.OnOffsetChangedListener {
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
    }
}
