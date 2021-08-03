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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {
    private TextInputLayout username;
    private TextInputLayout password;
    private TextInputLayout password_confirm;
    private MaterialButton nextButton;

    public boolean checkPasswordConstraints(String proposedPass) {
        boolean isokay = true;
        Pattern pattern = Pattern.compile(".*\\s.*");
        Matcher matcher = pattern.matcher(proposedPass);
        boolean foundspace = matcher.find();
        if (foundspace) {
            isokay = false;
        }
        if (proposedPass.length() <= 6) {
            isokay = false;
        }
        Pattern pattern2 = Pattern.compile(".*[0-9]+.*");//regex matching any character that appear 0 or more time then a digit that appear at least once then any character zero or more times
        Matcher matcher2 = pattern2.matcher(proposedPass);//must contain at least one digit
        boolean founddigit = matcher2.find();
        if (!founddigit) {
            isokay = false;
        }
        return isokay;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        username = findViewById(R.id.username_text_input);
        password = findViewById(R.id.password_text_input);
        password_confirm = findViewById(R.id.confirm_password_text_input);
        nextButton = findViewById(R.id.next_button);
        nextButton.setOnClickListener(v -> {
            String usernameInput = username.getEditText().getText().toString();
            String passwordInput = password.getEditText().getText().toString();
            String passwordConfirmInput = password_confirm.getEditText().getText().toString();
            if (passwordInput.equals(passwordConfirmInput)) {
                if (checkPasswordConstraints(passwordInput) && usernameInput.length() >= 4) {
                    SharedPreferences pref = getSharedPreferences("preference", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("firststart", false);
                    editor.apply();
                    SharedPreferences data = getSharedPreferences("user_data", MODE_PRIVATE);
                    SharedPreferences.Editor datasaver = data.edit();
                    datasaver.putString("user", usernameInput);
                    datasaver.putString("pass", passwordInput);
                    datasaver.apply();
                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            } else {
                Handler handler = new Handler();
//                    handler.postDelayed(() -> error.setText(""), 5000);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        password.setError("Passwords don't match!");
                        password_confirm.setError("Passwords don't match!");
                    }
                }, 5000);
            }
        });
    }

    @Override
    public void onBackPressed() {
    }
}