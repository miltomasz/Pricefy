package com.plumya.pricefy.ui.results;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.plumya.pricefy.data.PricefyRepository;

/**
 * Created by miltomasz on 20/07/18.
 */

public class ResultsActivityViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final PricefyRepository repository;

    public ResultsActivityViewModelFactory(PricefyRepository repository) {
        this.repository = repository;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        //noinspection unchecked
        return (T) new ResultsActivityViewModel(repository);
    }
}
