package com.varsity.collectit;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.HashMap;
import java.util.Map;

public class CategoryAdapter extends FirebaseRecyclerAdapter <CategoryModel,CategoryAdapter.myViewHolder>{

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public CategoryAdapter(@NonNull FirebaseRecyclerOptions<CategoryModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, final int position, @NonNull CategoryModel model) {

holder.btnCategories.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {

        String buttonText = holder.btnCategories.getText().toString();
        String txtGoal = holder.txtItemGoal.getText().toString();
        Intent intent = new Intent(holder.btnCategories.getContext(), ItemsCreateActivity.class);
        //GeeksfoGeeks (2019). Android | How to send data from one activity to second activity - GeeksforGeeks. [online] GeeksforGeeks. Available at: https://www.geeksforgeeks.org/android-how-to-send-data-from-one-activity-to-second-activity/ [Accessed 26 Jun. 2022].
        //Passing the category name to the next intent
        intent.putExtra("catName", buttonText);
        //passing the item goal to the next intent
        intent.putExtra("ItemGoal", txtGoal);
        model.setItemGoal(txtGoal);
        holder.btnCategories.getContext().startActivity(intent);
    }
});

        holder.btnCategories.setText(model.getCategoryName());
        holder.txtItemGoal.setText(model.getItemGoal());

        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            //BTech Days (2021). 4. Update and Delete data in Firebase Database | Android. YouTube. Available at: https://www.youtube.com/watch?v=7VNrh09Epmw&ab_channel=BTechDays [Accessed 22 May 2022].
            @Override
            public void onClick(View v) {
                //Edit page popup
                final DialogPlus dialogPlus = DialogPlus.newDialog(holder.btnCategories.getContext())
                        .setContentHolder(new ViewHolder(R.layout.update_category))
                        .setExpanded(true, 1200)
                        .create();

                View view = dialogPlus.getHolderView();

                EditText categoryName = view.findViewById(R.id.txtCategoryName);
                EditText itemGoal = view.findViewById(R.id.txtItemGoal);
                Button btnUpdate = view.findViewById(R.id.btnUpdate);

                TextView txtItemGoal;
                txtItemGoal = view.findViewById(R.id.txtItemGoal);
                TextView txtCategory;
                txtCategory = view.findViewById(R.id.txtCategoryName);
                //setting the textviews in the update page
                categoryName.setText(model.getCategoryName());
                itemGoal.setText(model.getItemGoal());

                dialogPlus.show();

                btnUpdate.setOnClickListener(new View.OnClickListener() {
                    //BTech Days (2021). 4. Update and Delete data in Firebase Database | Android. YouTube. Available at: https://www.youtube.com/watch?v=7VNrh09Epmw&ab_channel=BTechDays [Accessed 22 May 2022].

                    @Override
                    public void onClick(View view) {
                        String catName = txtCategory.getText().toString();
                        String itemGoal = txtItemGoal.getText().toString();
                        //checking if user inputs all required values
                        if (!TextUtils.isEmpty(catName) && !TextUtils.isEmpty(itemGoal)) {
                        //error handling for item goal input
                            try {
                                @SuppressWarnings("unused")
                                int x = Integer.parseInt(txtItemGoal.getText().toString());

                                Map<String, Object> map = new HashMap<>();
                                map.put("categoryName", categoryName.getText().toString());
                                map.put("itemGoal", itemGoal);

                                FirebaseDatabase.getInstance().getReference().child("category")
                                        .child((getRef(holder.getAbsoluteAdapterPosition()).getKey())).updateChildren(map)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(holder.btnCategories.getContext(), "Data Updated Successfully", Toast.LENGTH_SHORT).show();

                                                dialogPlus.dismiss();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(Exception e) {
                                                Toast.makeText(holder.btnCategories.getContext(), "Data Failed to Updated Successfully", Toast.LENGTH_SHORT).show();
                                                dialogPlus.dismiss();
                                            }
                                        });
                            } catch (NumberFormatException e) {
                                txtItemGoal.setError("Invalid value");
                            }
                        }
                        else{
                            Toast.makeText(holder.btnCategories.getContext(), "Complete all fields", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //BTech Days (2021). 4. Update and Delete data in Firebase Database | Android. YouTube. Available at: https://www.youtube.com/watch?v=7VNrh09Epmw&ab_channel=BTechDays [Accessed 22 May 2022].

                //delete categories
                AlertDialog.Builder builder = new AlertDialog.Builder(holder.btnCategories.getContext());
                builder.setTitle("Are you sure you want to delete the data?");
                builder.setMessage("Deleted Data can't be recovered.");

                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        FirebaseDatabase.getInstance().getReference().child("category").
                                child(getRef(holder.getAbsoluteAdapterPosition()).getKey()).removeValue();
                        Toast.makeText(holder.btnCategories.getContext(), "Deleted successfully", Toast.LENGTH_SHORT).show();

                    }
                });
                builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(holder.btnCategories.getContext(), "Canceled", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
            }
        });

    }

    //BTech Days (2021). 2. Retrieve Firebase data to RecyclerView. YouTube. Available at: https://www.youtube.com/watch?v=ePKC5ZEqeNY&ab_channel=BTechDays [Accessed 27 May 2022].
    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item,parent,
                false);
        return new myViewHolder(view);
    }

    class myViewHolder extends RecyclerView.ViewHolder{

        Button btnCategories;
        Button btnEdit, btnDelete;
        TextView txtItemGoal;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            btnCategories = (Button)itemView.findViewById(R.id.btnCategory);
            btnEdit = (Button) itemView.findViewById(R.id.btnEdit);
            btnDelete = (Button) itemView.findViewById(R.id.btnDelete);
            txtItemGoal = (TextView) itemView.findViewById(R.id.txtGetItemGoal);
        }
    }
}

