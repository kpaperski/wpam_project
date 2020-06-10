package com.example.wpam.ui.addHomework;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.example.wpam.Homework;
import com.example.wpam.R;
import com.example.wpam.TimeFunction;
import com.example.wpam.UploadPDF;
import com.example.wpam.ui.editLessons.EditLessonsFragment;
import com.example.wpam.ui.homeworks.HomeworksFragmentArgs;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class AddHomeworkFragment extends Fragment {

    private String studentEmail;
    private EditText addHWTitle;
    private EditText addHWDescription;
    private Spinner addHWFileName;
    private String fileName;

    private DatabaseReference databaseHomework = FirebaseDatabase.getInstance().getReference("homeworks");
    private StorageReference storageHomework = FirebaseStorage.getInstance().getReference();
    private DatabaseReference databaseFiles = FirebaseDatabase.getInstance().getReference("homeworkfiles");

    private List<UploadPDF> uploadPDFList;
    private List<String> fileNameList;

    public static AddHomeworkFragment newInstance() { return new AddHomeworkFragment(); }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_edit_homework, container, false);

        fileNameList = new ArrayList<>();

        addHWTitle = (EditText) root.findViewById(R.id.editHomework_input_title);
        addHWFileName = (Spinner) root.findViewById(R.id.editHomework_input_fileName);
        addHWDescription = (EditText) root.findViewById(R.id.editHomework_input_description);

        if (getArguments() != null) {
            HomeworksFragmentArgs args = HomeworksFragmentArgs.fromBundle(getArguments());
            studentEmail = args.getUserEmail();
        }

        uploadPDFList = new ArrayList<>();
        fileNameList = new ArrayList<>();

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        databaseFiles.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                uploadPDFList.clear();
                fileNameList.clear();
                fileNameList.add("Wybierz plik...");
                for (DataSnapshot filesSnapshot : dataSnapshot.getChildren()) {
                    UploadPDF uploadPDF = filesSnapshot.getValue(UploadPDF.class);
                    uploadPDFList.add(uploadPDF);

                }
                Collections.sort(uploadPDFList);
                for (UploadPDF pdf : uploadPDFList) {
                    fileNameList.add(pdf.getPdfName());
                }
                if (getActivity()!=null) {
                    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getContext(), R.layout.support_simple_spinner_dropdown_item, fileNameList) {
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
                    addHWFileName.setAdapter(spinnerArrayAdapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.btn_cancel_homework).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final NavController navController = NavHostFragment.findNavController(AddHomeworkFragment.this);
                AddHomeworkFragmentDirections.ActionAddHomeworkToHomework action = AddHomeworkFragmentDirections.actionAddHomeworkToHomework(studentEmail);
                action.setUserEmail(studentEmail);
                navController.navigate(action);
            }
        });

        view.findViewById(R.id.btn_add_homework).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(addHomework()) {
                    final NavController navController = NavHostFragment.findNavController(AddHomeworkFragment.this);
                    AddHomeworkFragmentDirections.ActionAddHomeworkToHomework action = AddHomeworkFragmentDirections.actionAddHomeworkToHomework(studentEmail);
                    action.setUserEmail(studentEmail);
                    navController.navigate(action);
                }
            }
        });

        view.findViewById(R.id.btn_upload_file).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUploadFileDialog();
            }
        });
    }


    private void showUploadFileDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());

        LayoutInflater inflater = LayoutInflater.from(getContext());

        final View dialogView = inflater.inflate(R.layout.fragment_upload_files, null);

        dialogBuilder.setView(dialogView);

        final EditText addFileName = (EditText) dialogView.findViewById(R.id.uploadFile_input_name);
        Button btnAdd = (Button) dialogView.findViewById(R.id.btn_add_file);

        dialogBuilder.setTitle("Dodawanie pliku");

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileName = addFileName.getText().toString().trim();

                if (!(TextUtils.isEmpty(fileName))) {
                    selectPdf();
                    alertDialog.dismiss();
                } else
                    Toast.makeText(getContext(), "Musisz podać nazwę pliku!", Toast.LENGTH_LONG).show();

            }
        });
    }

    private void selectPdf() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Wybierz plik pdf"), 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uploadFile(data.getData());
        }
    }

    private void uploadFile(Uri pdfUri) {

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Ładowanie pliku...");
        progressDialog.show();

        StorageReference reference = storageHomework.child("uploads/" + System.currentTimeMillis() + ".pdf");
        reference.putFile(pdfUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uri.isComplete());
                        Uri url = uri.getResult();

                        UploadPDF uploadPDF = new UploadPDF(fileName, url.toString());
                        databaseFiles.child(databaseFiles.push().getKey()).setValue(uploadPDF);
                        Toast.makeText(getContext(), "Plik został przesłany", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                progressDialog.setMessage("Załadowano: " + (int)progress + "%");
            }
        });
    }


    private boolean addHomework() {
        boolean add_homework = false;

        String title = addHWTitle.getText().toString().trim();
        String description = addHWDescription.getText().toString().trim();
        String fileName = addHWFileName.getSelectedItem().toString();
        String fileUrl = null;
        for (UploadPDF pdf : uploadPDFList) {
            if(fileName.equals(pdf.getPdfName())) {
                fileUrl = pdf.getPdfUrl();
                break;
            }
        }

        if (!(TextUtils.isEmpty(title) || TextUtils.isEmpty(description))) {
            if (fileUrl == null)
                fileName = null;
            String id = databaseHomework.push().getKey();

            String time = TimeFunction.getTime();

            Homework homework = new Homework(id, studentEmail, title, description, fileName, fileUrl, time);

            databaseHomework.child(id).setValue(homework);

            Toast.makeText(getActivity(), "Dodano nową lekcję", Toast.LENGTH_SHORT).show();

            add_homework = true;
        } else
            Toast.makeText(getActivity(), "Musisz podać tytuł i opis pracy domowej!", Toast.LENGTH_LONG).show();

        return add_homework;
    }
}
