package com.example.myapplication.services;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myapplication.models.Day;
import com.example.myapplication.models.Meal;
import com.example.myapplication.models.MyDate;
import com.example.myapplication.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;


/// a service to interact with the Firebase Realtime Database.
/// this class is a singleton, use getInstance() to get an instance of this class
/// @see #getInstance()
/// @see FirebaseDatabase
public class DatabaseService {

    /// tag for logging
    /// @see Log
    private static final String TAG = "DatabaseService";

    /// callback interface for database operations
    /// @param <T> the type of the object to return
    /// @see DatabaseCallback#onCompleted(Object)
    /// @see DatabaseCallback#onFailed(Exception)
    public interface DatabaseCallback<T> {
        /// called when the operation is completed successfully
        void onCompleted(T object);

        /// called when the operation fails with an exception
        void onFailed(Exception e);
    }

    /// the instance of this class
    /// @see #getInstance()
    private static DatabaseService instance;

    /// the reference to the database
    /// @see DatabaseReference
    /// @see FirebaseDatabase#getReference()
    private final DatabaseReference databaseReference;

    /// use getInstance() to get an instance of this class
    /// @see DatabaseService#getInstance()
    private DatabaseService() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    /// get an instance of this class
    /// @return an instance of this class
    /// @see DatabaseService
    public static DatabaseService getInstance() {
        if (instance == null) {
            instance = new DatabaseService();
        }
        return instance;
    }


    // private generic methods to write and read data from the database

    /// write data to the database at a specific path
    /// @param path the path to write the data to
    /// @param data the data to write (can be any object, but must be serializable, i.e. must have a default constructor and all fields must have getters and setters)
    /// @param callback the callback to call when the operation is completed
    /// @return void
    /// @see DatabaseCallback
    private void writeData(@NotNull final String path, @NotNull final Object data, final @NotNull DatabaseCallback<Void> callback) {
        databaseReference.child(path).setValue(data).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                callback.onCompleted(task.getResult());
            } else {
                callback.onFailed(task.getException());
            }
        });
    }

    /// read data from the database at a specific path
    /// @param path the path to read the data from
    /// @return a DatabaseReference object to read the data from
    /// @see DatabaseReference
    private DatabaseReference readData(@NotNull final String path) {
        return databaseReference.child(path);
    }


    /// get data from the database at a specific path
    /// @param path the path to get the data from
    /// @param clazz the class of the object to return
    /// @param callback the callback to call when the operation is completed
    /// @return void
    /// @see DatabaseCallback
    /// @see Class
    private <T> void getData(@NotNull final String path, @NotNull final Class<T> clazz, @NotNull final DatabaseCallback<T> callback) {
        readData(path).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e(TAG, "Error getting data", task.getException());
                callback.onFailed(task.getException());
                return;
            }
            T data = task.getResult().getValue(clazz);
            callback.onCompleted(data);
        });
    }

    /// get a list of data from the database at a specific path
    /// @param path the path to get the data from
    /// @param clazz the class of the objects to return
    /// @param callback the callback to call when the operation is completed
    private <T> void getDataList(@NotNull final String path, @NotNull final Class<T> clazz, @NotNull final DatabaseCallback<List<T>> callback) {
        readData(path).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e(TAG, "Error getting data", task.getException());
                callback.onFailed(task.getException());
                return;
            }
            List<T> tList = new ArrayList<>();
            task.getResult().getChildren().forEach(dataSnapshot -> {
                T t = dataSnapshot.getValue(clazz);
                tList.add(t);
            });

            callback.onCompleted(tList);
        });
    }

    /// generate a new id for a new object in the database
    /// @param path the path to generate the id for
    /// @return a new id for the object
    /// @see String
    /// @see DatabaseReference#push()

    private String generateNewId(@NotNull final String path) {
        return databaseReference.child(path).push().getKey();
    }


    // end of private methods for reading and writing data

    // public methods to interact with the database

    /// create a new user in the database
    /// @param user the user object to create
    /// @param callback the callback to call when the operation is completed
    ///              the callback will receive void
    ///            if the operation fails, the callback will receive an exception
    /// @return void
    /// @see DatabaseCallback
    /// @see User
    public String getCurrentUserId() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            return auth.getCurrentUser().getUid(); // Get the user ID from Firebase Authentication
        } else {
            return null; // Return null if no user is logged in
        }
    }


    public void createNewUser(@NotNull final User user, @NotNull final DatabaseCallback<Void> callback) {
        writeData("Users/" + user.getId(), user, callback);
    }

    public void createNewMeal(@NotNull final Meal meal,String uid, @NotNull final DatabaseCallback<Void> callback) {
        writeData("Users/" + uid+"/days", meal, callback);
    }

    /// get a user from the database
    /// @param uid the id of the user to get
    /// @param callback the callback to call when the operation is completed
    ///               the callback will receive the user object
    ///             if the operation fails, the callback will receive an exception
    /// @return void
    /// @see DatabaseCallback
    /// @see User
    public void getUser(@NotNull final String uid, @NotNull final DatabaseCallback<User> callback) {
        getData("Users/" + uid, User.class, callback);
    }

    /// get a meal from the database
    /// @param mealId the id of the meal to get
    /// @param callback the callback to call when the operation is completed
    ///                the callback will receive the meal object
    ///               if the operation fails, the callback will receive an exception
    /// @return void
    /// @see DatabaseCallback
    /// @see Meal
    public void getMeal(@NotNull final String mealId, @NotNull final DatabaseCallback<Meal> callback) {
        getData("meals/" + mealId, Meal.class, callback);
    }

    /// generate a new id for a new day in the database
    /// @return a new id for the day
    /// @see #generateNewId(String)
    /// @see Day
    public String generateDayId() {
        return generateNewId("days");
    }

    /// generate a new id for a new meal in the database
    /// @return a new id for the meal
    /// @see #generateNewId(String)
    /// @see Meal
    public String generateMealId() {
        return generateNewId("meals");
    }
    public void deleteMealFromFirebase(Meal meal, String userId, DatabaseCallback<Void> callback) {
        DatabaseReference userMealsRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("meals");

        userMealsRef.child(meal.getId()).removeValue()
                .addOnSuccessListener(aVoid -> callback.onCompleted(null))
                .addOnFailureListener(callback::onFailed);
    }


    public void searchDayByDate(MyDate myDate, String userId, @NotNull final DatabaseCallback<Day> callback) {

        getDataList("Users/" + userId + "/days", Day.class, new DatabaseCallback<List<Day>>() {
            @Override
            public void onCompleted(List<Day> days) {
                for (Day day : days) {
                    if (day.getDate().equals(myDate)) {
                        callback.onCompleted(day);
                        return;
                    }
                }
                callback.onCompleted(null);
            }

            @Override
            public void onFailed(Exception e) {
                callback.onFailed(e);
            }
        });
    }
    public void fetchAllDays(String userId, DatabaseCallback<HashMap<Long, Day>> callback) {
        databaseReference.child("Users").child(userId).child("days")
                .get()
                .addOnSuccessListener(snapshot -> {
                    HashMap<Long, Day> days = new HashMap<>();
                    for (DataSnapshot data : snapshot.getChildren()) {
                        Day day = data.getValue(Day.class);
                        if (day != null) {
                            long dateKey = day.getDate().asDate().getTime(); // Convert to timestamp
                            days.put(dateKey, day);
                        }
                    }
                    callback.onCompleted(days);
                })
                .addOnFailureListener(callback::onFailed);
    }


    /// get all the users from the database
    /// @param callback the callback to call when the operation is completed
    ///              the callback will receive a list of day objects
    ///            if the operation fails, the callback will receive an exception
    /// @return void
    /// @see DatabaseCallback
    /// @see List
    /// @see Day
    /// @see #getData(String, Class, DatabaseCallback)
    public void getUsers(@NotNull final DatabaseCallback<List<User>> callback) {
        getDataList("Users", User.class, callback);
    }



    /// create a new day in the database
    /// @param day the day object to create
    /// @param callback the callback to call when the operation is completed
    ///              the callback will receive void
    ///             if the operation fails, the callback will receive an exception
    /// @return void
    /// @see DatabaseCallback
    /// @see Day
    public void createNewDay(@NotNull final Day day,@NotNull final String userId, @NotNull final DatabaseCallback<Void> callback) {
        writeData("Users/" + userId+ "/days/" + day.getDayId(), day, callback);
    }
    public void updateDay(@NotNull final Day day,@NotNull final String userId, @NotNull final DatabaseCallback<Void> callback) {
        writeData("Users/" + userId+ "/days/" + day.getDayId(), day, callback);
    }
    public void getDayById(String userId, String dayId, DatabaseCallback<Day> callback) {
        getData("Users/"+ userId + "/days/"+ dayId, Day.class, callback);
    }

    public void fetchDays(String userId, final DatabaseCallback<List<Day>> callback) {
        getDataList("Users/"+ userId + "/days", Day.class, callback);
    }

    public void getAllDays(String userId, DatabaseCallback<List<Day>> callback) {
        // Reference to the user's "days" node in Firebase
        DatabaseReference userDaysRef = FirebaseDatabase.getInstance().getReference("Users")
                .child(userId)
                .child("days");

        // Listen for data at this reference
        userDaysRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Day> days = new ArrayList<>();

                // Iterate through the children of "days" (each child is a day)
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Map the snapshot to a Day object
                    Day day = snapshot.getValue(Day.class);

                    // Ensure day is not null before adding to the list
                    if (day != null) {
                        days.add(day);
                    }
                }

                // Sort the list of days by date (you can modify this logic if needed)
                Collections.sort(days, new Comparator<Day>() {
                    @Override
                    public int compare(Day day1, Day day2) {
                        return day1.getDate().compareTo(day2.getDate()); // Compare based on date
                    }
                });

                // Pass the list of days to the callback
                callback.onCompleted(days);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // If there is an error retrieving data, pass the exception to the callback
                callback.onFailed(databaseError.toException());
            }
        });
    }

}


