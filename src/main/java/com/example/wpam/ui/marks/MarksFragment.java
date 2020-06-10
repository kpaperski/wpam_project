package com.example.wpam.ui.marks;

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
import com.example.wpam.Mark;
import com.example.wpam.MarkAdapter;
import com.example.wpam.R;
import com.example.wpam.TimeFunction;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class MarksFragment extends Fragment {

    private String studentEmail;
    private String userEmail;
    private DatabaseReference databaseMarks = FirebaseDatabase.getInstance().getReference("marks");
    private ArrayList<Mark> marksList;
    private ListView listView;
    private static MarkAdapter markAdapter;

    public static MarksFragment newInstance() {
        return new MarksFragment();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_notices, container, false);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        userEmail = firebaseAuth.getCurrentUser().getEmail();

        if (userEmail.equals(Consts.TEACHER_EMAIL)) {
            if (getArguments() != null) {
                MarksFragmentArgs args = MarksFragmentArgs.fromBundle(getArguments());
                studentEmail = args.getUserEmail();
            }
        } else {
            studentEmail = userEmail;
        }

        FloatingActionButton fab = root.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddDialog();
            }
        });

        listView = (ListView) root.findViewById(R.id.notice_list);
        marksList = new ArrayList<>();
        if (!userEmail.equals(Consts.TEACHER_EMAIL))
            fab.setVisibility(View.GONE);
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

        databaseMarks.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                marksList.clear();
                for(DataSnapshot marksSnapshot : dataSnapshot.getChildren()) {
                    Mark mark = marksSnapshot.getValue(Mark.class);
                    if(studentEmail.equals(mark.getMarkStudentEmail())) {
                        marksList.add(mark);
                        Collections.sort(marksList);
                        if (getActivity()!=null) {
                            markAdapter = new MarkAdapter(marksList, userEmail, getContext());
                            listView.setAdapter(markAdapter);
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showAddDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());

        LayoutInflater inflater = LayoutInflater.from(getContext());

        final View dialogView = inflater.inflate(R.layout.fragment_edit_mark, null);

        dialogBuilder.setView(dialogView);

        final EditText addMarkName = (EditText) dialogView.findViewById(R.id.editMarks_input_name);
        final EditText addMarkPoints = (EditText) dialogView.findViewById(R.id.editMarks_input_points);
        final EditText addMarkMaxPoints = (EditText) dialogView.findViewById(R.id.editMarks_input_maxpoints);
        final EditText addMarkDetails = (EditText) dialogView.findViewById(R.id.editMarks_input_details);
        Button btnUpdate = (Button) dialogView.findViewById(R.id.btn_edit_marks);
        btnUpdate.setText("DODAJ");
        Button btnCancel = (Button) dialogView.findViewById(R.id.btn_cancel_marks);

        dialogBuilder.setTitle("Dodawanie oceny");

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = addMarkName.getText().toString().trim();
                String points = addMarkPoints.getText().toString().trim();
                String maxpoints = addMarkMaxPoints.getText().toString().trim();
                String details = addMarkDetails.getText().toString().trim();

                if (!(TextUtils.isEmpty(name) || TextUtils.isEmpty(points) || TextUtils.isEmpty(maxpoints) || TextUtils.isEmpty(details))) {
                    int perc;
                    perc = (Integer.parseInt(points)*100)/(Integer.parseInt(maxpoints));
                    String percent = Integer.toString(perc);
                    addMark(name, points, maxpoints, percent, details);
                    alertDialog.dismiss();
                    Toast.makeText(getContext(), "Dodano ocenę", Toast.LENGTH_LONG).show();
                } else
                    Toast.makeText(getContext(), "Musisz podać wszystkie informacje odnośnie oceny!", Toast.LENGTH_LONG).show();

            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

    }

    private boolean addMark(String name, String points, String maxpoints, String percent, String details){
        String id = databaseMarks.push().getKey();
        String time = TimeFunction.getTime();
        Mark mark = new Mark(id, name, studentEmail, points, maxpoints, percent, details, time);
        databaseMarks.child(id).setValue(mark);
        return true;
    }
}
