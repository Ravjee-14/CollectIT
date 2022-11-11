package com.varsity.collectit;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ItemAdapter extends FirebaseRecyclerAdapter<Items, ItemAdapter.myViewHolder> {
    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public ItemAdapter(@NonNull FirebaseRecyclerOptions<Items> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, final int position, @NonNull Items model) {

        //Setting objects to class
        holder.itemName.setText(model.getItemName());
        holder.description.setText(model.getDescription());
        holder.cost.setText(model.getCost());
        holder.dateofAcq.setText(model.getDateofAcq());

        //Glide is used to load images into the activity
        Glide.with(holder.img.getContext())
                .load(model.getImageURL())
                .placeholder(com.firebase.ui.database.R.drawable.common_google_signin_btn_icon_dark)
                .circleCrop()
                .error(com.google.android.gms.base.R.drawable.common_google_signin_btn_icon_dark_normal)
                .into(holder.img);

        ////BTech Days (2021). 4. Update and Delete data in Firebase Database | Android. YouTube. Available at: https://www.youtube.com/watch?v=7VNrh09Epmw&ab_channel=BTechDays [Accessed 22 May 2022].
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Proimakis, M. (2013). How to use the AlertDialog’s return value. [online] Stack Overflow. Available at: https://stackoverflow.com/questions/17894420/how-to-use-the-alertdialogs-return-value [Accessed 24 Jun. 2022].
                //Alert Dialog used to open to display whether the user accepts to delete data
                AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemName.getContext());
                builder.setTitle("Are you sure?");
                builder.setMessage("Deleted data can't be undo");

                //Proimakis, M. (2013). How to use the AlertDialog’s return value. [online] Stack Overflow. Available at: https://stackoverflow.com/questions/17894420/how-to-use-the-alertdialogs-return-value [Accessed 24 Jun. 2022].
                //Positive button set to delete
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Data has been removed from the database
                        FirebaseDatabase.getInstance().getReference().child("Items")
                                .child(getRef(holder.getAbsoluteAdapterPosition()).getKey()).removeValue();
                        Toast.makeText(holder.itemName.getContext(), "Item Successfully Deleted", Toast.LENGTH_SHORT).show();
                    }
                });
                //Proimakis, M. (2013). How to use the AlertDialog’s return value. [online] Stack Overflow. Available at: https://stackoverflow.com/questions/17894420/how-to-use-the-alertdialogs-return-value [Accessed 24 Jun. 2022].
                //Negative button set to cancel process
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(holder.itemName.getContext(), "Canceled", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
            }
        });

    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_item,parent,false);
        return new myViewHolder(view);
    }

    class myViewHolder extends RecyclerView.ViewHolder{

        CircleImageView img;
        TextView itemName, description, cost, dateofAcq, itemGoal;

        Button btnDelete;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            //Delclaring textviews and image to objects
            img = (CircleImageView)itemView.findViewById(R.id.img1);
            itemName = (TextView)itemView.findViewById(R.id.itemNametxt);
            description = (TextView)itemView.findViewById(R.id.descriptiontxt);
            cost = (TextView)itemView.findViewById(R.id.costtxt);
            dateofAcq = (TextView)itemView.findViewById(R.id.dateofAcqtxt);
            itemGoal = (TextView)itemView.findViewById(R.id.txtItemGoal);

            btnDelete = (Button) itemView.findViewById(R.id.btnDeleteItem);
        }
    }
}
