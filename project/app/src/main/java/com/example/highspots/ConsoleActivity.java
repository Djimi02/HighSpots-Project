package com.example.highspots;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.highspots.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ConsoleActivity extends AppCompatActivity {

    /* Views */
    private EditText searchUserET;
    private TextView userIDTV;
    private TextView userEmailTV;
    private TextView userNicknameTV;
    private TextView userRoleTV;
    private TextView userNumVisitedSpotsTV;
    private TextView userNumFoundSpotsTV;
    private TextView userFoundSpotsTV;
    private Button userSearchBTN;


    /* Database */
    private final DatabaseReference usersDataReference = FirebaseDatabase.getInstance("https://highspots-project-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users");
    private final DatabaseReference spotsDataReference = FirebaseDatabase.getInstance("https://highspots-project-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Spots");
    private final StorageReference imageStorageReference = FirebaseStorage.getInstance().getReference().child("Images");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_console);
        this.getSupportActionBar().hide();

        initVars();
        initViews();
    }

    private void initVars() {

    }

    private void initViews() {
        this.searchUserET = findViewById(R.id.consoleSearchUserET);
        this.userIDTV = findViewById(R.id.consoleUserIDTV);
        this.userEmailTV = findViewById(R.id.consoleUserEmailTV);
        this.userNicknameTV = findViewById(R.id.consoleUserNicknameTV);
        this.userRoleTV = findViewById(R.id.consoleUserRoleTV);
        this.userNumVisitedSpotsTV = findViewById(R.id.consoleUserNumVisitedSpotsTV);
        this.userNumFoundSpotsTV = findViewById(R.id.consoleUserNumFoundSpotsTV);
        this.userFoundSpotsTV = findViewById(R.id.consoleUserFoundSpotsTV);

        this.userSearchBTN = findViewById(R.id.consoleUserSearchBTN);
        this.userSearchBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usersDataReference.child(searchUserET.getText().toString().trim()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }

                        User user = task.getResult().getValue(User.class);

                        if (user == null) {
                            Toast.makeText(ConsoleActivity.this, "User with this ID does not exists!", Toast.LENGTH_LONG).show();
                            return;
                        }

                        userIDTV.setText("User ID: " + user.getDbID());
                        userEmailTV.setText("User Email: " + user.getEmail());
                        userNicknameTV.setText("User Nickname: " + user.getNickName());
                        userRoleTV.setText("User role: " + user.getRole());
                        userNumVisitedSpotsTV.setText("User number of visited spots: " + user.getVisitedSpots().size());
                        userNumFoundSpotsTV.setText("User number of found spots: " + user.getFoundSpots().size());
                        userFoundSpotsTV.setText("User found spots' IDs: " + user.getFoundSpots().keySet().toString());
                    }
                });
            }
        });
    }
}