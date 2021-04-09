/*
 * Copyright © 2018-present, MNK Group. All rights reserved.
 */

package edu.skku.map.personalproject;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    //region GLOBALS
    private static final String TAG = "LoginActivity";

    private EditText UserIDEditText;
    private EditText passwordEditText;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
    }

    //  Initialization for text fields and buttons
    public void init() {
        UserIDEditText = (EditText) findViewById(R.id.UserIDText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        final Button loginButton = (Button) findViewById(R.id.login_button);
        TextView forgotPass = (TextView) findViewById(R.id.forgot_password);
        Button backButton = (Button) findViewById(R.id.simple_toolbar_back_button);


        String id = getIntent().getStringExtra("id");
        String pass = getIntent().getStringExtra("password");

        if (id != null && pass != null) {
            UserIDEditText.setText(id);
            passwordEditText.setText(pass);
        }
        loginButton.setOnClickListener(this);
        forgotPass.setOnClickListener(this);
        backButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_button:
                final UserData currentUser = (UserData) getApplication();
                db.collection("Users").whereEqualTo("id",UserIDEditText.getText().toString()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            if(task.getResult().size()==0) {
                                Toast.makeText(LoginActivity.this, "login fail!", Toast.LENGTH_SHORT).show();
                            }
                            for(QueryDocumentSnapshot document : task.getResult()) {
                                if(passwordEditText.getText().toString().equals(document.getData().get("password")))
                                {
                                    Toast.makeText(LoginActivity.this, "login success!", Toast.LENGTH_SHORT).show();
                                    currentUser.setData(document.getData());
                                    currentUser.setUser();
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                }
                                else
                                {
                                    Toast.makeText(LoginActivity.this, "wrong password!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                });
                break;

            case R.id.forgot_password:
                Toast.makeText(this, "forgot password", Toast.LENGTH_SHORT).show();
                break;

            case R.id.simple_toolbar_back_button:
                finish();
                break;
        }
    }


    @Override
    public void onBackPressed() {
        finish();
    }


    @Override
    protected void onResume() {
        super.onResume();
        ProductListsManager.getInstance().resetInstance();
    }
}
