package com.example.wpam.ui.marks;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.wpam.R;

public class MarksFragment extends Fragment {

    private MarksViewModel marksViewModel;

    public static MarksFragment newInstance() {
        return new MarksFragment();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        marksViewModel =
                ViewModelProviders.of(this).get(MarksViewModel.class);
        View root = inflater.inflate(R.layout.fragment_marks, container, false);
        final TextView textView = root.findViewById(R.id.text_marks);
        marksViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }

}
