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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    private TextInputLayout resetEmailWrapper;

    Button sendButton;
    TextInputEditText forgotE;

    ProgressBar progressBar3;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        TextView forgotBottom = findViewById(R.id.forgotBottom);

        String text = "Remember your password? Login";

        SpannableString yy = new SpannableString(text);

        ClickableSpan clickableSpan3 = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                startActivity(new Intent(ForgotPassword.this, MainActivity.class));
                finish();
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

        forgotBottom.setHighlightColor(Color.TRANSPARENT);

        yy.setSpan(clickableSpan3, 24, 29, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        forgotBottom.setText(yy);
        forgotBottom.setMovementMethod(LinkMovementMethod.getInstance());

        progressBar3 = findViewById(R.id.progressBar);
        fAuth = FirebaseAuth.getInstance();

        resetEmailWrapper = findViewById(R.id.resetEmailWrapper);

        sendButton = findViewById(R.id.resetButton);
        forgotE = findViewById(R.id.resetEmail);

        forgotE.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    sendButton.performClick();
                }
                return false;
            }
        });

    }


    private boolean validateResetEmail(){
        String resetEmail = resetEmailWrapper.getEditText().getText().toString().trim();

        if (resetEmail.isEmpty()) {
            resetEmailWrapper.setError("Field can't be empty");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(resetEmail).matches()) {
            resetEmailWrapper.setError("Please enter a valid email address");
            return false;
        } else {
            resetEmailWrapper.setError(null);
            return true;
        }
    }

    public void resetEmail(View view) {
        String resetEmail = resetEmailWrapper.getEditText().getText().toString().trim();

        if (!validateResetEmail()) {
            return;
        }

        sendButton.setEnabled(false);
        sendButton.setBackground(this.getResources().getDrawable(R.drawable.disable));

        closeKeyboard();
        progressBar3.setVisibility(View.VISIBLE);

        fAuth.sendPasswordResetEmail(resetEmail).addOnCompleteListener(new OnCompleteListener<Void>(){
        @Override
        public void onComplete(@NonNull Task<Void> task) {
            if (task.isSuccessful()) {
                Toast.makeText(ForgotPassword.this, "Password sent to your email", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            } else {
                Toast.makeText(ForgotPassword.this, "Error!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                progressBar3.setVisibility(View.GONE);

                sendButton.setEnabled(true);
                sendButton.setBackgroundResource(R.drawable.round);

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
        Intent a = new Intent(ForgotPassword.this,MainActivity.class);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
