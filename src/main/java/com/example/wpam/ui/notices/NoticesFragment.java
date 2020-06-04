package com.example.wpam.ui.notices;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.wpam.Consts;
import com.example.wpam.Lesson;
import com.example.wpam.LessonAdapter;
import com.example.wpam.Notice;
import com.example.wpam.NoticeAdapter;
import com.example.wpam.R;
import com.example.wpam.TimeFunction;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

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
                showAddDialog();
            }
        });

        listView = (ListView) root.findViewById(R.id.notice_list);
        noticesList = new ArrayList<>();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        usrName = firebaseAuth.getCurrentUser().getDisplayName();
        usrEmail = firebaseAuth.getCurrentUser().getEmail();

        if (!usrEmail.equals(Consts.TEACHER_EMAIL))
            fab.setVisibility(View.GONE);
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
                    Notice notice = noticeSnapshot.getValue(Notice.class);
                    noticesList.add(notice);
                }
                Collections.sort(noticesList);
                noticeAdapter = new NoticeAdapter(noticesList, usrEmail, getContext());
                listView.setAdapter(noticeAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showAddDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());

        LayoutInflater inflater = LayoutInflater.from(getContext());

        final View dialogView = inflater.inflate(R.layout.fragment_edit_notice, null);

        dialogBuilder.setView(dialogView);

        final EditText addNoticeName = (EditText) dialogView.findViewById(R.id.editNotice_input_name);
        final EditText addNoticeText = (EditText) dialogView.findViewById(R.id.editNotice_input_text);
        Button btnUpdate = (Button) dialogView.findViewById(R.id.btn_edit_notice);
        Button btnCancel = (Button) dialogView.findViewById(R.id.btn_cancel_notice);

        dialogBuilder.setTitle("Dodawanie ogłoszenia");

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = addNoticeName.getText().toString().trim();
                String text = addNoticeText.getText().toString().trim();

                if (!(TextUtils.isEmpty(name) || TextUtils.isEmpty(text))) {
                    addNotice(name, text);
                    alertDialog.dismiss();
                    Toast.makeText(getContext(), "Dodano ogłoszenie", Toast.LENGTH_LONG).show();
                } else
                    Toast.makeText(getContext(), "Musisz podać wszystkie informacje odnośnie ogłoszenia!", Toast.LENGTH_LONG).show();

            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

    }

    private boolean addNotice(String name, String text){
        String id = databaseNotices.push().getKey();
        String time = TimeFunction.getTime();
        Notice notice = new Notice(id, name, text, time);
        databaseNotices.child(id).setValue(notice);
        return true;
    }
}
