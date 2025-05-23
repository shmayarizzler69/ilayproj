package com.example.myapplication.screens;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;

public class AboutActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView aboutText = findViewById(R.id.aboutText);
        aboutText.setText("ברוכים הבאים ל-Diet Master!\n\n" +
                "Diet Master היא אפליקציה שנועדה לעזור לכם לנהל את התזונה והבריאות שלכם בצורה פשוטה ויעילה.\n\n" +
                "תכונות עיקריות:\n" +
                "• מערכת הרשמה והתחברות מאובטחת\n" +
                "• ניהול יומן תזונה יומי\n" +
                "• מעקב אחר ארוחות ומזונות\n" +
                "• ממשק משתמש נוח ופשוט\n" +
                "• תמיכה מלאה בעברית\n" +
                "• התראות והתראות מותאמות אישית\n\n" +
                "Diet Master מאפשרת לכם:\n" +
                "• להוסיף ולנהל ימים חדשים\n" +
                "• לעקוב אחר התקדמות אישית\n" +
                "• לשמור על תזונה מאוזנת\n" +
                "• לקבל המלצות מותאמות אישית\n\n" +
                "אנחנו מחויבים לספק לכם את החוויה הטובה ביותר ולעזור לכם להשיג את המטרות שלכם.");
    }
} 