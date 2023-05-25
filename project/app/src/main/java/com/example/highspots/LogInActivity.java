package com.example.highspots;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.highspots.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LogInActivity extends AppCompatActivity {

    /* Views */
    private EditText emailET;
    private EditText passwordET;
    private Button logInBTN;
    private TextView goToRegisterBTN;
    private ProgressBar progressBar;

    /* Vars */
    private long lastClickTime = 0;

    /* Database */
    private DatabaseReference usersDataReference = FirebaseDatabase.getInstance("https://highspots-project-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        this.getSupportActionBar().hide();

        autoLogIn();
    }

    private void autoLogIn() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            initViews();
            return;
        }

        usersDataReference.child(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    return;
                }

                User user = task.getResult().getValue(User.class);

                if (user != null) {
                    Toast.makeText(LogInActivity.this, "Login was successful!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LogInActivity.this, HomePageActivity.class));
                    finish();
                } else {
                    initViews();
                }
            }
        });

    }

    private void initViews() {
        this.emailET = findViewById(R.id.LogInPageEmailET);
        this.passwordET = findViewById(R.id.LogInPagePasswordET);
        this.logInBTN = findViewById(R.id.LogInPageLogInBTN);
        this.goToRegisterBTN = findViewById(R.id.LogInPageRegisterBTN);

        logInBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - lastClickTime < 1000){
                    return;
                }
                lastClickTime = SystemClock.elapsedRealtime();
                //Log.e(String.valueOf(LogInActivity.this), "LOGIN");
                logIn();
                hideSoftKeyboard(LogInActivity.this);
            }
        });

        goToRegisterBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LogInActivity.this, RegistrationActivity.class));
            }
        });

        this.progressBar = findViewById(R.id.logInPagePB);
        progressBar.setVisibility(View.GONE);
    }

    private boolean isDataValid() {
        boolean output = true;

        String email = emailET.getText().toString().trim();
        String password = passwordET.getText().toString().trim();

        if (email.isEmpty()) {
            emailET.setError("Input your email!");
            output = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailET.setError("Input correct email!");
            output = false;
        }

        if (password.isEmpty()) {
            passwordET.setError("Input your password!");
            output = false;
        } else if (password.length() < 6) {
            passwordET.setError("Password should be at least 6 characters long!");
            output = false;
        }

        return output;
    }

    private void logIn() {
        if (!isDataValid()) {
            return;
        }

        String email = emailET.getText().toString().trim();
        String password = passwordET.getText().toString().trim();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Toast.makeText(LogInActivity.this, "LogIn was successful!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LogInActivity.this, HomePageActivity.class));
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof FirebaseAuthInvalidUserException) {
                    Toast.makeText(LogInActivity.this, "There is no user with those credentials!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(LogInActivity.this, "Registration was not successful! Please try again later!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void hideSoftKeyboard(View view){
        InputMethodManager imm =(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void hideSoftKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}