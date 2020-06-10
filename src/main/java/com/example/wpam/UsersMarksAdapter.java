package com.example.wpam;

import android.app.AlertDialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

public class UsersMarksAdapter extends ArrayAdapter<Users>{
    private ArrayList<Users> usersArrayList;
    private String usrEmail;

    Context context;
    DatabaseReference databaseUser = FirebaseDatabase.getInstance().getReference("users");

    private static class ViewHolder {
        TextView textDate;
        TextView textName;
        TextView textEmail;
        ImageView edit;
        ImageView delete;
    }

    public UsersMarksAdapter(ArrayList<Users> usersArrayList, String usrEmail, Context context) {
        super(context, R.layout.row_item, usersArrayList);
        this.usersArrayList = usersArrayList;
        this.usrEmail = usrEmail;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Users dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        UsersMarksAdapter.ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new UsersMarksAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item, parent, false);
            viewHolder.textDate = (TextView) convertView.findViewById(R.id.date);
            viewHolder.textDate.setVisibility(View.GONE);
            viewHolder.textName = (TextView) convertView.findViewById(R.id.name);
            viewHolder.textEmail = (TextView) convertView.findViewById(R.id.day_hour);
            viewHolder.edit = (ImageView) convertView.findViewById(R.id.item_edit);
            viewHolder.delete = (ImageView) convertView.findViewById(R.id.item_remove);
            viewHolder.edit.setVisibility(View.GONE);
            viewHolder.delete.setVisibility(View.GONE);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (UsersMarksAdapter.ViewHolder) convertView.getTag();
            result=convertView;
        }


        viewHolder.textName.setText(dataModel.getUsrName());
        viewHolder.textEmail.setText("e-mail: " + dataModel.getUsrEmail());
        // Return the completed view to render on screen
        return convertView;
    }
}
