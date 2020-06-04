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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MessageAdapter extends ArrayAdapter<MyMessage> implements View.OnClickListener{
    private ArrayList<MyMessage> messageArrayList;
    private String usrEmail;
    private String usrName;

    Context context;
    private DatabaseReference databaseMessages = FirebaseDatabase.getInstance().getReference("messages");

    private static class ViewHolder {
        TextView textMessageAuthor;
        TextView textMessageText;
        ImageView edit;
        ImageView delete;
    }

    public MessageAdapter(ArrayList<MyMessage> messageArrayList, String usrEmail, String usrName, Context context) {
        super(context, R.layout.row_item, messageArrayList);
        this.messageArrayList = messageArrayList;
        this.usrEmail = usrEmail;
        this.usrName = usrName;
        this.context = context;
    }

    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();
        Object object= getItem(position);
        MyMessage dataModel=(MyMessage) object;

        switch (v.getId())
        {
            case R.id.item_remove:
                databaseMessages.child(dataModel.getMsgID()).removeValue();
                Toast.makeText(context, "Usunięto", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        MyMessage dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item, parent, false);
            viewHolder.textMessageAuthor = (TextView) convertView.findViewById(R.id.name);
            viewHolder.textMessageText = (TextView) convertView.findViewById(R.id.day_hour);
            viewHolder.edit = (ImageView) convertView.findViewById(R.id.item_edit);
            viewHolder.edit.setVisibility(View.GONE);
            viewHolder.delete = (ImageView) convertView.findViewById(R.id.item_remove);
            if(!usrName.equals(dataModel.getMsgAuthor())) {
                viewHolder.delete.setVisibility(View.GONE);
            }

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }


        viewHolder.textMessageAuthor.setText(dataModel.getMsgAuthor());
        viewHolder.textMessageText.setText(dataModel.getMsgText());
        viewHolder.edit.setOnClickListener(this);
        viewHolder.edit.setTag(position);
        viewHolder.delete.setOnClickListener(this);
        viewHolder.delete.setTag(position);
        // Return the completed view to render on screen
        return convertView;
    }
}