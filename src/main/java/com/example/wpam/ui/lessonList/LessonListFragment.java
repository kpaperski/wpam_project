package com.example.wpam.ui.lessonList;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.wpam.Consts;
import com.example.wpam.Lesson;
import com.example.wpam.LessonAdapter;
import com.example.wpam.R;
import com.example.wpam.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class LessonListFragment extends Fragment {
    private DatabaseReference databaseLesson = FirebaseDatabase.getInstance().getReference("lessons");
    private ArrayList<Lesson> lessonsList;
    private ListView listView;
    private static LessonAdapter lessonAdapter;

    public static LessonListFragment newInstance() {
        return new LessonListFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_list, container, false);
        lessonsList = new ArrayList<>();
        listView = (ListView) root.findViewById(R.id.lesson_list);

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

        databaseLesson.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                lessonsList.clear();
                for(DataSnapshot lessonSnapshot : dataSnapshot.getChildren()) {
                    Lesson lesson = lessonSnapshot.getValue(Lesson.class);
                    lessonsList.add(lesson);
                    if (getActivity()!=null) {
                        lessonAdapter = new LessonAdapter(lessonsList, Consts.TEACHER_EMAIL, getContext());
                        listView.setAdapter(lessonAdapter);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
