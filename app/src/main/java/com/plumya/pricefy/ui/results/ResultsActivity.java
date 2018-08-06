package com.plumya.pricefy.ui.results;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.plumya.pricefy.R;
import com.plumya.pricefy.data.local.model.WebsiteItem;
import com.plumya.pricefy.data.network.NetworkDataSource;
import com.plumya.pricefy.data.network.model.WebsiteItemModel;
import com.plumya.pricefy.di.Injector;
import com.plumya.pricefy.ui.detail.ResultDetailActivity;
import com.plumya.pricefy.ui.main.MainActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ResultsActivity extends AppCompatActivity implements ResultsAdapter.WebsiteItemOnClickHandler {

    public static final String LOG_TAG = ResultsActivity.class.getSimpleName();
    public static final String ITEM_ID = "websiteItemId";
    public static final String ITEM_DETAILS_URI = "itemDetailsUri";

    private ResultsAdapter resultsAdapter;
    private ResultsActivityViewModel viewModel;

    @BindView(R.id.emptyViewTvResults) TextView emptyViewTv;
    @BindView(R.id.resultsRv) RecyclerView resultsRv;
    @BindView(R.id.progressBarResults) ProgressBar progressBar;
    @BindView(R.id.toolbarResults) Toolbar toolbar;
    @BindView(R.id.coordinatorLayoutResults) CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        ButterKnife.bind(this);

        initializeToolbar();
        initializeRecyclerView();
        showProgressBar(true);

        Intent intent = getIntent();
        long imageId = intent.getLongExtra(MainActivity.IMAGE_ID, -1);
        String params = intent.getStringExtra(NetworkDataSource.PARAMS);

        ResultsActivityViewModelFactory factory =
                Injector.provideResultsActivityViewModelFactory(getApplicationContext());
        viewModel = ViewModelProviders.of(this, factory).get(ResultsActivityViewModel.class);
        viewModel.setImageParameters(imageId, params);

        viewModel.getWebsiteItemModelErrors().observe(this, new Observer<WebsiteItemModel>() {
            @Override
            public void onChanged(@Nullable WebsiteItemModel websiteItemModel) {
                if (websiteItemModel.getResultStatus() == WebsiteItemModel.ResultStatus.REQUEST_NO_DATA_FOUND) {
                    showProgressBar(false);
                    showEmptyTextView();
                }
                if (websiteItemModel.getResultStatus() == WebsiteItemModel.ResultStatus.REQUEST_PARSING_ERROR) {
                    showProgressBar(false);
                    showEmptyTextView();
                    Snackbar
                            .make(
                                    coordinatorLayout,
                                    "Cannot find items. Take another picture and try again",
                                    Snackbar.LENGTH_LONG
                            ).show();
                }
            }
        });

        viewModel.getWebsiteItems().observe(this, new Observer<List<WebsiteItem>>() {
            @Override
            public void onChanged(@Nullable List<WebsiteItem> websiteItems) {
                Log.d(LOG_TAG, "Website items changed: " + websiteItems);
                if (websiteItems != null && websiteItems.size() > 0) {
                    showRecyclerView();
                    updateAdapter(websiteItems);
                } else {
                   showEmptyTextView();
                }
                showProgressBar(false);
            }
        });
    }

    private void initializeToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void updateAdapter(List<WebsiteItem> websiteItems) {
        resultsAdapter.update(websiteItems);
    }

    private void initializeRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        resultsRv.setLayoutManager(layoutManager);
        resultsRv.setDrawingCacheEnabled(true);
        resultsRv.setSaveEnabled(true);
        resultsRv.setHasFixedSize(true);
        resultsAdapter = new ResultsAdapter(this, new ArrayList<>(), this);
        resultsRv.setAdapter(resultsAdapter);
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(resultsRv.getContext(), layoutManager.getOrientation());
        resultsRv.addItemDecoration(dividerItemDecoration);
    }

    private void showEmptyTextView() {
        emptyViewTv.setVisibility(View.VISIBLE);
        resultsRv.setVisibility(View.GONE);
    }

    private void showRecyclerView() {
        emptyViewTv.setVisibility(View.GONE);
        resultsRv.setVisibility(View.VISIBLE);
    }

    private void showProgressBar(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onClick(WebsiteItem websiteItem) {
        Log.d(LOG_TAG, "Clicking item: " + websiteItem.getMainTitle());
        Intent intent = new Intent(this, ResultDetailActivity.class);
        intent.putExtra(ITEM_ID, websiteItem.getId());
        intent.putExtra(ITEM_DETAILS_URI, websiteItem.getDetailsUri());
        startActivity(intent);
    }
}
