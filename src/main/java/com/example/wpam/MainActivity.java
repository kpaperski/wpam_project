package com.example.wpam;

import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private String usrName;
    private String usrEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_notices, R.id.nav_homework, R.id.nav_marks, R.id.nav_marks_teacher, R.id.nav_editAddress)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        usrName = firebaseAuth.getCurrentUser().getDisplayName();
        usrEmail = firebaseAuth.getCurrentUser().getEmail();

        View headerView = navigationView.getHeaderView(0);
        TextView textUsrName = (TextView) headerView.findViewById(R.id.text_usr_name);
        TextView textUsrEmail = (TextView) headerView.findViewById(R.id.text_usr_email);

        textUsrName.setText(usrName);
        textUsrEmail.setText(usrEmail);

        if(usrEmail.equals(Consts.TEACHER_EMAIL)){
            menu.findItem(R.id.nav_marks).setVisible(false);
            menu.findItem(R.id.nav_messages).setVisible(false);
            menu.findItem(R.id.nav_homework).setVisible(false);
            menu.findItem(R.id.nav_drive).setVisible(false);
        } else {
            menu.findItem(R.id.nav_marks_teacher).setVisible(false);
            menu.findItem(R.id.nav_messages_teacher).setVisible(false);
            menu.findItem(R.id.nav_homework_teacher).setVisible(false);
            menu.findItem(R.id.nav_drive_teacher).setVisible(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        if(!usrEmail.equals(Consts.TEACHER_EMAIL)) {
            menu.findItem(R.id.action_new_student).setVisible(false);
            menu.findItem(R.id.action_new_lesson).setVisible(false);
            menu.findItem(R.id.action_list_lessons).setVisible(false);
            menu.findItem(R.id.action_list_users).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    public void click(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_new_student:
                NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
                navController.navigate(R.id.action_to_AddUser);
                break;
            case  R.id.action_list_users:
                NavController navController2 = Navigation.findNavController(this, R.id.nav_host_fragment);
                navController2.navigate(R.id.action_to_UserList);
                break;
            case  R.id.action_new_lesson:
                NavController navController1 = Navigation.findNavController(this, R.id.nav_host_fragment);
                navController1.navigate(R.id.action_to_AddLessons);
                break;
            case  R.id.action_list_lessons:
                NavController navController3 = Navigation.findNavController(this, R.id.nav_host_fragment);
                navController3.navigate(R.id.action_to_LessonList);
                break;
            case R.id.action_signout:
                signout();
                break;
        }
    }
    public void signout() {
        FirebaseAuth.getInstance().signOut();
        GoogleSignIn.getClient(this, new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build())
                .signOut()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        startActivity(new Intent(getApplicationContext(), AuthActivity.class));
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Błąd wylogowania", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
