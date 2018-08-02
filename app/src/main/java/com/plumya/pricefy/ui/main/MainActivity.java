package com.plumya.pricefy.ui.main;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
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
import com.plumya.pricefy.data.local.model.Image;
import com.plumya.pricefy.data.network.NetworkDataSource;
import com.plumya.pricefy.di.Injector;
import com.plumya.pricefy.ui.results.ResultsActivity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements ImagesAdapter.ImageOnClickHandler {

    public static final String PHOTO_PATH_EXTRA = "photoPath";
    public static final String IMAGE_ID = "imageId";

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PROCESSING = 2;
    private static final int REQUEST_CAMERA_PERMISSION = 10;

    private String currentPhotoPath;
    private MainActivityViewModel viewModel;
    private ImagesAdapter imagesAdapter;

    @BindView(R.id.takeImageFab) FloatingActionButton takeImageFab;
    @BindView(R.id.emptyViewTv) TextView emptyViewTv;
    @BindView(R.id.imagesRv) RecyclerView imagesRv;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.coordinatorLayout) CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.app_name));

        initializeFab();
        initializeRecyclerView();
        showProgressBar();

        MainActivityViewModelFactory factory =
                Injector.provideMainActivityViewModelFactory(getApplicationContext());
        viewModel = ViewModelProviders.of(this, factory).get(MainActivityViewModel.class);
        viewModel.getCapturedImages().observe(this, new Observer<List<Image>>() {
            @Override
            public void onChanged(@Nullable List<Image> images) {
                Log.d(LOG_TAG, "Images changed: " + images);
                if (images != null && images.size() > 0) {
                    updateAdapter(images);
                    showRecyclerView();
                } else {
                    showEmptyView();
                }
            }
        });
    }

    private void initializeFab() {
        takeImageFab.setOnClickListener(v -> takePicture());
    }

    private void initializeRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        imagesRv.setLayoutManager(layoutManager);
        imagesRv.setHasFixedSize(true);
        imagesAdapter = new ImagesAdapter(this, new ArrayList<>(), this);
        imagesRv.setAdapter(imagesAdapter);
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(imagesRv.getContext(), layoutManager.getOrientation());
        imagesRv.addItemDecoration(dividerItemDecoration);
    }

    private void updateAdapter(List<Image> images) {
        imagesAdapter.update(images);
    }

    private void showRecyclerView() {
        imagesRv.setVisibility(View.VISIBLE);
        emptyViewTv.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }

    private void showProgressBar() {
        imagesRv.setVisibility(View.GONE);
        emptyViewTv.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void showEmptyView() {
        imagesRv.setVisibility(View.GONE);
        emptyViewTv.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    public void takePicture() {
        Log.d(LOG_TAG, "Start taking picture");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION
            );
        } else {
            launchCamera();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Intent processTakenImageIntent = new Intent(this, ProgressCircleActivity.class);
            processTakenImageIntent.putExtra(PHOTO_PATH_EXTRA, currentPhotoPath);
            startActivityForResult(processTakenImageIntent, REQUEST_IMAGE_PROCESSING);
        } else if (requestCode == REQUEST_IMAGE_PROCESSING) {
            if (resultCode != RESULT_OK) {
                Snackbar
                        .make(coordinatorLayout, R.string.could_not_process_image_error, Snackbar.LENGTH_LONG)
                        .show();
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION: {
                if (grantResults.length > 2
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    launchCamera();
                } else {
                    Snackbar
                            .make(coordinatorLayout, R.string.camera_permissions_error, Snackbar.LENGTH_LONG)
                            .show();
                }
            }
        }
    }

    private void launchCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
                currentPhotoPath = photoFile.getAbsolutePath();
            } catch (IOException ex) {
                Log.e(LOG_TAG, "Exception occurred while creating photo path: " + ex.getMessage());
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.plumya.pricefy.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            } else {
                Snackbar
                        .make(coordinatorLayout, R.string.could_not_take_pic_error, Snackbar.LENGTH_LONG)
                        .show();
            }
        } else {
            Snackbar
                    .make(coordinatorLayout, R.string.no_camera_app_error, Snackbar.LENGTH_LONG)
                    .show();
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onClick(Image image) {
        Intent intent = new Intent(this, ResultsActivity.class);
        intent.putExtra(IMAGE_ID, image.getId());
        intent.putExtra(NetworkDataSource.PARAMS, image.getLabels());
        startActivity(intent);
    }
}
