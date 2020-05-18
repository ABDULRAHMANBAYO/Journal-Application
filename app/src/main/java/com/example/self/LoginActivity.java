package com.example.self;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import utils.JournalApi;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private Button loginButton, createAccountButtonLogin;
    private AutoCompleteTextView emailAddress;
    private EditText password;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
        loginButton = findViewById(R.id.email_sign_in_button);
        createAccountButtonLogin = findViewById(R.id.create_account_button_login);
        emailAddress = findViewById(R.id.email);
        password = findViewById(R.id.password);
        loginButton.setOnClickListener(this);
        createAccountButtonLogin.setOnClickListener(this);
        progressBar = findViewById(R.id.loginProgress);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.email_sign_in_button:
                login(emailAddress.getText().toString().trim(), password.getText().toString().trim());
                break;
            case R.id.create_account_button_login:
                startActivity(new Intent(LoginActivity.this, CreateAccountActivity.class));
                break;
        }

    }

    private void login(String email, String password) {
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            progressBar.setVisibility(View.VISIBLE);
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            assert user != null;
                            final String currentUserId = user.getUid();

                            collectionReference
                                    .whereEqualTo("userId", currentUserId)
                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                            if (e != null) {

                                            }
                                            assert queryDocumentSnapshots != null;
                                            if (!queryDocumentSnapshots.isEmpty()) {
                                                for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                                                    JournalApi journalApi = JournalApi.getInstance();
                                                    journalApi.setUsername(snapshot.getString("username"));
                                                    journalApi.setUserId(snapshot.getString("userId"));

                                                    //Go to list activity

                                                    startActivity(new Intent(LoginActivity.this, PostJournalActivity.class));
//                                                    finish();
                                                }


                                            }
                                        }
                                    });


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(), "Login Failed: " + e.toString(), Toast.LENGTH_SHORT)
                            .show();
                }
            });

        } else {
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(getApplicationContext(), "Please enter valid email and password", Toast.LENGTH_SHORT)
                    .show();
        }
    }
}
