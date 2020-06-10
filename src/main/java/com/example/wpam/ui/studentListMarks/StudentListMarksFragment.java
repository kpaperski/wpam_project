package com.example.wpam.ui.studentListMarks;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.wpam.Consts;
import com.example.wpam.R;
import com.example.wpam.Users;
import com.example.wpam.UsersMarksAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class StudentListMarksFragment extends Fragment {
    private DatabaseReference databaseUser = FirebaseDatabase.getInstance().getReference("users");
    private ArrayList<Users> usersList;
    private ListView listView;
    private static UsersMarksAdapter userAdapter;

    public static StudentListMarksFragment newInstance() {
        return new StudentListMarksFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_list, container, false);
        usersList = new ArrayList<>();
        listView = (ListView) root.findViewById(R.id.lesson_list);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Users user = usersList.get(position);
                String email = user.getUsrEmail();

                final NavController navController = Navigation.findNavController(view);
                StudentListMarksFragmentDirections.ActionStudentListToMarks action = StudentListMarksFragmentDirections.actionStudentListToMarks(email);
                action.setUserEmail(email);
                navController.navigate(action);

            }
        });

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

        databaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersList.clear();
                for(DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    Users user = userSnapshot.getValue(Users.class);
                    if (!user.getUsrRole().equals("teacher"))
                        usersList.add(user);
                }
                if (getActivity()!=null) {
                    userAdapter = new UsersMarksAdapter(usersList, Consts.TEACHER_EMAIL, getContext());
                    listView.setAdapter(userAdapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
