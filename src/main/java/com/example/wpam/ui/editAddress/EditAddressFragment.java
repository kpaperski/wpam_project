package com.example.wpam.ui.editAddress;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wpam.R;
import com.example.wpam.Users;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditAddressFragment extends Fragment {

    private EditAddressViewModel editAddressViewModel;

    private EditText editTextName;
    private EditText editTextEmail;
    private EditText editTextPhone;
    private DatabaseReference databaseUser;

    public static EditAddressFragment newInstance() {
        return new EditAddressFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        editAddressViewModel =
                ViewModelProviders.of(this).get(EditAddressViewModel.class);
        View root = inflater.inflate(R.layout.fragment_edit_address, container, false);

        databaseUser = FirebaseDatabase.getInstance().getReference("users");

        editTextName = (EditText) root.findViewById(R.id.editAddress_input_name);
        editTextEmail = (EditText) root.findViewById(R.id.editAddress_input_email);
        editTextPhone = (EditText) root.findViewById(R.id.editAddress_input_phone);

//        final TextView textView = root.findViewById(R.id.text_marks);
//        EditAddressViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(EditAddressFragment.this)
                        .navigate(R.id.action_EditAddress_to_HomeFragment);
            }
        });

        view.findViewById(R.id.btn_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editUser()) {
                    NavHostFragment.findNavController(EditAddressFragment.this)
                        .navigate(R.id.action_EditAddress_to_HomeFragment);
                }
            }
        });
    }

    private boolean editUser(){
        boolean ed_user = false;
        String name = editTextName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();

        if (!(TextUtils.isEmpty(name) || (TextUtils.isEmpty(email) || (TextUtils.isEmpty(phone))))) {
            String id = databaseUser.push().getKey();
            String role = "teacher";

            Users user = new Users(id, name, role, email, phone);

            databaseUser.child(id).setValue(user);

            Toast.makeText(getActivity(), "Edycja przebiegła pomyślnie", Toast.LENGTH_LONG).show();

            ed_user = true;

        } else {
            Toast.makeText(getActivity(), "Musisz podać imię, nazwisko, email i telefon!", Toast.LENGTH_LONG).show();
        }
        return ed_user;
    }
}
