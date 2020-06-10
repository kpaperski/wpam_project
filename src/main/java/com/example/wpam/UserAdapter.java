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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserAdapter extends ArrayAdapter<Users> implements View.OnClickListener{
    private ArrayList<Users> usersArrayList;
    private String usrEmail;

    Context context;
    DatabaseReference databaseUser = FirebaseDatabase.getInstance().getReference("users");
    private DatabaseReference databaseLesson = FirebaseDatabase.getInstance().getReference("lessons");
    private DatabaseReference databaseMessages = FirebaseDatabase.getInstance().getReference("messages");
    private DatabaseReference databaseHomeworks = FirebaseDatabase.getInstance().getReference("homeworks");
    private DatabaseReference databaseDrive = FirebaseDatabase.getInstance().getReference("studentfiles");
    private DatabaseReference databaseMarks = FirebaseDatabase.getInstance().getReference("marks");

    private static class ViewHolder {
        TextView textDate;
        TextView textName;
        TextView textEmailPhone;
        ImageView edit;
        ImageView delete;
    }

    public UserAdapter(ArrayList<Users> usersArrayList, String usrEmail, Context context) {
        super(context, R.layout.row_item, usersArrayList);
        this.usersArrayList = usersArrayList;
        this.usrEmail = usrEmail;
        this.context = context;
    }

    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();
        Object object= getItem(position);
        Users dataModel=(Users) object;

        switch (v.getId())
        {
            case R.id.item_edit:
                showUpdateDialog(dataModel.getUsrId(), dataModel.getUsrName(), dataModel.getUsrEmail(), dataModel.getUsrEmail(), dataModel.getUsrPhone());
                break;
            case R.id.item_remove:
                showDeleteDialog(dataModel);
                break;
        }
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Users dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item, parent, false);
            viewHolder.textDate = (TextView) convertView.findViewById(R.id.date);
            viewHolder.textDate.setVisibility(View.GONE);
            viewHolder.textName = (TextView) convertView.findViewById(R.id.name);
            viewHolder.textEmailPhone = (TextView) convertView.findViewById(R.id.day_hour);
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


        viewHolder.textName.setText(dataModel.getUsrName());
        viewHolder.textEmailPhone.setText("e-mail: " + dataModel.getUsrEmail() + "   tel: " + dataModel.getUsrPhone());
        viewHolder.edit.setOnClickListener(this);
        viewHolder.edit.setTag(position);
        viewHolder.delete.setOnClickListener(this);
        viewHolder.delete.setTag(position);
        // Return the completed view to render on screen
        return convertView;
    }

    private void showUpdateDialog(final String usrID, String usrName, final String usrRole, final String usrEmail_, String usrPhone) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);

        LayoutInflater inflater = LayoutInflater.from(context);

        final View dialogView = inflater.inflate(R.layout.fragment_edit_address, null);

        dialogBuilder.setView(dialogView);

        final EditText updateUsrName = (EditText) dialogView.findViewById(R.id.editAddress_input_name);
        updateUsrName.setText(usrName);
        final EditText updateUsrEmail = (EditText) dialogView.findViewById(R.id.editAddress_input_email);
        final EditText updateUsrPhone = (EditText) dialogView.findViewById(R.id.editAddress_input_phone);
        updateUsrEmail.setText(usrEmail_);
        updateUsrPhone.setText(usrPhone);
        Button btnUpdate = (Button) dialogView.findViewById(R.id.btn_edit);
        Button btnCancel = (Button) dialogView.findViewById(R.id.btn_cancel);
        btnCancel.setVisibility(View.GONE);

        dialogBuilder.setTitle("Edycja użytkownika");

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = updateUsrName.getText().toString().trim();
                String email = updateUsrEmail.getText().toString().trim();
                String phone = updateUsrPhone.getText().toString().trim();

                if (!(TextUtils.isEmpty(name) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(email))) {
                    updateUser(usrEmail_, usrID, name, usrRole, email, phone);
                    alertDialog.dismiss();
                    Toast.makeText(context, "Zedytowano pomyślnie!", Toast.LENGTH_LONG).show();
                } else
                    Toast.makeText(context, "Musisz podać wszystkie informacje odnośnie lekcji!", Toast.LENGTH_LONG).show();

            }
        });

    }

    private boolean updateUser(String lastEmail, String id, final String name, String role, final String email, String phone){
        Users user = new Users(id, name, role, email, phone);

        Query lsnQuery = databaseLesson.orderByChild("lsnStudentEmail").equalTo(lastEmail);
        lsnQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot lsnSnapshot: dataSnapshot.getChildren()) {
                    Lesson oldLesson = lsnSnapshot.getValue(Lesson.class);
                    Lesson newLesson = new Lesson(oldLesson.getLsnId(), oldLesson.getLsnName(), name, email, oldLesson.getLsnDay(), oldLesson.getLsnStartHour(), oldLesson.getLsnEndHour());
                    databaseLesson.child(oldLesson.getLsnId()).setValue(newLesson);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseUser.child(id).setValue(user);

        return true;
    }

    private void showDeleteDialog(final Users dataModel) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);

        LayoutInflater inflater = LayoutInflater.from(context);

        final View dialogView = inflater.inflate(R.layout.dialog_receipt, null);

        dialogBuilder.setView(dialogView);

        Button btnDelete = (Button) dialogView.findViewById(R.id.btn_delete);
        Button btnCancel = (Button) dialogView.findViewById(R.id.btn_cancel_);

        dialogBuilder.setTitle("Usuwanie użytkownika");

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteUser(dataModel);
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

    private void deleteUser(Users dataModel) {
        Query lsnQuery = databaseLesson.orderByChild("lsnStudentEmail").equalTo(dataModel.getUsrEmail());
        lsnQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot lsnSnapshot: dataSnapshot.getChildren()) {
                    Lesson oldLesson = lsnSnapshot.getValue(Lesson.class);
                    databaseLesson.child(oldLesson.getLsnId()).removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Query msgQuery = databaseMessages.orderByChild("msgStudentEmail").equalTo(dataModel.getUsrEmail());
        msgQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot msgSnapshot: dataSnapshot.getChildren()) {
                    MyMessage msg = msgSnapshot.getValue(MyMessage.class);
                    databaseMessages.child(msg.getMsgID()).removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Query hwQuery = databaseHomeworks.orderByChild("hwStudEmail").equalTo(dataModel.getUsrEmail());
        hwQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot hwSnapshot: dataSnapshot.getChildren()) {
                    Homework hw = hwSnapshot.getValue(Homework.class);
                    databaseHomeworks.child(hw.getHwID()).removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Query markQuery = databaseMarks.orderByChild("markStudentEmail").equalTo(dataModel.getUsrEmail());
        markQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot markSnapshot: dataSnapshot.getChildren()) {
                    Mark mark = markSnapshot.getValue(Mark.class);
                    databaseMarks.child(mark.getMarkId()).removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        databaseUser.child(dataModel.getUsrId()).removeValue();
    }
}
