package com.varsity.collectit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ItemsActivity extends AppCompatActivity {

    ImageView mImageView;
    Button mChooseBtn;
    Button mAddBtn;
    TextView signOut, home;

    private EditText mItemName;
    private EditText mDescription;
    private Button mDateOfAcq;
    private EditText mCost;
    private Spinner mCategory;
    //private Button button;

    private DatePickerDialog datePickerDialog;
    private Button dateButton;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(FirebaseAuth.getInstance().getCurrentUser().getUid());

    FirebaseStorage fireStorage;
    StorageReference storageReference;
    private Uri filepath = null;

    Items items;

    public String ImageURL;

    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ItemsActivity.this, ViewCategoriesActivity.class));
    }

    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);

        //button = findViewById(R.id.button);
        List<String> spinnerCat;

        fireStorage = FirebaseStorage.getInstance();
        storageReference = fireStorage.getReference();

        intDatePicker();
        dateButton = findViewById(R.id.datePicker);
        dateButton.setText(getTodaysDate());

        signOut = findViewById(R.id.signOut);
        home = findViewById(R.id.home);

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Signs the user out of the application
                //Griffins (2017). How to make a user sign out in Firebase? [online] Stack Overflow. Available at: https://stackoverflow.com/questions/42571618/how-to-make-a-user-sign-out-in-firebase [Accessed 22 Jun. 2022].
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(ItemsActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Goes to category activity
                startActivity(new Intent(ItemsActivity.this, ViewCategoriesActivity.class));
            }
        });

        //Coding Demos (2016). Android Drop Down List Tutorial. YouTube. Available at: https://www.youtube.com/watch?v=urQp7KsQhW8&ab_channel=CodingDemos [Accessed 27 May 2022].
        Spinner mySpinner = (Spinner) findViewById(R.id.spinner);
        spinnerCat = new ArrayList<>();
        String userID = FirebaseAuth.getInstance().getUid().toString();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("category").orderByChild("userID").equalTo(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot childSnap : snapshot.getChildren()) {
                    String spinnerName = childSnap.child("categoryName").getValue(String.class);
                    spinnerCat.add(spinnerName);
                }
                ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(ItemsActivity.this, android.R.layout.simple_list_item_1, spinnerCat);
                myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mySpinner.setAdapter(myAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mImageView = findViewById(R.id.image_view);
        mChooseBtn = findViewById(R.id.choose_image_btn);
        mAddBtn = findViewById(R.id.buttonAdd);

        mItemName = findViewById(R.id.editTextTextItemName);
        mDescription = findViewById(R.id.editTextTextDescription);
        mDateOfAcq = findViewById(R.id.datePicker);
        mCost = findViewById(R.id.editTextTextCostOfItem);
        mCategory = findViewById(R.id.spinner);

        firebaseDatabase = FirebaseDatabase.getInstance();

        databaseReference = firebaseDatabase.getReference("Items").push();

        items = new Items();

        mAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String itemName = mItemName.getText().toString();
                String description = mDescription.getText().toString();
                String dateofAcq = mDateOfAcq.getText().toString();
                String cost = mCost.getText().toString();
                String category = mCategory.toString();
                String url = ImageURL;

                if (TextUtils.isEmpty(itemName) && TextUtils.isEmpty(description) && TextUtils.isEmpty(dateofAcq) && TextUtils.isEmpty(cost)) {

                    Toast.makeText(ItemsActivity.this, "Please fill all the fields", Toast.LENGTH_LONG).show();

                } else {
                    uploadImage();
                }
            }
        });

        mChooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_DENIED) {
                        //permission already granted
                        pickImageFromGallery();
                    }
                } else {
                    //System OS is older than marshmallow
                    pickImageFromGallery();
                }
            }
        });
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }
    //handle result of runtime permission


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //Pervaiz, A. (2018). Pick an Image from the Gallery – Android Studio - Java. YouTube. Available at: https://www.youtube.com/watch?v=O6dWwoULFI8&ab_channel=AtifPervaiz [Accessed 20 May 2022].
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {
                    //Permission was granted
                    pickImageFromGallery();
                } else {
                    //permission was denied
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            filepath = data.getData();
            try{
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filepath);
                mImageView.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //mImageView.setImageURI(data.getData());
    }

    private void addDatatoFirebase(String itemName, String description, String dateofAcq, String cost, String category, String URL) {
        //Add data to firebase
        String userID = FirebaseAuth.getInstance().getUid().toString();

        Spinner mySpinner = (Spinner) findViewById(R.id.spinner);
        String spinnerText = mySpinner.getSelectedItem().toString();

        DatabaseReference refCategory = FirebaseDatabase.getInstance().getReference();
        refCategory.child("category").orderByChild("categoryName").equalTo(spinnerText).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String catKey = dataSnapshot.getKey();

                    items.setUserID(userID);
                    items.setCategoryID(catKey);
                    items.setItemName(itemName);
                    items.setDescription(description);
                    items.setDateofAcq(dateofAcq);
                    items.setCost(cost);
                    items.setImageURL(URL);

                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            databaseReference.setValue(items);
                            //databaseReference.setValue(items);
                            Toast.makeText(ItemsActivity.this, "data added", Toast.LENGTH_SHORT).show();
                            //Chirag (2011). Clear text in EditText when entered. [online] Stack Overflow. Available at: https://stackoverflow.com/questions/5308200/clear-text-in-edittext-when-entered [Accessed 24 Jun. 2022].
                            mItemName.getText().clear();
                            mDescription.getText().clear();
                            mCost.getText().clear();

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(ItemsActivity.this, "Fail to add data ", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void openActivity2() {
        //To open new Intent
        Intent intent = new Intent(this, ItemsCreateActivity.class);
        startActivity(intent);
    }

    private void uploadImage() {
        //Method to upload image
        //Izuchukwu, C. (2017). How to Upload Images to Firebase from an Android App. [online] Code Envato Tuts+. Available at: https://code.tutsplus.com/tutorials/image-upload-to-firebase-in-android-application--cms-29934 [Accessed 28 May 2022].
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Image is being uploaded...");
        progressDialog.show();
        StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
        ref.putFile(filepath)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                //Variables to retreive image URL, and othe information
                                ImageURL = uri.toString();
                                String itemName = mItemName.getText().toString();
                                String description = mDescription.getText().toString();
                                String dateofAcq = mDateOfAcq.getText().toString();
                                String cost = mCost.getText().toString();
                                String category = mCategory.toString();
                                String url = ImageURL;

                                progressDialog.dismiss();

                                //Toast shown to display image is uploaded to database
                                Toast.makeText(ItemsActivity.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
                                addDatatoFirebase(itemName, description, dateofAcq, cost, category, url);
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    //Stated if the image has not been uploaded
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(ItemsActivity.this, "Upload Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    //To view the progress of the upload
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                .getTotalByteCount());
                        progressDialog.setMessage("Uploaded " + (int) progress + "%");
                    }
                });
    }
    //Code With Cal (2020). Pop Up Date Picker Android Studio Tutorial. YouTube. Available at: https://www.youtube.com/watch?v=qCoidM98zNk&ab_channel=CodeWithCal [Accessed 24 Jun. 2022].
    private void intDatePicker() {
        //Datepicker dialog declared
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                //Setting date String and the text
                month = month + 1;
                String date = makeDateString(day, month, year);
                dateButton.setText(date);

            }
        };
        //Code With Cal (2020). Pop Up Date Picker Android Studio Tutorial. YouTube. Available at: https://www.youtube.com/watch?v=qCoidM98zNk&ab_channel=CodeWithCal [Accessed 24 Jun. 2022].
        //Declaring Calendar and setting calue for day, month, and year
        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);
        //Setting theme
        int style = AlertDialog.THEME_HOLO_DARK;

        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);

    }

    private String makeDateString(int day, int month, int year) {

        return day + " " + getMonthFormat(month) + " " + year;

    }
    //Code With Cal (2020). Pop Up Date Picker Android Studio Tutorial. YouTube. Available at: https://www.youtube.com/watch?v=qCoidM98zNk&ab_channel=CodeWithCal [Accessed 24 Jun. 2022].
    private String getMonthFormat(int month) {
        //Getting month format
        if(month == 1)
            return "January";

        if(month == 2)
            return "February";

        if(month == 3)
            return "March";

        if(month == 4)
            return "April";

        if(month == 5)
            return "May";

        if(month == 6)
            return "June";

        if(month == 7)
            return "July";

        if(month == 8)
            return "August";

        if(month == 9)
            return "September";

        if(month == 10)
            return "October";

        if(month == 11)
            return "November";

        if(month == 12)
            return "December";

        //Default, Should never happen
        return  "January";
    }

    public void openDatePicker(View view) {

        datePickerDialog.show();

    }
    //Code With Cal (2020). Pop Up Date Picker Android Studio Tutorial. YouTube. Available at: https://www.youtube.com/watch?v=qCoidM98zNk&ab_channel=CodeWithCal [Accessed 24 Jun. 2022].
    private String getTodaysDate() {
        //Getting todays Date
        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH);
        month = month + 1;
        int year = cal.get(Calendar.YEAR);

        return makeDateString(day, month, year);

    }

}