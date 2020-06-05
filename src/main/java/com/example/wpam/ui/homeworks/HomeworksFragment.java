package com.example.wpam.ui.homeworks;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.wpam.Consts;
import com.example.wpam.Homework;
import com.example.wpam.HomeworkAdapter;
import com.example.wpam.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class HomeworksFragment extends Fragment {

    private String studentEmail;
    private String userEmail;
    private DatabaseReference databaseHomeworks = FirebaseDatabase.getInstance().getReference("homeworks");
    private ArrayList<Homework> homeworkList;
    private ListView listView;
    private static HomeworkAdapter markAdapter;

    public static HomeworksFragment newInstance() {
        return new HomeworksFragment();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_notices, container, false);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        userEmail = firebaseAuth.getCurrentUser().getEmail();

        if (userEmail.equals(Consts.TEACHER_EMAIL)) {
            if (getArguments() != null) {
                HomeworksFragmentArgs args = HomeworksFragmentArgs.fromBundle(getArguments());
                studentEmail = args.getUserEmail();
            }
        } else {
            studentEmail = userEmail;
        }

        FloatingActionButton fab = root.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final NavController navController = Navigation.findNavController(view);
                HomeworksFragmentDirections.ActionHomeworkToAddHomework action = HomeworksFragmentDirections.actionHomeworkToAddHomework(studentEmail);
                action.setUserEmail(studentEmail);
                navController.navigate(action);
            }
        });

        listView = (ListView) root.findViewById(R.id.notice_list);
        homeworkList = new ArrayList<>();
        if (!userEmail.equals(Consts.TEACHER_EMAIL))
            fab.setVisibility(View.GONE);
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

        databaseHomeworks.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                homeworkList.clear();
                for(DataSnapshot marksSnapshot : dataSnapshot.getChildren()) {
                    Homework homework = marksSnapshot.getValue(Homework.class);
                    if(studentEmail.equals(homework.getHwStudEmail())) {
                        homeworkList.add(homework);
                        Collections.sort(homeworkList);
                        markAdapter = new HomeworkAdapter(homeworkList, userEmail, getContext());
                        listView.setAdapter(markAdapter);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
