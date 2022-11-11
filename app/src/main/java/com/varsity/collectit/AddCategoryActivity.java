package com.varsity.collectit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddCategoryActivity extends AppCompatActivity {

    private Button btnAddCategory, btnBack;
    private EditText etCategoryName;
    private EditText etItemGoal;
    private CategoryModel category_model = new CategoryModel();
    TextView signOut, home;


    FirebaseDatabase database = FirebaseDatabase.getInstance();

    DatabaseReference collectItRef = database.getReference("category");
//method for back button
    @Override
    public void onBackPressed() {
        startActivity(new Intent(AddCategoryActivity.this, ViewCategoriesActivity.class));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);
        etCategoryName = findViewById(R.id.edCategoryName);
        etItemGoal = findViewById(R.id.edItemGoal);
        btnAddCategory = findViewById(R.id.btnAddCategory);
        signOut = findViewById(R.id.signOut);
        home = findViewById(R.id.home);

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Signs the user out of the application
                //Griffins (2017). How to make a user sign out in Firebase? [online] Stack Overflow. Available at: https://stackoverflow.com/questions/42571618/how-to-make-a-user-sign-out-in-firebase [Accessed 22 Jun. 2022].
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(AddCategoryActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Goes to category activity
                startActivity(new Intent(AddCategoryActivity.this, ViewCategoriesActivity.class));
            }
        });

        btnAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Add();
            }
        });
    }
    //method for add category button
    public void Add(){
        String userID = FirebaseAuth.getInstance().getUid().toString();
        String catName = etCategoryName.getText().toString();
        String itemGoal = etItemGoal.getText().toString();

        //ensuring all fields have been inputted
        if (!TextUtils.isEmpty(catName) && !TextUtils.isEmpty(itemGoal)) {
            category_model.setUserID(userID);
            category_model.setCategoryName(catName);
            category_model.setItemGoal(itemGoal);

            try {

                int x = Integer.parseInt(itemGoal);
                //write to the database
                collectItRef.push().setValue(category_model);
                Toast.makeText(AddCategoryActivity.this, "Category Added ",
                        Toast.LENGTH_SHORT).show();
                etCategoryName.setText("");
                etItemGoal.setText("");


            }      catch (NumberFormatException e) {
                etItemGoal.setError("Invalid value");

            }

        } else {
            Toast.makeText(AddCategoryActivity.this, "Complete All Fields",
                    Toast.LENGTH_SHORT).show();
        }
    }


}