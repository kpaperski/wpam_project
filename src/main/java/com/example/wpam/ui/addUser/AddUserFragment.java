package com.example.wpam.ui.addUser;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.wpam.R;
import com.example.wpam.Users;
import com.example.wpam.ui.editAddress.EditAddressFragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddUserFragment extends Fragment {
    private EditText addTextName;
    private EditText addTextEmail;
    private EditText addTextPhone;
    private Button addBtn;
    private DatabaseReference databaseUser;

    public static AddUserFragment newInstance() { return new AddUserFragment(); }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_edit_address, container, false);

        databaseUser = FirebaseDatabase.getInstance().getReference("users");

        addTextName = (EditText) root.findViewById(R.id.editAddress_input_name);
        addTextEmail = (EditText) root.findViewById(R.id.editAddress_input_email);
        addTextPhone = (EditText) root.findViewById(R.id.editAddress_input_phone);
        addBtn = (Button) root.findViewById(R.id.btn_edit);

        addBtn.setText("DODAJ");
        return root;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(AddUserFragment.this)
                        .navigate(R.id.action_AddUser_to_HomeFragment);
            }
        });

        view.findViewById(R.id.btn_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(addUser()) {
                    NavHostFragment.findNavController(AddUserFragment.this)
                            .navigate(R.id.action_AddUser_to_HomeFragment);
                }
            }
        });
    }

    private boolean addUser(){
        Boolean add_user = false;
        String name = addTextName.getText().toString().trim();
        String email = addTextEmail.getText().toString().trim();
        String phone = addTextPhone.getText().toString().trim();

        if (!(TextUtils.isEmpty(name) || (TextUtils.isEmpty(email) || (TextUtils.isEmpty(phone))))) {
            String id = databaseUser.push().getKey();
            String role = "student";

            Users user = new Users(id, name, role, email, phone);

            databaseUser.child(id).setValue(user);

            Toast.makeText(getActivity(), "Dodano ucznia", Toast.LENGTH_LONG).show();

            add_user = true;

        } else {
            Toast.makeText(getActivity(), "Musisz podać imię, nazwisko, email i telefon!", Toast.LENGTH_LONG).show();
        }
        return add_user;
    }
}
