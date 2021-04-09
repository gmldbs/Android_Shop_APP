/*
 * Copyright © 2018-present, MNK Group. All rights reserved.
 */
package edu.skku.map.personalproject;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    //region GLOBALS

    private static final String TAG = "RegisterActivity";

    private static String PASSWORD_CONSTRAINT_MSG;
    private static String PHONE_NO_CONSTRAINT_MSG;


    private Button registerButton;
    private Button backButton;
    private TextView termsAndConditions;
    private CheckBox checkBox;
    private EditText id,
            Name,
            email,
            phone,
            address,
            address_detail,
            password,
            confirmPassword;
    private TextView registerTitle;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
        setUpViews();
    }

    public void setUpViews() {
        registerTitle.setText("Login");
        backButton.setOnClickListener(this);

        phone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    phone.setError(PHONE_NO_CONSTRAINT_MSG, null);
                }
            }
        });


        password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    password.setError(PASSWORD_CONSTRAINT_MSG, null);
                }
            }
        });

        SpannableString content = new SpannableString(getResources().getString(R.string.accept_terms_and_conditions_string));
        content.setSpan(new UnderlineSpan(), 13, content.length(), 0);
        termsAndConditions.setText(content);
        /*termsAndConditions.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    termsAndConditions.setTextColor(ContextCompat.getColor(RegisterActivity.this, R.color.colorPink));
                    startActivity(new Intent(RegisterActivity.this, LegalActivity.class));
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    //finger was lifted
                    termsAndConditions.setTextColor(ContextCompat.getColor(RegisterActivity.this, R.color.text_medium_grey));
                }
                return true;
            }
        });*/

        checkBox.setOnClickListener(this);
        registerButton.setOnClickListener(this);

    }

    //Method to initialise EditViews, TextViews, Buttons and CheckBox
    public void init() {
        registerTitle = (TextView) findViewById(R.id.simple_toolbar_title);
        backButton = (Button) findViewById(R.id.simple_toolbar_back_button);

        id = findViewById(R.id.register_id);
        Name = findViewById(R.id.register_name);
        email = (EditText) findViewById(R.id.register_email);
        phone = (EditText) findViewById(R.id.register_phone_number);
        address = (EditText) findViewById(R.id.register_address);
        address_detail = (EditText) findViewById(R.id.register_address_detail);

        password = (EditText) findViewById(R.id.register_password);
        confirmPassword = (EditText) findViewById(R.id.register_confirm_password);

        checkBox = (CheckBox) findViewById(R.id.register_terms_checkBox);
        termsAndConditions = (TextView) findViewById(R.id.terms_and_conditions_text);

        registerButton = (Button) findViewById(R.id.register_button);

        PHONE_NO_CONSTRAINT_MSG = "The Phone Number needs to be 11 digits long";
        PASSWORD_CONSTRAINT_MSG = getResources().getString(R.string.password_constraint_msg);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_button:
                if (canRegister()) {
                    final Map<String, Object> user = new HashMap<>();
                    final String id_= id.getText().toString();
                    user.put("id",id_);
                    user.put("Name",Name.getText().toString());
                    user.put("email",email.getText().toString());
                    user.put("phone",phone.getText().toString());
                    user.put("address",address.getText().toString());
                    user.put("address_detail",address_detail.getText().toString());
                    user.put("password",password.getText().toString());
                    db.collection("Users").whereEqualTo("id",id_).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()) {
                                if(task.getResult().size()==0){
                                    db.collection("Users").document(id.getText().toString()).set(user);
                                    Toast.makeText(RegisterActivity.this, "Sign Up success!", Toast.LENGTH_SHORT).show();
                                    Intent goLoginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                                    goLoginIntent.putExtra("id",id_);
                                    goLoginIntent.putExtra("pass",password.getText().toString());
                                    startActivity(goLoginIntent);
                                }
                                else {
                                    Toast.makeText(RegisterActivity.this, "User ID is already exist. please use another User ID!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                    /*db.collection("Users").document(id.getText().toString()).set(user);
                    Toast.makeText(RegisterActivity.this, "Sign Up success!", Toast.LENGTH_SHORT).show();
                    Intent createPostIntent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(createPostIntent);*/
                }
                break;
            case R.id.register_terms_checkBox:
                termsAndConditions.setTextColor(checkBox.isChecked() ? Color.BLACK : ContextCompat.getColor(RegisterActivity.this, R.color.colorPink));
                checkBox.setButtonDrawable(ContextCompat.getDrawable(RegisterActivity.this, R.drawable.register_checkbox_selector));
                break;
            case R.id.simple_toolbar_back_button:
                onBackPressed();
                break;
        }
    }

    private boolean canRegister() {
        if (Name.getText().toString().isEmpty() && id.getText().toString().isEmpty()&&
                email.getText().toString().isEmpty() && phone.getText().toString().isEmpty() &&
                address.getText().toString().isEmpty() && address_detail.getText().toString().isEmpty() &&
                password.getText().toString().isEmpty() && confirmPassword.getText().toString().isEmpty()) {
            Utils.showSingleButtonAlertWithoutTitle(this, getResources().getString(R.string.alert_all_fields_are_required));
            checkBox.setButtonDrawable(ContextCompat.getDrawable(this, R.drawable.checkbox_not_checked_selector));
            return false;
        }
        if (!checkBox.isChecked()) {
            checkBox.setButtonDrawable(ContextCompat.getDrawable(this, R.drawable.checkbox_not_checked_selector));
            Utils.showSingleButtonAlertWithoutTitle(RegisterActivity.this, getResources().getString(R.string.alert_terms_and_conditions));
            return false;
        } else {
            checkBox.setButtonDrawable(ContextCompat.getDrawable(this, R.drawable.register_checkbox_selector));
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
    @Override
    public void onBackPressed() {
        finish();
    }
}