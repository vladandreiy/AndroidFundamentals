package com.example.stockwatch.Login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.stockwatch.MainActivity;
import com.example.stockwatch.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

public class LogInActivity extends AppCompatActivity {
    private TextInputLayout username;
    private TextInputLayout password;
    private MaterialButton nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        nextButton = findViewById(R.id.next_button);
        username = findViewById(R.id.username_text_input);
        password = findViewById(R.id.password_text_input);
//        EditText inpass = findViewById(R.id.passwordinput);
        SharedPreferences datasaver = getSharedPreferences("user_data", MODE_PRIVATE);
        String savedUsername = datasaver.getString("user", "");
        String savedPassword = datasaver.getString("pass", "");
//        TextView error = findViewById(R.id.Erorr);
        nextButton.setOnClickListener(v -> {
            String passwordInput = password.getEditText().getText().toString();
            if (passwordInput.equals(savedPassword)) {
                Intent intent = new Intent(LogInActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                if (!passwordInput.equals("")) {
//                    error.setText("Password is incorrect!");
                    Handler handler = new Handler();
//                    handler.postDelayed(() -> error.setText(""), 5000);
                    handler.postDelayed(() -> password.setError("Password is incorrect!"), 5000);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
    }
}