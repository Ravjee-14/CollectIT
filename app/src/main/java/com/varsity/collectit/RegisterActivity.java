package com.varsity.collectit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private EditText edUserName, edPassword;
    private Button btnRegister, btnLogin;

    //create firebase instances
    FirebaseAuth mAuth= FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        edUserName=findViewById(R.id.edUsername);
        edPassword=findViewById(R.id.edPassword);
        btnRegister=findViewById(R.id.btnRegister);

        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //call register method
                register();
            }
        });


    }

    private void register() {
        //The IIE. (2022). Open Source Coding (Introduction) Module Manual. The Independent Institute of Education: Unpublished.
        String userEmail=edUserName.getText().toString();
        String userPassword=edPassword.getText().toString();
        //Check if it is empty
        if (userEmail.isEmpty() || userPassword.isEmpty()){
            Toast.makeText(RegisterActivity.this, "Email and Password cannot be blank",
                    Toast.LENGTH_SHORT).show();
        }else{
            mAuth.createUserWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener
                    (new OnCompleteListener<AuthResult>() {
                        //Create user
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(RegisterActivity.this, "Register Success",
                                        Toast.LENGTH_SHORT).show();
                                //startActivity(new Intent(RegisterActivity.this,MainActivity.class));

                            }else{
                                Toast.makeText(RegisterActivity.this, "Register Failure" +
                                        task.getException(), Toast.LENGTH_SHORT).show();


                            }
                        }
                    });
        }
    }
}