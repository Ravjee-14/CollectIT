package com.varsity.collectit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;


import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private EditText edUserName, edPassword;
    private Button btnLogin, btnRegister;
    private FirebaseAuth mAuth=FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Retrieve Objects
        FirebaseDatabase.getInstance().getReference().keepSynced(true);
        FirebaseUser userID = FirebaseAuth.getInstance().getCurrentUser();
        //Leenah (2017). Firebase: How to keep an Android user logged in? [online] Stack Overflow. Available at: https://stackoverflow.com/questions/22262463/firebase-how-to-keep-an-android-user-logged-in [Accessed 29 Jun. 2022].
        //User is has login access if user does not sign out
        if (userID != null)
        {
            // When user accesses app again
            //User will stay logged in
            Intent i = new Intent(MainActivity.this, ViewCategoriesActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }else{
            Toast.makeText(this, "Enter your Details", Toast.LENGTH_SHORT).show();
        }

        edUserName=findViewById(R.id.edUsername);
        edPassword=findViewById(R.id.edPassword);
        btnLogin=findViewById(R.id.btnLogin);
        btnRegister=findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Login();
            }

        });

    }
    public void Login(){
        //The IIE. (2022). Open Source Coding (Introduction) Module Manual. The Independent Institute of Education: Unpublished.
        String userEmail=edUserName.getText().toString();
        String userPassword=edPassword.getText().toString();
        if(userEmail.isEmpty() || userPassword.isEmpty()){
            Toast.makeText(MainActivity.this, "Email or" +
                    "Password cannot be blank", Toast.LENGTH_SHORT).show();
        }
        else{
            mAuth.signInWithEmailAndPassword(userEmail,userPassword).
                    addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                //Allow user to log into the app
                                SharedPreferences settings = getSharedPreferences("LoginToken", MODE_PRIVATE);
                                SharedPreferences.Editor prefEditor = settings.edit();
                                prefEditor.putString("idToken", task.getResult().getUser().getIdToken(false).getResult().getToken());
                                prefEditor.apply();
                                Toast.makeText(MainActivity.this, "Login Success",
                                        Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(MainActivity.this,
                                        ViewCategoriesActivity.class));
                            }
                            else{
                                //Login details are incorrect
                                Toast.makeText(MainActivity.this,
                                        "Login Failure" +
                                                task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}