package com.example.highspots.repositories;

import androidx.annotation.NonNull;

import com.example.highspots.interfaces.UserDataListener;
import com.example.highspots.models.Spot;
import com.example.highspots.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
    private DatabaseReference spotsDataReference;

    private List<UserDataListener> listeners;

    private UserDataRepository() {
        this.spotsDataReference = FirebaseDatabase.getInstance("https://highspots-project-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Spots");
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
                    System.out.println("Ratings = " + user.getNumberOfDoneRatings());
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

    public void updateNickName(String newNickName) {
        usersDataReference.child(this.userID).child("nickName").setValue(newNickName);
    }

    public void updateUserInDB() {
        usersDataReference.child(this.userID).setValue(this.user);
    }

    public void visitSpot(Spot spot) {
        // Update user
        this.user.addVisitedSpot(spot.getDbID());
        this.user.addRatedSpot(spot.getDbID());
        updateUserInDB();

        // Update spot
        spot.addVisitor(this.user.getDbID());
        spotsDataReference.child(spot.getDbID()).setValue(spot);
    }

    public void rateSpot(Spot spot, double rating) {
        // Update spot
        spot.addNewRating(rating);
        spotsDataReference.child(spot.getDbID()).setValue(spot);

        // Update user
        this.user.addRatedSpot(spot.getDbID());
        updateUserInDB();
    }

    public void addListener(UserDataListener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(UserDataListener listener) {
        this.listeners.remove(listener);
    }

    public User getUser() {
        return this.user;
    }
}
