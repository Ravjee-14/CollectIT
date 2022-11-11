package com.varsity.collectit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ViewCategoriesActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    CategoryAdapter category_adapter;
    Button btnAdd;
    Button btnItemsAdd;
    TextView signOut;

//method for back button
    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //BTech Days (2021). 2. Retrieve Firebase data to RecyclerView. YouTube. Available at: https://www.youtube.com/watch?v=ePKC5ZEqeNY&ab_channel=BTechDays [Accessed 27 May 2022].
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_categories);
        btnAdd = findViewById(R.id.btnAdd);
        btnItemsAdd = findViewById(R.id.btnItemsAdd);
        signOut = findViewById(R.id.signOut);

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Signs the user out of the application
                //Griffins (2017). How to make a user sign out in Firebase? [online] Stack Overflow. Available at: https://stackoverflow.com/questions/42571618/how-to-make-a-user-sign-out-in-firebase [Accessed 22 Jun. 2022].
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(ViewCategoriesActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

//retrieving all data from firebase and putting it into the recycleview
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Goes to category activity
                startActivity(new Intent(ViewCategoriesActivity.this, AddCategoryActivity.class));
            }
        });
        btnItemsAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ViewCategoriesActivity.this, ItemsActivity.class));
            }
        });

        recyclerView = (RecyclerView)findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        String userID = FirebaseAuth.getInstance().getUid().toString();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

                FirebaseRecyclerOptions<CategoryModel> options =
                        new FirebaseRecyclerOptions.Builder<CategoryModel>()
                                .setQuery(FirebaseDatabase.getInstance().getReference().child("category").orderByChild("userID").equalTo(userID)
                                        , CategoryModel.class)
                                .build();

                category_adapter = new CategoryAdapter(options);
                recyclerView.setAdapter(category_adapter);
    }

    @Override
    protected void onStart(){
        super.onStart();
        category_adapter.startListening();
    }

    @Override
    protected void onStop(){
        super.onStop();
        category_adapter.stopListening();
    }

}