package com.example.highspots;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.highspots.repositories.UserDataRepository;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SettingsActivity extends AppCompatActivity {

    /* Views */
    private Button logOutBTN;
    private EditText nickNameET;
    private Button saveNicknameET;
    private BottomNavigationView bottomNavigationView;
    private Button deleteAccountBTN;
    private Button changePassBTN;
    private Button changeEmailBTN;
    private Button contactFormBTN;

    /* Dialog */
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;

    /* Database */
    private UserDataRepository repository;
    private DatabaseReference usersDataReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        this.getSupportActionBar().hide();

        initVars(); // should be called before initViews()
        initViews();
    }

    private void initViews() {
        this.logOutBTN = findViewById(R.id.SettingsPageLogOutBTN);
        this.nickNameET = findViewById(R.id.SettingsPageNicknameET);
        this.saveNicknameET = findViewById(R.id.SettingsPageSaveNicknameBTN);
        this.bottomNavigationView = findViewById(R.id.nav_view_home_page);

        // Configure Bottom Nav View
        bottomNavigationView.setSelectedItemId(R.id.navigation_settings);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.navigation_home:
                        Intent intent1 = new Intent(getApplicationContext(), HomePageActivity.class);
                        intent1.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(intent1);
                        return true;
                    case R.id.navigation_settings:
                        return true;
                    case R.id.navigation_map:
                        startActivity(new Intent(SettingsActivity.this, MapsActivity.class));
                        return true;
                }

                return false;
            }
        });

        if (repository.getUser() != null) {
            nickNameET.setText(repository.getUser().getNickName());
        }

        logOutBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLoginPage();
                Toast.makeText(SettingsActivity.this, "LogOut was successful!", Toast.LENGTH_SHORT).show();
            }
        });

        saveNicknameET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateNickName();
                hideSoftKeyboard(SettingsActivity.this);
                Toast.makeText(SettingsActivity.this, "Nickname updated successfully!", Toast.LENGTH_SHORT).show();
            }
        });

        this.deleteAccountBTN = findViewById(R.id.settingsPageDeleteAccBTN);
        deleteAccountBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDeleteAccountDialog();
            }
        });

        this.changePassBTN = findViewById(R.id.SettingsPageChangePassBTN);
        changePassBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openChangePasswordDialog();
            }
        });

        this.changeEmailBTN = findViewById(R.id.SettingsPageChangeEmailBTN);
        changeEmailBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openChangeEmailDialog();
            }
        });

        this.contactFormBTN = findViewById(R.id.SettingsPageLogOutBTN);
        contactFormBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openContactFormDialog();
            }
        });
    }

    private void initVars() {
        this.repository = UserDataRepository.getInstance();
        this.usersDataReference = FirebaseDatabase.getInstance("https://highspots-project-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users");
    }

    private void updateNickName() {
        String newNickName = nickNameET.getText().toString().trim();

        if (newNickName.length() < 4) {
            nickNameET.setError("Nickname should be at least 4 characters long!");
            return;
        }

        repository.updateNickName(newNickName);
    }

    private void openDeleteAccountDialog() {
        // Build dialog
        dialogBuilder = new AlertDialog.Builder(this);
        final View popupView = getLayoutInflater().inflate(R.layout.delete_account_dialog, null);

        // Init views
        final EditText passwordET = popupView.findViewById(R.id.deleteAccDialogET);
        final Button deleteBTN = popupView.findViewById(R.id.deleteAccDialogBTN);
        deleteBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (repository.getUser() == null || FirebaseAuth.getInstance().getCurrentUser() == null) {
                    return;
                }

                String password = passwordET.getText().toString().trim();

                if (password.isEmpty()) {
                    passwordET.setError("Input your password!");
                    return;
                }

                String email = repository.getUser().getEmail();
                FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
                AuthCredential credential = EmailAuthProvider.getCredential(email, password);

                fUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {
                            passwordET.setError("Incorrect Password");
                            return;
                        }

                        fUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(SettingsActivity.this, "Something went wrong try again later!", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                repository.deleteUser();


                                dialog.dismiss();
                                goToLoginPage();
                                Toast.makeText(SettingsActivity.this, "Account was deleted successfully.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        });

        // Show dialog
        dialogBuilder.setView(popupView);
        dialog = dialogBuilder.create();
        dialog.show();
    }

    private void openChangePasswordDialog() {
        // Build dialog
        dialogBuilder = new AlertDialog.Builder(this);
        final View popupView = getLayoutInflater().inflate(R.layout.change_password_dialog, null);

        final EditText currentPasswordET = popupView.findViewById(R.id.changePassDialogCurrentPassET);
        final EditText newPasswordET = popupView.findViewById(R.id.changePassDialogNewPassET);
        final Button savePasswordBTN = popupView.findViewById(R.id.changePassDialogSaveBTN);
        savePasswordBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (repository.getUser() == null || FirebaseAuth.getInstance().getCurrentUser() == null) {
                    return;
                }

                String currentPassword = currentPasswordET.getText().toString().trim();
                String newPassword = newPasswordET.getText().toString().trim();

                if (currentPassword.isEmpty()) {
                    currentPasswordET.setError("Input your password!");
                    return;
                } else if (newPassword.length() < 6) {
                    newPasswordET.setError("The password should be at least 6 characters long!");
                    return;
                }

                FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
                AuthCredential credential = EmailAuthProvider.getCredential(repository.getUser().getEmail(), currentPassword);

                fUser.reauthenticate(credential).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        fUser.updatePassword(newPassword);
                        dialog.dismiss();
                        Toast.makeText(SettingsActivity.this, "Password has been changed successfully!", Toast.LENGTH_SHORT).show();
                    }
                }). addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            currentPasswordET.setError("Incorrect Password!");
                        } else if (e instanceof FirebaseNetworkException) {
                            Toast.makeText(SettingsActivity.this, "No internet connection!", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(SettingsActivity.this, "Something went wrong! Please try again later!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        // Show dialog
        dialogBuilder.setView(popupView);
        dialog = dialogBuilder.create();
        dialog.show();
    }

    private void openChangeEmailDialog() {
        // Build dialog
        dialogBuilder = new AlertDialog.Builder(this);
        final View popupView = getLayoutInflater().inflate(R.layout.change_email_dialog, null);

        final EditText currentPasswordET = popupView.findViewById(R.id.changeEmailDialogCurrentPassET);
        final EditText newEmailET = popupView.findViewById(R.id.changeEmailDialogNewEmailET);
        final Button savePasswordBTN = popupView.findViewById(R.id.changeEmailDialogSaveBTN);
        savePasswordBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (repository.getUser() == null || FirebaseAuth.getInstance().getCurrentUser() == null) {
                    return;
                }

                String currentPassword = currentPasswordET.getText().toString().trim();
                String newEmail = newEmailET.getText().toString().trim();

                if (currentPassword.isEmpty()) {
                    currentPasswordET.setError("Input your password!");
                    return;
                } else if (newEmail.isEmpty()) {
                    newEmailET.setError("Input your email!");
                    return;
                } else if (!Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                    newEmailET.setError("Input correct email!");
                    return;
                }

                FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
                AuthCredential credential = EmailAuthProvider.getCredential(repository.getUser().getEmail(), currentPassword);

                fUser.reauthenticate(credential).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        fUser.updateEmail(newEmail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                repository.getUser().setEmail(newEmail);
                                repository.updateUserInDB();
                                dialog.dismiss();
                                Toast.makeText(SettingsActivity.this, "Email has been updated successfully", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                if (e instanceof FirebaseAuthUserCollisionException){
                                    Toast.makeText(SettingsActivity.this, "The specified email is already taken!", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            currentPasswordET.setError("Incorrect Password!");
                        } else if (e instanceof FirebaseNetworkException) {
                            Toast.makeText(SettingsActivity.this, "No internet connection!", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(SettingsActivity.this, "Something went wrong! Please try again later!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        // Show dialog
        dialogBuilder.setView(popupView);
        dialog = dialogBuilder.create();
        dialog.show();
    }

    private void openContactFormDialog() {
        // Build dialog
        dialogBuilder = new AlertDialog.Builder(this);
        final View popupView = getLayoutInflater().inflate(R.layout.contact_form_dialog, null);

        // Initialize dialog views
        final EditText subjectET = popupView.findViewById(R.id.contactFormSubjectET);
        final EditText emailBodyET = popupView.findViewById(R.id.contactFormTextET);
        final Button submitFormBTN = popupView.findViewById(R.id.contactFormSubmitBTN);
        submitFormBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // Show dialog
        dialogBuilder.setView(popupView);
        dialog = dialogBuilder.create();
        dialog.show();
    }

    /** Redirects to login page and clears the back stack. */
    private void goToLoginPage() {
        FirebaseAuth.getInstance().signOut();

        Intent intent = new Intent(SettingsActivity.this, LogInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
        UserDataRepository.deleteCurrentUserData();
    }

    private void hideSoftKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.bottomNavigationView.setSelectedItemId(R.id.navigation_settings);
    }
}