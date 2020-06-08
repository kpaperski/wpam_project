package com.example.wpam;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class HomeworkAdapter extends ArrayAdapter<Homework> implements View.OnClickListener{
    private ArrayList<Homework> hwArrayList;
    private String usrEmail;

    Context context;
    private DatabaseReference databaseHW = FirebaseDatabase.getInstance().getReference("homeworks");

    private static class ViewHolder {
        TextView textHWTitle;
        TextView textHWDescription;
        TextView textHWFile;
        ImageView edit;
        ImageView delete;
    }

    public HomeworkAdapter(ArrayList<Homework> hwArrayList, String usrEmail, Context context) {
        super(context, R.layout.row_mark_item, hwArrayList);
        this.hwArrayList = hwArrayList;
        this.usrEmail = usrEmail;
        this.context = context;
    }

    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();
        Object object= getItem(position);
        Homework dataModel=(Homework) object;

        switch (v.getId())
        {
            case R.id.mark_text:
                Toast.makeText(context, dataModel.getHwFileUrl(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setType(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(dataModel.getHwFileUrl()));
                context.startActivity(intent);
                break;
            case R.id.item_edit_m:
                showUpdateDialog(dataModel.getHwID(), dataModel.getHwTitle(), dataModel.getHwStudEmail(), dataModel.getHwDescription(), dataModel.getHwFileName(), dataModel.getHwFileUrl());
                break;
            case R.id.item_remove_m:
                showDeleteDialog(dataModel.getHwID());
                break;
        }
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Homework dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        HomeworkAdapter.ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new HomeworkAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_mark_item, parent, false);
            viewHolder.textHWTitle = (TextView) convertView.findViewById(R.id.mark_name);
            viewHolder.textHWDescription = (TextView) convertView.findViewById(R.id.mark_perc);
            viewHolder.textHWFile = (TextView) convertView.findViewById(R.id.mark_text);
            viewHolder.textHWFile.setTextColor(Color.BLUE);
            viewHolder.edit = (ImageView) convertView.findViewById(R.id.item_edit_m);
            viewHolder.delete = (ImageView) convertView.findViewById(R.id.item_remove_m);
            if(!usrEmail.equals(Consts.TEACHER_EMAIL)) {
                viewHolder.edit.setVisibility(View.GONE);
                viewHolder.delete.setVisibility(View.GONE);
            }

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (HomeworkAdapter.ViewHolder) convertView.getTag();
            result=convertView;
        }


        viewHolder.textHWTitle.setText(dataModel.getHwTitle());
        viewHolder.textHWDescription.setText(dataModel.getHwDescription());
        if(dataModel.getHwFileName() == null) {
            viewHolder.textHWFile.setVisibility(View.GONE);
        } else {
            viewHolder.textHWFile.setText(dataModel.getHwFileName() + ".pdf");
            viewHolder.textHWFile.setOnClickListener(this);
            viewHolder.textHWFile.setTag(position);
        }
        viewHolder.edit.setOnClickListener(this);
        viewHolder.edit.setTag(position);
        viewHolder.delete.setOnClickListener(this);
        viewHolder.delete.setTag(position);
        // Return the completed view to render on screen
        return convertView;
    }

    private void showUpdateDialog(final String hwID, String hwTitle, final String hwStudentEmail, String hwDescription, final String hwFileName, final String hwFileUrl) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);

        LayoutInflater inflater = LayoutInflater.from(getContext());

        final View dialogView = inflater.inflate(R.layout.fragment_edit_homework, null);

        dialogBuilder.setView(dialogView);

        final EditText addHWTitle = (EditText) dialogView.findViewById(R.id.editHomework_input_title);
        addHWTitle.setText(hwTitle);
        final EditText addHWDescription = (EditText) dialogView.findViewById(R.id.editHomework_input_description);
        addHWDescription.setText(hwDescription);
        final Spinner addHWFileName = (Spinner) dialogView.findViewById(R.id.editHomework_input_fileName);
        addHWFileName.setVisibility(View.GONE);
        Button btnUpload = (Button) dialogView.findViewById(R.id.btn_upload_file);
        btnUpload.setVisibility(View.GONE);
        Button btnUpdate = (Button) dialogView.findViewById(R.id.btn_add_homework);
        btnUpdate.setText("EDYTUJ");
        Button btnCancel = (Button) dialogView.findViewById(R.id.btn_cancel_homework);

        dialogBuilder.setTitle("Edytowanie pracy domowej");

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = addHWTitle.getText().toString().trim();
                String description = addHWDescription.getText().toString().trim();

                if (!(TextUtils.isEmpty(title))) {
                    updateHomework(hwID, title, hwStudentEmail, description, hwFileName, hwFileUrl);
                    alertDialog.dismiss();
                    Toast.makeText(getContext(), "Zedytowano pracę domową", Toast.LENGTH_LONG).show();
                } else
                    Toast.makeText(getContext(), "Musisz podać wszystkie tytuł pracy domowej!", Toast.LENGTH_LONG).show();

            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

    }

    private boolean updateHomework(String id, String title, String studentEmail, String description, String fileName, String fileUrl){
        String time = TimeFunction.getTime();
        Homework homework = new Homework(id, studentEmail, title, description, fileName, fileUrl, time);
        databaseHW.child(id).setValue(homework);
        return true;
    }

    private void showDeleteDialog(final String hwID) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);

        LayoutInflater inflater = LayoutInflater.from(context);

        final View dialogView = inflater.inflate(R.layout.dialog_receipt, null);

        dialogBuilder.setView(dialogView);

        Button btnDelete = (Button) dialogView.findViewById(R.id.btn_delete);
        Button btnCancel = (Button) dialogView.findViewById(R.id.btn_cancel_);

        dialogBuilder.setTitle("Usuwanie pracy domowej");

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseHW.child(hwID).removeValue();
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