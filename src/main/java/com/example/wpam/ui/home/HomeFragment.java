package com.example.wpam.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;

import com.example.wpam.Consts;
import com.example.wpam.Lesson;
import com.example.wpam.LessonAdapter;
import com.example.wpam.R;
import com.example.wpam.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    private DatabaseReference databaseUser = FirebaseDatabase.getInstance().getReference("users");
    private DatabaseReference databaseLesson = FirebaseDatabase.getInstance().getReference("lessons");
    private ArrayList<Users> usersList;
    private Users teacherUser;
    private Users actualUser;
    private ArrayList<Lesson> lessonsList;
    private static LessonAdapter lessonAdapter;

    private TextView mHomeWelcomeTextView;
    private String usrName;
    private String usrEmail;

    private TextView textName;
    private TextView textEmail;
    private TextView textPhone;
    private Button btnEdit;
    private ListView listView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
  //      GoogleSignInClient client = GoogleSignIn.getClient(getActivity(), new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build());

        //        final TextView textView = root.findViewById(R.id.text_condition);
//        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        usersList = new ArrayList<>();
        lessonsList = new ArrayList<>();
        textName = (TextView) root.findViewById(R.id.text_home_addressName);
        textEmail = (TextView) root.findViewById(R.id.text_home_addressEmail);
        textPhone = (TextView) root.findViewById(R.id.text_home_addressPhone);
        btnEdit = (Button) root.findViewById(R.id.button_edit);
        listView = (ListView) root.findViewById(R.id.lesson_list);

        mHomeWelcomeTextView = (TextView)root.findViewById(R.id.text_home_welcome);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        usrName = firebaseAuth.getCurrentUser().getDisplayName();
        usrEmail = firebaseAuth.getCurrentUser().getEmail();
        mHomeWelcomeTextView.setText("Witaj " + usrName + "!");

        if (!usrEmail.equals(Consts.TEACHER_EMAIL)) {
            btnEdit.setVisibility(View.GONE);
        }
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

        Query teacherUserQuery = databaseUser.orderByChild("usrRole").equalTo("teacher");
        teacherUserQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot: dataSnapshot.getChildren()){
                    teacherUser = userSnapshot.getValue(Users.class);
                }
                textName.setText(teacherUser.getUsrName());
                textEmail.setText(teacherUser.getUsrEmail());
                textPhone.setText(teacherUser.getUsrPhone());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseLesson.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                lessonsList.clear();
                for(DataSnapshot lessonSnapshot : dataSnapshot.getChildren()) {
                    Lesson lesson = lessonSnapshot.getValue(Lesson.class);
                    if(usrEmail.equals(Consts.TEACHER_EMAIL) || usrEmail.equals(lesson.getLsnStudentEmail()))
                        lessonsList.add(lesson);
                    lessonAdapter = new LessonAdapter(lessonsList, usrEmail, getContext());
                    listView.setAdapter(lessonAdapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.button_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(HomeFragment.this)
                        .navigate(R.id.action_HomeFragment_to_EditAddress);
            }
        });
    }




}
