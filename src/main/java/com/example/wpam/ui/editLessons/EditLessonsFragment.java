package com.example.wpam.ui.editLessons;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wpam.Lesson;
import com.example.wpam.R;
import com.example.wpam.Users;
import com.example.wpam.ui.editAddress.EditAddressFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class EditLessonsFragment extends Fragment {

    private EditText editName;
    private Spinner editStudent;
    private Spinner editDay;
    private EditText editHourStart;
    private EditText editHourEnd;

    private DatabaseReference databaseUser;
    private DatabaseReference databaseLessons;

    private List<Users> usersList;
    private List<String> studentsName;

    public static EditLessonsFragment newInstance() {
        return new EditLessonsFragment();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_edit_lessons, container, false);

        databaseLessons = FirebaseDatabase.getInstance().getReference("lessons");
        databaseUser = FirebaseDatabase.getInstance().getReference("users");

        usersList = new ArrayList<>();
        studentsName = new ArrayList<>();

        editName = (EditText) root.findViewById(R.id.editLessons_name);
        editStudent = (Spinner) root.findViewById(R.id.editLessons_student);
        editDay = (Spinner) root.findViewById(R.id.editLessons_day);
        editHourStart = (EditText) root.findViewById(R.id.editLessons_StartHour);
        editHourEnd = (EditText) root.findViewById(R.id.editLessons_endHour);

        addDaySpinnerItem();

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

        databaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersList.clear();
                studentsName.clear();
                studentsName.add("Wybierz ucznia...");
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    Users user = userSnapshot.getValue(Users.class);
                    usersList.add(user);
                }

                for (Users user : usersList) {
                    if (!user.getUsrRole().equals("teacher"))
                        studentsName.add(user.getUsrName());
                }

                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getContext(), R.layout.support_simple_spinner_dropdown_item, studentsName) {
                    @Override
                    public boolean isEnabled(int position) {
                        if (position == 0) {
                            // Disable the first item from Spinner
                            // First item will be use for hint
                            return false;
                        } else {
                            return true;
                        }
                    }

                    @Override
                    public View getDropDownView(int position, View convertView,
                                                ViewGroup parent) {
                        View view = super.getDropDownView(position, convertView, parent);
                        TextView tv = (TextView) view;
                        if (position == 0) {
                            // Set the hint text color gray
                            tv.setTextColor(Color.GRAY);
                        } else {
                            tv.setTextColor(Color.BLACK);
                        }
                        return view;
                    }
                };
                spinnerArrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                editStudent.setAdapter(spinnerArrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.btn_cancel_lessons).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(EditLessonsFragment.this)
                        .navigate(R.id.action_AddLesson_to_HomeFragment);
            }
        });

        view.findViewById(R.id.btn_add_lessons).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(addLesson()) {
                    NavHostFragment.findNavController(EditLessonsFragment.this)
                            .navigate(R.id.action_AddLesson_to_HomeFragment);
                }
            }
        });
    }

    private boolean addLesson() {
        boolean add_lesson = false;

        String name = editName.getText().toString().trim();
        String studentName = editStudent.getSelectedItem().toString();
        String studentEmail = null;
        for (Users user : usersList) {
            if(studentName.equals(user.getUsrName())) {
                studentEmail = user.getUsrEmail();
                break;
            }
        }
        String day = editDay.getSelectedItem().toString();
        String startHour = editHourStart.getText().toString().trim();
        String endHour = editHourEnd.getText().toString().trim();

        if (!(TextUtils.isEmpty(name) || TextUtils.isEmpty(studentEmail) || TextUtils.isEmpty(studentName) ||TextUtils.isEmpty(startHour) || TextUtils.isEmpty(endHour) || day.equals("Wybierz dzień..."))) {
            String id = databaseLessons.push().getKey();

            Lesson lesson = new Lesson(id, name, studentName, studentEmail, day, startHour, endHour);

            databaseLessons.child(id).setValue(lesson);

            Toast.makeText(getActivity(), "Dodano nową lekcję", Toast.LENGTH_SHORT).show();

            add_lesson = true;
        } else
            Toast.makeText(getActivity(), "Musisz podać wszystkie informacje odnośnie lekcji!", Toast.LENGTH_LONG).show();

        return add_lesson;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void addDaySpinnerItem() {
        String[] days = new String[]{
                "Wybierz dzień...",
                "Poniedziałek",
                "Wtorek",
                "Środa",
                "Czwartek",
                "Piątek",
                "Sobota",
                "Niedziela"
        };

        final List<String> daysList = new ArrayList<>(Arrays.asList(days));

        // Initializing an ArrayAdapter
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                getContext(),R.layout.support_simple_spinner_dropdown_item,daysList){
            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                    return false;
                else
                    return true;
            }
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0)
                    tv.setTextColor(Color.GRAY);
                else
                    tv.setTextColor(Color.BLACK);
                return view;
            }
        };
        spinnerArrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        editDay.setAdapter(spinnerArrayAdapter);
    }
}
