package com.example.wpam.ui.userList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.wpam.Consts;
import com.example.wpam.R;
import com.example.wpam.UserAdapter;
import com.example.wpam.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserListFragment extends Fragment {
    private DatabaseReference databaseUser = FirebaseDatabase.getInstance().getReference("users");
    private ArrayList<Users> usersList;
    private ListView listView;
    private static UserAdapter userAdapter;

    public static UserListFragment newInstance() {
        return new UserListFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_list, container, false);
        usersList = new ArrayList<>();
        listView = (ListView) root.findViewById(R.id.lesson_list);

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
                    usersList.add(user);
                    if (getActivity()!=null) {
                        userAdapter = new UserAdapter(usersList, Consts.TEACHER_EMAIL, getContext());
                        listView.setAdapter(userAdapter);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
