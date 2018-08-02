package com.plumya.pricefy.ui.detail;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.plumya.pricefy.data.PricefyRepository;

/**
 * Created by miltomasz on 20/07/18.
 */

public class ResultDetailActivityViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final PricefyRepository repository;

    public ResultDetailActivityViewModelFactory(PricefyRepository repository) {
        this.repository = repository;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        //noinspection unchecked
        return (T) new ResultDetailActivityViewModel(repository);
    }
}
