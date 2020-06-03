package com.example.wpam.ui.notices;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.wpam.Consts;
import com.example.wpam.Lesson;
import com.example.wpam.LessonAdapter;
import com.example.wpam.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class NoticesFragment extends Fragment {

    private String usrName;
    private String usrEmail;
    private DatabaseReference databaseNotices = FirebaseDatabase.getInstance().getReference("notices");
    private ArrayList<Notice> noticesList;
    private ListView listView;
    private static NoticeAdapter noticeAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_notices, container, false);

        FloatingActionButton fab = root.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        if (usrEmail.equals(Consts.TEACHER_EMAIL))
            fab.setVisibility(View.GONE);

        listView = (ListView) root.findViewById(R.id.notice_list);
        noticesList = new ArrayList<>();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        usrName = firebaseAuth.getCurrentUser().getDisplayName();
        usrEmail = firebaseAuth.getCurrentUser().getEmail();
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

        databaseNotices.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                noticesList.clear();
                for(DataSnapshot noticeSnapshot : dataSnapshot.getChildren()) {
                    Notice notice = noticeSnapshot.getValue(Lesson.class);
                    noticesList.add(notice);
                    noticeAdapter = new NoticeAdapter(noticesList, Consts.TEACHER_EMAIL, getContext());
                    listView.setAdapter(noticeAdapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
