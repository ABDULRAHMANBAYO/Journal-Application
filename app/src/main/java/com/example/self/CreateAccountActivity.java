package com.example.self;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.self.utils.JournalApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CreateAccountActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    //Firestore connection
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");

    private Button createAccountButton;
    private ProgressBar progressBar;
    private EditText usernameEditView, emailEditView, passwordEditView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();


        usernameEditView = findViewById(R.id.username_account);
        emailEditView = findViewById(R.id.email_account);
        passwordEditView = findViewById(R.id.password_account);
        createAccountButton = findViewById(R.id.create_account_button);
        progressBar = findViewById(R.id.createAccountProgress);

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser = firebaseAuth.getCurrentUser();

                if (currentUser != null) {
                    //user is already logged in
                } else {
                    //No user yet
                }

            }
        };


        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(emailEditView.getText().toString())
                        && !TextUtils.isEmpty(passwordEditView.getText().toString())
                        && !TextUtils.isEmpty(usernameEditView.getText().toString())) {

                    String username = usernameEditView.getText().toString().trim();
                    String email = emailEditView.getText().toString().trim();
                    String password = passwordEditView.getText().toString().trim();

                    createUserEmailAccount(email, password, username);
                } else {
                    Toast.makeText(getApplicationContext(), "Empty fields not allowed", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
    }

    private void createUserEmailAccount(String email, String password, final String username) {

        if (!TextUtils.isEmpty(email)
                && !TextUtils.isEmpty(password)
                && !TextUtils.isEmpty(username)) {

            progressBar.setVisibility(View.VISIBLE);
//
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, navigate to add journal
                                currentUser = firebaseAuth.getCurrentUser();
                                assert currentUser != null;
                                final String currentUserId = currentUser.getUid();
                                //Create a user in the user collection

                                Map<String, String> userObj = new HashMap<>();
                                userObj.put("userId", currentUserId);
                                userObj.put("username", username);

                                //save to firestore db
                                collectionReference.add(userObj)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if (Objects.requireNonNull(task.getResult().exists())) {
                                                            progressBar.setVisibility(View.INVISIBLE);

                                                            String name = task.getResult().getString("username");

                                                            JournalApi journalApi = JournalApi.getInstance();

                                                            journalApi.setUserId(currentUserId);
                                                            journalApi.setUsername(name);

                                                            Intent intent = new Intent(CreateAccountActivity.this, PostJournalActivity.class);
                                                            intent.putExtra("username", name);
                                                            intent.putExtra("userdId", currentUserId);
                                                            startActivity(intent);

                                                        } else {
                                                            progressBar.setVisibility(View.INVISIBLE);

                                                        }
                                                    }


                                                    {

                                                    }


                                                });

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressBar.setVisibility(View.INVISIBLE);
                                        Toast.makeText(getApplicationContext(), "Add users detail:failure: " + e.toString(), Toast.LENGTH_SHORT)
                                                .show();

                                    }
                                });

                                Toast.makeText(getApplicationContext(), "createUserWithEmail:success", Toast.LENGTH_SHORT)
                                        .show();
//                                    Log.d(TAG, );
//                                    FirebaseUser user = mAuth.getCurrentUser();
//                                    updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(getApplicationContext(), "createUserWithEmail:failure: " + task.getException(), Toast.LENGTH_SHORT)
                                        .show();
//                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());

//                                updateUI(null);
                            }

                            // ...
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(), "createUserWithEmail:failure: " + e.toString(), Toast.LENGTH_SHORT)
                            .show();

                }
            });
        } else {

        }


    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
//        updateUI(currentUser);
    }
}
