package com.example.demo.views.Login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.demo.R;
import com.example.demo.views.Home;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private TextInputLayout loginEmailWrapper;
    private TextInputLayout loginPasswordWrapper;

    ProgressBar progressBar2;
    FirebaseAuth fAuth;

    Button loginButton;
    TextInputEditText loginPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


            TextView loginBottom = findViewById(R.id.loginBottom);
            TextView loginForgot = findViewById(R.id.loginForgot);

            String text = "Don't have an account? Register";
            SpannableString xx = new SpannableString(text);


            loginForgot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(), ForgotPassword.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                }
            });


            ClickableSpan clickableSpan2 = new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    startActivity(new Intent(MainActivity.this, Register.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }

                @Override
                public void updateDrawState(@NonNull TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setColor(getResources().getColor(R.color.colorPrimary));
                    ds.setUnderlineText(false);
                    ds.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                    ds.clearShadowLayer();
                }
            };

            loginBottom.setHighlightColor(Color.TRANSPARENT);

            xx.setSpan(clickableSpan2, 23, 31, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            loginBottom.setText(xx);
            loginBottom.setMovementMethod(LinkMovementMethod.getInstance());


            progressBar2 = findViewById(R.id.progressBar2);
            fAuth = FirebaseAuth.getInstance();


            loginEmailWrapper = findViewById(R.id.emailWrapper);
            loginPasswordWrapper = findViewById(R.id.passwordWrapper);

            loginButton = findViewById(R.id.loginButton);
            loginPassword = findViewById(R.id.loginPassword);

            loginPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                        loginButton.performClick();
                    }
                    return false;
                }
            });




        }


    private boolean validateLoginEmail() {
        String loginEmail = loginEmailWrapper.getEditText().getText().toString().trim();

        if (loginEmail.isEmpty()) {
            loginEmailWrapper.setError("Field can't be empty");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(loginEmail).matches()) {
            loginEmailWrapper.setError("Please enter a valid email address");
            return false;
        } else {
            loginEmailWrapper.setError(null);
            return true;
        }
    }

    private boolean validateLoginPassword() {
        String loginPassword = loginPasswordWrapper.getEditText().getText().toString().trim();

        if (loginPassword.isEmpty()) {
            loginPasswordWrapper.setError("Field can't be empty");
            return false;
        } else if (loginPassword.length() < 6) {
            loginPasswordWrapper.setError("Password must have more than 5 characters!");
            return false;
        } else {
            loginPasswordWrapper.setError(null);
            return true;
        }
    }



    public void loginUser(View view) {

        String loginEmail = loginEmailWrapper.getEditText().getText().toString().trim();
        String loginPassword = loginPasswordWrapper.getEditText().getText().toString().trim();

        if (!validateLoginEmail() | !validateLoginPassword()) {
            return;
        }


        loginButton.setEnabled(false);
        loginButton.setBackground(this.getResources().getDrawable(R.drawable.disable));
        closeKeyboard();

        progressBar2.setVisibility(View.VISIBLE);


        fAuth.signInWithEmailAndPassword(loginEmail, loginPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Logged in Successfully!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), Home.class));

                } else {
                    Toast.makeText(MainActivity.this, "Error!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar2.setVisibility(View.GONE);
                    loginButton.setEnabled(true);
                    loginButton.setBackgroundResource(R.drawable.round);

                }

            }
        });


    }

    private void closeKeyboard(){
        View view = this.getCurrentFocus();
        if (view != null){
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
}
