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

// מתאם (אדפטר) לתצוגת רשימת המשתמשים - משמש את מסך המנהל להצגת כל המשתמשים במערכת
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> userList;    // רשימת המשתמשים להצגה
    private Context context;        // הקשר האפליקציה - נדרש למעבר בין מסכים

    // בנאי - מקבל את רשימת המשתמשים והקשר האפליקציה
    public UserAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    // פונקציה שיוצרת תצוגה חדשה של משתמש
    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    // פונקציה שממלאת את תצוגת המשתמש בפרטים ומגדירה מעבר למסך פרטי משתמש בלחיצה
    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.userIdTextView.setText(user.getId());
        holder.userNameTextView.setText(user.getFname() + " " + user.getLname());

        // הגדרת מעבר למסך פרטי משתמש בלחיצה על משתמש
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, UserDetailsActivity.class);
            intent.putExtra("user", user);  // העברת פרטי המשתמש למסך הבא
            context.startActivity(intent);
        });
    }

    // פונקציה שמחזירה את מספר המשתמשים ברשימה
    @Override
    public int getItemCount() {
        return userList.size();
    }

    // מחלקה פנימית שמחזיקה את רכיבי התצוגה של משתמש בודד
    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView userIdTextView;     // מזהה המשתמש
        TextView userNameTextView;   // שם מלא של המשתמש

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userIdTextView = itemView.findViewById(R.id.userIdTextView);
            userNameTextView = itemView.findViewById(R.id.userNameTextView);
        }
    }
}
