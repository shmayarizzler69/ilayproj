package com.example.myapplication.screens;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.Spinner;
import com.example.myapplication.R;
import com.example.myapplication.models.User;
import com.example.myapplication.services.AuthenticationService;
import com.example.myapplication.services.DatabaseService;
import com.example.myapplication.utils.SharedPreferencesUtil;

/// Activity for registering the user
/// This activity is used to register the user
/// It contains fields for the user to enter their information
/// It also contains a button to register the user
/// When the user is registered, they are redirected to the main activity
public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "RegisterActivity";

    EditText etFName, etLName, etPhone, etEmail, etPass;
    Button btnReg;

    String fName, lName, phone, email, pass;

    AuthenticationService authenticationService;
    DatabaseService databaseService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init_views();

        authenticationService = AuthenticationService.getInstance();
        databaseService = DatabaseService.getInstance();

    }

    private void init_views() {
        btnReg = findViewById(R.id.btnReg);
        etFName = findViewById(R.id.etFname);
        etLName = findViewById(R.id.etLname);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        etPass = findViewById(R.id.etPassword);

    }

    public void onClick(View v) {
        fName = etFName.getText().toString();
        lName = etLName.getText().toString();
        phone = etPhone.getText().toString();
        email = etEmail.getText().toString();
        pass = etPass.getText().toString();

        //check if registration is valid
        Boolean isValid = true;
        if (fName.length() < 2) {

            etFName.setError("שם פרטי קצר מדי");

            isValid = false;
        }
        if (lName.length() < 2) {
            Toast.makeText(RegisterActivity.this, "שם משפחה קצר מדי", Toast.LENGTH_LONG).show();
            isValid = false;
        }
        if (phone.length() < 9 || phone.length() > 10) {
            Toast.makeText(RegisterActivity.this, "מספר הטלפון לא תקין", Toast.LENGTH_LONG).show();
            isValid = false;
        }

        if (!email.contains("@")) {
            Toast.makeText(RegisterActivity.this, "כתובת האימייל לא תקינה", Toast.LENGTH_LONG).show();
            isValid = false;
        }
        if (pass.length() < 6) {
            Toast.makeText(RegisterActivity.this, "הסיסמה קצרה מדי", Toast.LENGTH_LONG).show();
            isValid = false;
        }
        if (pass.length() > 20) {
            Toast.makeText(RegisterActivity.this, "הסיסמה ארוכה מדי", Toast.LENGTH_LONG).show();
            isValid = false;
        }


        if(isValid) {
            authenticationService.signUp(email, pass, new AuthenticationService.AuthCallback<String>() {
                @Override
                public void onCompleted(String uid) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail A:success");
                    User newUser = new User(uid, fName, lName, phone, email, pass);
                    Log.w(TAG, newUser.toString());

                    databaseService.createNewUser(newUser, new DatabaseService.DatabaseCallback<Void>() {
                        @Override
                        public void onCompleted(Void object) {
                            Log.w(TAG, "createUserWithEmail DB:success");
                            SharedPreferencesUtil.saveUser(RegisterActivity.this, newUser);

                            Intent goLog = new Intent(RegisterActivity.this, MainActivity.class);
                            startActivity(goLog);
                        }

                        @Override
                        public void onFailed(Exception e) {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", e);
                            Toast.makeText(RegisterActivity.this, "Authentication failed. DB",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });

                }

                @Override
                public void onFailed(Exception e) {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", e);
                    Toast.makeText(RegisterActivity.this, "Authentication failed. A",
                            Toast.LENGTH_SHORT).show();
                }
            });


        }
    }

}
