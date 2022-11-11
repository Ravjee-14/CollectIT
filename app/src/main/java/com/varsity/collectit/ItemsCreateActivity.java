package com.varsity.collectit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

public class ItemsCreateActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    ItemAdapter itemAdapter;
    TextView textView, signOut, home, viewGoal, percentage, snapVal;

    ProgressBar progressBar;

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ItemsCreateActivity.this, ViewCategoriesActivity.class));
    }

    //BTech Days (2021). 2. Retrieve Firebase data to RecyclerView. YouTube. Available at: https://www.youtube.com/watch?v=ePKC5ZEqeNY&ab_channel=BTechDays [Accessed 27 May 2022].
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items_create);

        //Declared Textviews
        textView = findViewById(R.id.tvCategory);
        viewGoal = findViewById(R.id.viewGoal);
        percentage = findViewById(R.id.percentage);
        snapVal = findViewById(R.id.snapvalue);

        //Declared TextViews for user to navigate
        signOut = findViewById(R.id.signOut);
        home = findViewById(R.id.home);

        //Progress bar declared
        progressBar = findViewById(R.id.progressBar);

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Signs the user out of the application
                //Griffins (2017). How to make a user sign out in Firebase? [online] Stack Overflow. Available at: https://stackoverflow.com/questions/42571618/how-to-make-a-user-sign-out-in-firebase [Accessed 22 Jun. 2022].
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(ItemsCreateActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Goes to category activity
                startActivity(new Intent(ItemsCreateActivity.this, ViewCategoriesActivity.class));
            }
        });

        CategoryModel model = new CategoryModel();

        recyclerView = (RecyclerView)findViewById(R.id.rv1);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        String userID = FirebaseAuth.getInstance().getUid().toString();
        //String cat = model.getBtnName();

        Items items = new Items();
        Intent intent = getIntent();
        //GeeksfoGeeks (2019). Android | How to send data from one activity to second activity - GeeksforGeeks. [online] GeeksforGeeks. Available at: https://www.geeksforgeeks.org/android-how-to-send-data-from-one-activity-to-second-activity/ [Accessed 26 Jun. 2022].
        //Getting category name from previous intent
        String catName = intent.getStringExtra("catName");
        textView.setText(catName);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        //van Puffelen, F. (2016). How to get the key from the value in firebase. [online] Stack Overflow. Available at: https://stackoverflow.com/questions/38232140/how-to-get-the-key-from-the-value-in-firebase [Accessed 25 Jun. 2022].
        databaseReference.child("category").orderByChild("categoryName").equalTo(catName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String catKey;
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    catKey = dataSnapshot.getKey();
                    items.setCategoryID(catKey);

                    //BTech Days (2021). 2. Retrieve Firebase data to RecyclerView. YouTube. Available at: https://www.youtube.com/watch?v=ePKC5ZEqeNY&ab_channel=BTechDays [Accessed 27 May 2022].
                    //Data is being pulled into the recycler view
                    //Ordered by Category ID
                    FirebaseRecyclerOptions<Items> options =
                            new FirebaseRecyclerOptions.Builder<Items>()
                                    .setQuery(FirebaseDatabase.getInstance().getReference("Items").orderByChild("categoryID").equalTo(items.getCategoryID()), Items.class)
                                    .build();
                    itemAdapter = new ItemAdapter(options);
                    recyclerView.setAdapter(itemAdapter);

                    //GeeksfoGeeks (2019). Android | How to send data from one activity to second activity - GeeksforGeeks. [online] GeeksforGeeks. Available at: https://www.geeksforgeeks.org/android-how-to-send-data-from-one-activity-to-second-activity/ [Accessed 26 Jun. 2022].
                    String itemG = intent.getStringExtra("ItemGoal");
                    //van Puffelen, F. (2021). How to find the count of specific values from Firebase in Android Studio? [online] Stack Overflow. Available at: https://stackoverflow.com/questions/69867867/how-to-find-the-count-of-specific-values-from-firebase-in-android-studio [Accessed 29 Jun. 2022].
                    databaseReference.child("Items").orderByChild("categoryID").equalTo(catKey).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            //Method is used to set the progress bar
                            //shows the progress to the user depending on number of items and total items
                            int getSnap = (int) snapshot.getChildrenCount();
                            progressBar.setProgress(getSnap);
                            progressBar.setMax(Integer.parseInt(itemG));
                            double percentageValue = getSnap * 1.0 / Integer.parseInt(itemG) * 100;
                            //Textview displays items out of total number of items
                            viewGoal.setText(snapshot.getChildrenCount() + "/" + itemG + " Items");
                            //Displays the percentage of the progress bar
                            percentage.setText(String.valueOf(percentageValue) + "%");
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });
                    //Adapter used to start listening for data
                    itemAdapter.startListening();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        itemAdapter.stopListening();
    }
}