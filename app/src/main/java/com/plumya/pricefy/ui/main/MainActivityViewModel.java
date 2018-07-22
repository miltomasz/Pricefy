package com.plumya.pricefy.ui.main;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.plumya.pricefy.data.PricefyRepository;
import com.plumya.pricefy.data.local.model.Image;

import java.util.List;

/**
 * Created by miltomasz on 20/07/18.
 */

class MainActivityViewModel extends ViewModel {

    private final PricefyRepository repository;
    private final LiveData<List<Image>> capturedImages;

    public MainActivityViewModel(PricefyRepository repository) {
        this.repository = repository;
        this.capturedImages = repository.getImages();
    }

    public LiveData<List<Image>> getCapturedImages() {
        return capturedImages;
    }
}