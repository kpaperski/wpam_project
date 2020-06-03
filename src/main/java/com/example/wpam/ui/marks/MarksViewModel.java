package com.example.wpam.ui.marks;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.wpam.ui.slideshow.SlideshowViewModel;

public class MarksViewModel extends ViewModel {
    private MutableLiveData<String> mText;

    public MarksViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is marks fragment");
    }

    public LiveData<String> getText() {return mText;}
}
