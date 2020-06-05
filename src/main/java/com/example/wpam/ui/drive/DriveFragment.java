package com.example.wpam.ui.drive;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.wpam.Consts;
import com.example.wpam.R;
import com.example.wpam.UploadPDF;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

import java.util.ArrayList;
import java.util.Collections;

import static android.app.Activity.RESULT_OK;

public class DriveFragment extends Fragment {
    private String studentEmail;
    private String userEmail;
    private String fileName;

    private DatabaseReference databaseFiles = FirebaseDatabase.getInstance().getReference("studentfiles");
    private StorageReference storageHomework = FirebaseStorage.getInstance().getReference();

    private ListView listView;
    private ArrayList<UploadPDF> uploadPDFList;

    public static DriveFragment newInstance() {
        return new DriveFragment();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_notices, container, false);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        userEmail = firebaseAuth.getCurrentUser().getEmail();

        if (userEmail.equals(Consts.TEACHER_EMAIL)) {
            if (getArguments() != null) {
                DriveFragmentArgs args = DriveFragmentArgs.fromBundle(getArguments());
                studentEmail = args.getUserEmail();
            }
        } else {
            studentEmail = userEmail;
        }

        FloatingActionButton fab = root.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUploadFileDialog();
            }
        });

        listView = (ListView) root.findViewById(R.id.notice_list);
        uploadPDFList = new ArrayList<>();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UploadPDF uploadPDF = uploadPDFList.get(position);

                Intent intent = new Intent();
                intent.setType(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(uploadPDF.getPdfUrl()));
                startActivity(intent);
            }
        });
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

        databaseFiles.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                uploadPDFList.clear();
                for(DataSnapshot marksSnapshot : dataSnapshot.getChildren()) {
                    UploadPDF uploadPDF = marksSnapshot.getValue(UploadPDF.class);
                    if(studentEmail.equals(uploadPDF.getPdfOwnerEmail())) {
                        uploadPDFList.add(uploadPDF);
                    }
                    Collections.sort(uploadPDFList);
                    String[] uploads = new String[uploadPDFList.size()];
                    for (int i = 0; i < uploads.length; i++)
                        uploads[i] = uploadPDFList.get(i).getPdfName();
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.support_simple_spinner_dropdown_item, uploads);
                    listView.setAdapter(adapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
                    selectFile();
                    alertDialog.dismiss();
                } else
                    Toast.makeText(getContext(), "Musisz podać nazwę pliku!", Toast.LENGTH_LONG).show();

            }
        });
    }

    private void selectFile() {
        Intent intent = new Intent();
        intent.setType("*/*");
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

    private void uploadFile(Uri fileUri) {

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Ładowanie pliku...");
        progressDialog.show();

        StorageReference reference = storageHomework.child(studentEmail + "_files/" + System.currentTimeMillis());
        reference.putFile(fileUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uri.isComplete());
                        Uri url = uri.getResult();

                        UploadPDF uploadPDF = new UploadPDF(fileName, url.toString(), studentEmail);
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
}
