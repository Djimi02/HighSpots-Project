package com.example.highspots.repositories;

import androidx.annotation.NonNull;

import com.example.highspots.interfaces.UserDataListener;
import com.example.highspots.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserDataRepository {

    private static UserDataRepository instance;

    private String userID;
    private User user;

    private DatabaseReference usersDataReference;

    private List<UserDataListener> listeners;

    private UserDataRepository() {
        this.usersDataReference = FirebaseDatabase.getInstance("https://highspots-project-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users");
        this.listeners = new ArrayList<>();

        this.userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        usersDataReference.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user1 = snapshot.getValue(User.class);

                if (user1 != null) {
                    user = user1;
                    System.out.println("Name = " + user.getNickName());
                    notifyListenersOnUserDataChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static UserDataRepository getInstance() {
        if (instance == null) {
            instance = new UserDataRepository();
        }

        return instance;
    }

    public static void deleteCurrentUserData() {
        instance = null;
    }

    private void notifyListenersOnUserDataChanged() {
        for (UserDataListener listener : this.listeners) {
            listener.retrieveUserData();
        }
    }

    public void addListener(UserDataListener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(UserDataListener listener) {
        this.listeners.remove(listener);
    }
}
