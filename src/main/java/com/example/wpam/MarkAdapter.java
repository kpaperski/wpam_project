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

public class MarkAdapter extends ArrayAdapter<Mark> implements View.OnClickListener{
    private ArrayList<Mark> markArrayList;
    private String usrEmail;

    Context context;
    private DatabaseReference databaseMark = FirebaseDatabase.getInstance().getReference("marks");

    private static class ViewHolder {
        TextView textMarkName;
        TextView textMarkPerc;
        TextView textMarkText;
        ImageView edit;
        ImageView delete;
    }

    public MarkAdapter(ArrayList<Mark> markArrayList, String usrEmail, Context context) {
        super(context, R.layout.row_mark_item, markArrayList);
        this.markArrayList = markArrayList;
        this.usrEmail = usrEmail;
        this.context = context;
    }

    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();
        Object object= getItem(position);
        Mark dataModel=(Mark) object;

        switch (v.getId())
        {
            case R.id.item_edit_m:
                showUpdateDialog(dataModel.getMarkId(), dataModel.getMarkName(), dataModel.getMarkStudentEmail(), dataModel.getMarkPoints(), dataModel.getMarkMaxPoints(), dataModel.getMarkDetails());
                break;
            case R.id.item_remove_m:
                showDeleteDialog(dataModel.getMarkId());
                break;
        }
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Mark dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_mark_item, parent, false);
            viewHolder.textMarkName = (TextView) convertView.findViewById(R.id.mark_name);
            viewHolder.textMarkPerc = (TextView) convertView.findViewById(R.id.mark_perc);
            viewHolder.textMarkText = (TextView) convertView.findViewById(R.id.mark_text);
            viewHolder.edit = (ImageView) convertView.findViewById(R.id.item_edit_m);
            viewHolder.delete = (ImageView) convertView.findViewById(R.id.item_remove_m);
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


        viewHolder.textMarkName.setText(dataModel.getMarkName());
        viewHolder.textMarkPerc.setText("Liczba punktów: " + dataModel.getMarkPoints() + "/" + dataModel.getMarkMaxPoints() + " p.      Procent: " + dataModel.getMarkPercent());
        viewHolder.textMarkText.setText(dataModel.getMarkDetails());
        viewHolder.edit.setOnClickListener(this);
        viewHolder.edit.setTag(position);
        viewHolder.delete.setOnClickListener(this);
        viewHolder.delete.setTag(position);
        // Return the completed view to render on screen
        return convertView;
    }

    private void showUpdateDialog(final String marksID, String marksName, final String marksStudent, String marksPoints, String marksMaxPoints, String marksDetails) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);

        LayoutInflater inflater = LayoutInflater.from(getContext());

        final View dialogView = inflater.inflate(R.layout.fragment_edit_mark, null);

        dialogBuilder.setView(dialogView);

        final EditText addMarkName = (EditText) dialogView.findViewById(R.id.editMarks_input_name);
        addMarkName.setText(marksName);
        final EditText addMarkPoints = (EditText) dialogView.findViewById(R.id.editMarks_input_points);
        addMarkPoints.setText(marksPoints);
        final EditText addMarkMaxPoints = (EditText) dialogView.findViewById(R.id.editMarks_input_maxpoints);
        addMarkMaxPoints.setText(marksMaxPoints);
        final EditText addMarkDetails = (EditText) dialogView.findViewById(R.id.editMarks_input_details);
        addMarkDetails.setText(marksDetails);
        Button btnUpdate = (Button) dialogView.findViewById(R.id.btn_edit_marks);
        Button btnCancel = (Button) dialogView.findViewById(R.id.btn_cancel_marks);

        dialogBuilder.setTitle("Edytowanie oceny");

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = addMarkName.getText().toString().trim();
                String points = addMarkPoints.getText().toString().trim();
                String maxpoints = addMarkMaxPoints.getText().toString().trim();
                String details = addMarkDetails.getText().toString().trim();

                if (!(TextUtils.isEmpty(name) || TextUtils.isEmpty(points) || TextUtils.isEmpty(maxpoints) || TextUtils.isEmpty(details))) {
                    int perc;
                    perc = (Integer.parseInt(points)*100)/(Integer.parseInt(maxpoints));
                    String percent = Integer.toString(perc);
                    updateMark(marksID, name, marksStudent, points, maxpoints, percent, details);
                    alertDialog.dismiss();
                    Toast.makeText(getContext(), "Dodano ocenę", Toast.LENGTH_LONG).show();
                } else
                    Toast.makeText(getContext(), "Musisz podać wszystkie informacje odnośnie oceny!", Toast.LENGTH_LONG).show();

            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

    }

    private boolean updateMark(String id, String name, String studentEmail, String points, String maxpoints, String percent, String details){
        String time = TimeFunction.getTime();
        Mark mark = new Mark(id, name, studentEmail, points, maxpoints, percent, details, time);
        databaseMark.child(id).setValue(mark);
        return true;
    }

    private void showDeleteDialog(final String markID) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);

        LayoutInflater inflater = LayoutInflater.from(context);

        final View dialogView = inflater.inflate(R.layout.dialog_receipt, null);

        dialogBuilder.setView(dialogView);

        Button btnDelete = (Button) dialogView.findViewById(R.id.btn_delete);
        Button btnCancel = (Button) dialogView.findViewById(R.id.btn_cancel_);

        dialogBuilder.setTitle("Usuwanie oceny");

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseMark.child(markID).removeValue();
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
