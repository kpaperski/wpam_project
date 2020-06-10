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

public class NoticeAdapter extends ArrayAdapter<Notice> implements View.OnClickListener{
    private ArrayList<Notice> noticeArrayList;
    private String usrEmail;

    Context context;
    private DatabaseReference databaseNotice = FirebaseDatabase.getInstance().getReference("notices");

    private static class ViewHolder {
        TextView textNoticeDate;
        TextView textNoticeName;
        TextView textNoticeText;
        ImageView edit;
        ImageView delete;
    }

    public NoticeAdapter(ArrayList<Notice> noticeArrayList, String usrEmail, Context context) {
        super(context, R.layout.row_item, noticeArrayList);
        this.noticeArrayList = noticeArrayList;
        this.usrEmail = usrEmail;
        this.context = context;
    }

    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();
        Object object= getItem(position);
        Notice dataModel=(Notice) object;

        switch (v.getId())
        {
            case R.id.item_edit:
                showUpdateDialog(dataModel.getNoticeID(), dataModel.getNoticeName(), dataModel.getNoticeText());
                break;
            case R.id.item_remove:
                showDeleteDialog(dataModel.getNoticeID());
                break;
        }
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Notice dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item, parent, false);
            viewHolder.textNoticeDate = (TextView) convertView.findViewById(R.id.date);
            viewHolder.textNoticeName = (TextView) convertView.findViewById(R.id.name);
            viewHolder.textNoticeText = (TextView) convertView.findViewById(R.id.day_hour);
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

        viewHolder.textNoticeDate.setText(dataModel.getNoticeDate());
        viewHolder.textNoticeName.setText(dataModel.getNoticeName());
        viewHolder.textNoticeText.setText(dataModel.getNoticeText());
        viewHolder.edit.setOnClickListener(this);
        viewHolder.edit.setTag(position);
        viewHolder.delete.setOnClickListener(this);
        viewHolder.delete.setTag(position);
        // Return the completed view to render on screen
        return convertView;
    }

    private void showUpdateDialog(final String noticeID, String noticeName, String noticeText) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);

        LayoutInflater inflater = LayoutInflater.from(context);

        final View dialogView = inflater.inflate(R.layout.fragment_edit_notice, null);

        dialogBuilder.setView(dialogView);

        final EditText updateNoticeName = (EditText) dialogView.findViewById(R.id.editNotice_input_name);
        updateNoticeName.setText(noticeName);
        final EditText updateNoticeText = (EditText) dialogView.findViewById(R.id.editNotice_input_text);
        updateNoticeText.setText(noticeText);
        Button btnUpdate = (Button) dialogView.findViewById(R.id.btn_edit_notice);
        Button btnCancel = (Button) dialogView.findViewById(R.id.btn_cancel_notice);

        dialogBuilder.setTitle("Edycja ogłoszenia");

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = updateNoticeName.getText().toString().trim();
                String text = updateNoticeText.getText().toString().trim();

                if (!(TextUtils.isEmpty(name) || TextUtils.isEmpty(text))) {
                    updateNotice(noticeID, name, text);
                    alertDialog.dismiss();
                    Toast.makeText(context, "Zedytowano pomyślnie!", Toast.LENGTH_LONG).show();
                } else
                    Toast.makeText(context, "Musisz podać wszystkie informacje odnośnie lekcji!", Toast.LENGTH_LONG).show();

            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

    }

    private boolean updateNotice(String id, String name, String text){
        Notice notice = new Notice(id, name, text);
        databaseNotice.child(id).setValue(notice);
        return true;
    }

    private void showDeleteDialog(final String noticeID) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);

        LayoutInflater inflater = LayoutInflater.from(context);

        final View dialogView = inflater.inflate(R.layout.dialog_receipt, null);

        dialogBuilder.setView(dialogView);

        Button btnDelete = (Button) dialogView.findViewById(R.id.btn_delete);
        Button btnCancel = (Button) dialogView.findViewById(R.id.btn_cancel_);

        dialogBuilder.setTitle("Usuwanie ogłoszenia");

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseNotice.child(noticeID).removeValue();
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
