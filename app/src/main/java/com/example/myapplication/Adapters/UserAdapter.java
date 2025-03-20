package com.example.myapplication.Adapters;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.models.User;
import com.example.myapplication.screens.UserDetailsActivity;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> userList;
    private OnDeleteClickListener onDeleteClickListener;
    private Context context;

    // Interface to handle delete button clicks
    public interface OnDeleteClickListener {
        void onDeleteClick(String userId);
    }

    public UserAdapter(Context context, List<User> userList, OnDeleteClickListener onDeleteClickListener) {
        this.context = context;
        this.userList = userList;
        this.onDeleteClickListener = onDeleteClickListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for the individual list item (each user)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        // Get the user data at the current position and bind it to the views
        User user = userList.get(position);
        holder.userIdTextView.setText(user.getId());
        holder.userNameTextView.setText(user.getFname() + " " + user.getLname());

        // Set up the delete button click listener
        holder.deleteUserButton.setOnClickListener(v -> {
            if (onDeleteClickListener != null) {
                onDeleteClickListener.onDeleteClick(user.getId());
            }
        });

        // Set up item click listener to open user details activity
        holder.itemView.setOnClickListener(v -> {
            // Navigate to the new activity with the user details
            Intent intent = new Intent(context, UserDetailsActivity.class);
            intent.putExtra("user", user);  // Pass the user object
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();  // Return the number of users
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView userIdTextView;
        TextView userNameTextView;
        Button deleteUserButton;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userIdTextView = itemView.findViewById(R.id.userIdTextView);
            userNameTextView = itemView.findViewById(R.id.userNameTextView);
            deleteUserButton = itemView.findViewById(R.id.deleteUserButton);
        }
    }
}
