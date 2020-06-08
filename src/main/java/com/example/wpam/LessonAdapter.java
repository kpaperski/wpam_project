package com.example.wpam;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LessonAdapter extends ArrayAdapter<Lesson> implements View.OnClickListener{
    private ArrayList<Lesson> lessonArrayList;
    private String usrEmail;

    Context context;
    DatabaseReference lessonDatabase = FirebaseDatabase.getInstance().getReference("lessons");
    DatabaseReference databaseUser = FirebaseDatabase.getInstance().getReference("users");

    ArrayList<Users> usersList = new ArrayList<>();
    ArrayList<String> studentsName = new ArrayList<>();

    private static class ViewHolder {
        TextView textName;
        TextView textDayHour;
        ImageView edit;
        ImageView delete;
    }

    public LessonAdapter(ArrayList<Lesson> lessonArrayList, String usrEmail, Context context) {
        super(context, R.layout.row_item, lessonArrayList);
        this.lessonArrayList = lessonArrayList;
        this.usrEmail = usrEmail;
        this.context = context;
    }

    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();
        Object object= getItem(position);
        Lesson dataModel=(Lesson) object;

        switch (v.getId())
        {
            case R.id.item_edit:
                showUpdateDialog(dataModel.getLsnId(), dataModel.getLsnName(), dataModel.getLsnStartHour(), dataModel.getLsnEndHour());
                break;
            case R.id.item_remove:
                showDeleteDialog(dataModel.getLsnId());
                break;
        }
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Lesson dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item, parent, false);
            viewHolder.textName = (TextView) convertView.findViewById(R.id.name);
            viewHolder.textDayHour = (TextView) convertView.findViewById(R.id.day_hour);
            viewHolder.edit = (ImageView) convertView.findViewById(R.id.item_edit);
            viewHolder.delete = (ImageView) convertView.findViewById(R.id.item_remove);
            if(!usrEmail.equals(Consts.TEACHER_EMAIL)) {
                viewHolder.edit.setVisibility(View.GONE);
                viewHolder.delete.setVisibility(View.GONE);
            }

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }


        viewHolder.textName.setText(dataModel.getLsnName() + " " + dataModel.getLsnStudentName());
        viewHolder.textDayHour.setText(dataModel.getLsnDay() + " " + dataModel.getLsnStartHour() + " - " + dataModel.getLsnEndHour());
        viewHolder.edit.setOnClickListener(this);
        viewHolder.edit.setTag(position);
        viewHolder.delete.setOnClickListener(this);
        viewHolder.delete.setTag(position);

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
                    if (!(user.getUsrRole().equals("teacher")))
                        studentsName.add(user.getUsrName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        // Return the completed view to render on screen
        return convertView;
    }

    private void showUpdateDialog(final String lsnID, String lsnName, String lsnStartHour, String lsnEndHour) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);

        LayoutInflater inflater = LayoutInflater.from(context);

        final View dialogView = inflater.inflate(R.layout.update_lesson_dialog, null);

        dialogBuilder.setView(dialogView);

        final EditText updateLsnName = (EditText) dialogView.findViewById(R.id.updateLessons_name);
        updateLsnName.setText(lsnName);
        final Spinner updateLsnStudent = (Spinner) dialogView.findViewById(R.id.updateLessons_Student);
        addStudentSpinnerItem(updateLsnStudent);
        final Spinner updateLsnDay = (Spinner) dialogView.findViewById(R.id.update_day);
        addDaySpinnerItem(updateLsnDay);
        final EditText updateHourStart = (EditText) dialogView.findViewById(R.id.updateLessons_StartHour);
        final EditText updateHourEnd = (EditText) dialogView.findViewById(R.id.updateLessons_endHour);
        updateHourStart.setText(lsnStartHour);
        updateHourEnd.setText(lsnEndHour);
        Button btnUpdate = (Button) dialogView.findViewById(R.id.btn_update_lessons);

        dialogBuilder.setTitle("Edycja lekcji");

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = updateLsnName.getText().toString().trim();
                String studentName = updateLsnStudent.getSelectedItem().toString();
                String studentEmail = null;
                for (Users user : usersList) {
                    if(studentName.equals(user.getUsrName())) {
                        studentEmail = user.getUsrEmail();
                        break;
                    }
                }
                String day = updateLsnDay.getSelectedItem().toString();
                String startHour = updateHourStart.getText().toString().trim();
                String endHour = updateHourEnd.getText().toString().trim();

                if (!(TextUtils.isEmpty(name) || TextUtils.isEmpty(studentEmail) || TextUtils.isEmpty(studentName) ||TextUtils.isEmpty(startHour) || TextUtils.isEmpty(endHour) || day.equals("Wybierz dzień..."))) {
                    updateLesson(lsnID, name, studentName, studentEmail, day, startHour, endHour);
                    alertDialog.dismiss();
                    Toast.makeText(context, "Zedytowano pomyślnie!", Toast.LENGTH_LONG).show();
                } else
                    Toast.makeText(context, "Musisz podać wszystkie informacje odnośnie lekcji!", Toast.LENGTH_LONG).show();

            }
        });

    }

    private boolean updateLesson(String id, String lsnName, String studName, String studEmail, String lsnDay, String lsnStartHour, String lsnEndHour){
        Lesson lesson = new Lesson(id, lsnName, studName, studEmail, lsnDay, lsnStartHour, lsnEndHour);

        lessonDatabase.child(id).setValue(lesson);

        return true;
    }

    private void addStudentSpinnerItem(Spinner spinner) {
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
        spinner.setAdapter(spinnerArrayAdapter);
    }

    private void addDaySpinnerItem(Spinner spinner) {
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
        spinner.setAdapter(spinnerArrayAdapter);
    }

    private void showDeleteDialog(final String lsnID) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);

        LayoutInflater inflater = LayoutInflater.from(context);

        final View dialogView = inflater.inflate(R.layout.dialog_receipt, null);

        dialogBuilder.setView(dialogView);

        Button btnDelete = (Button) dialogView.findViewById(R.id.btn_delete);
        Button btnCancel = (Button) dialogView.findViewById(R.id.btn_cancel_);

        dialogBuilder.setTitle("Usuwanie lekcji");

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lessonDatabase.child(lsnID).removeValue();
                Toast.makeText(context, "Usunięto", Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }
}
