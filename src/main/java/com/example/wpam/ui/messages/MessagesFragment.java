package com.example.wpam.ui.messages;

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
import com.example.wpam.MessageAdapter;
import com.example.wpam.MyMessage;
import com.example.wpam.R;
import com.example.wpam.TimeFunction;
import com.example.wpam.ui.marks.MarksFragment;
import com.example.wpam.ui.marks.MarksFragmentArgs;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

public class MessagesFragment extends Fragment {

    private String studentEmail;
    private String userName;
    private String userEmail;
    private DatabaseReference databaseMessages = FirebaseDatabase.getInstance().getReference("messages");
    private ArrayList<MyMessage> messagesList;
    private ListView listView;
    private static MessageAdapter messageAdapter;

    public static MessagesFragment newInstance() {
        return new MessagesFragment();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_notices, container, false);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        userEmail = firebaseAuth.getCurrentUser().getEmail();
        userName = firebaseAuth.getCurrentUser().getDisplayName();

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
        messagesList = new ArrayList<>();
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

        databaseMessages.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messagesList.clear();
                for(DataSnapshot messagesSnapshot : dataSnapshot.getChildren()) {
                    MyMessage myMessage = messagesSnapshot.getValue(MyMessage.class);
                    if(studentEmail.equals(myMessage.getMsgStudentEmail())) {
                        messagesList.add(myMessage);
                    }
                    Collections.sort(messagesList);
                    messageAdapter = new MessageAdapter(messagesList, userEmail, userName, getContext());
                    listView.setAdapter(messageAdapter);
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

        final View dialogView = inflater.inflate(R.layout.fragment_edit_messange, null);

        dialogBuilder.setView(dialogView);

        final EditText addMessageText = (EditText) dialogView.findViewById(R.id.editMessage_input_text);
        Button btnAdd = (Button) dialogView.findViewById(R.id.btn_edit_message);

        dialogBuilder.setTitle("Dodawanie posta");

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = addMessageText.getText().toString().trim();

                if (!(TextUtils.isEmpty(text))) {
                    addMessage(text);
                    alertDialog.dismiss();
                    Toast.makeText(getContext(), "Dodano post", Toast.LENGTH_LONG).show();
                } else
                    Toast.makeText(getContext(), "Musisz podać treść!", Toast.LENGTH_LONG).show();

            }
        });
    }

    private boolean addMessage(String text){
        String id = databaseMessages.push().getKey();
        String time = TimeFunction.getTime();

        MyMessage message = new MyMessage(id, studentEmail, userName, text, time);
        databaseMessages.child(id).setValue(message);
        return true;
    }
}
