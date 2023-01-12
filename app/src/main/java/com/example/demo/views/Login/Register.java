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
import android.util.Log;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class Register extends AppCompatActivity {

    private TextInputLayout registerNameWrapper;
    private TextInputLayout registerEmailWrapper;
    private TextInputLayout registerPasswordWrapper;
    private TextInputLayout registerConfirmWrapper;

    String userID;
    Button registerButton;
    TextInputEditText registerC;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        TextView textView = findViewById(R.id.textView5);

        String text = "Already have an account? Login";

        SpannableString ss = new SpannableString(text);

        ClickableSpan clickableSpan1 = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                startActivity(new Intent (Register.this,MainActivity.class));
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

        textView.setHighlightColor(Color.TRANSPARENT);

        ss.setSpan(clickableSpan1, 25, 30, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(ss);
        textView.setMovementMethod(LinkMovementMethod.getInstance());


        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        progressBar = findViewById(R.id.progressBar);

        registerNameWrapper = findViewById(R.id.registerNameWrapper);
        registerEmailWrapper = findViewById(R.id.registerEmailWrapper);
        registerPasswordWrapper = findViewById(R.id.registerPasswordWrapper);
        registerConfirmWrapper = findViewById(R.id.registerConfirmWrapper);

        registerButton = findViewById(R.id.registerButton);
        registerC = findViewById(R.id.registerConfirm);

        registerC.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    registerButton.performClick();
                }
                return false;
            }
        });

    }


    private boolean validateRegisterName() {
        String registerName = registerNameWrapper.getEditText().getText().toString().trim();

        if (registerName.isEmpty()) {
            registerNameWrapper.setError("Field can't be empty");
            return false;
        } else if (registerName.length() < 3) {
            registerNameWrapper.setError("Please enter a valid name");
            return false;
        } else {
            registerNameWrapper.setError(null);
            return true;
        }
    }


    private boolean validateRegisterEmail() {
        String registerEmail = registerEmailWrapper.getEditText().getText().toString().trim();

        if (registerEmail.isEmpty()) {
            registerEmailWrapper.setError("Field can't be empty");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(registerEmail).matches()) {
            registerEmailWrapper.setError("Please enter a valid email address");
            return false;
        } else {
            registerEmailWrapper.setError(null);
            return true;
        }
    }

    private boolean validateRegisterPassword() {
        String registerPassword = registerPasswordWrapper.getEditText().getText().toString().trim();

        if (registerPassword.isEmpty()) {
            registerPasswordWrapper.setError("Field can't be empty");
            return false;
        } else if (registerPassword.length() < 6) {
            registerPasswordWrapper.setError("Password must have more than 5 characters!");
            return false;
        } else {
            registerPasswordWrapper.setError(null);
            return true;
        }
    }

    private boolean validateRegisterConfirm() {
        String registerConfirm = registerConfirmWrapper.getEditText().getText().toString().trim();
        String registerPassword = registerPasswordWrapper.getEditText().getText().toString().trim();

        if (registerConfirm.isEmpty() && validateRegisterPassword()) {
            registerConfirmWrapper.setError("Field can't be empty");
            return false;
        } else if (!registerConfirm.equals(registerPassword) && validateRegisterPassword()) {
            registerConfirmWrapper.setError("Passwords don't match. Try again!");
            registerConfirmWrapper.getEditText().getText().clear();
            return false;
        } else if(!registerConfirm.isEmpty() && (!validateRegisterName() || !validateRegisterEmail() || !validateRegisterPassword())){
            registerConfirmWrapper.getEditText().getText().clear();
            registerConfirmWrapper.setError(null);
            return false;
        }
        else {
            registerConfirmWrapper.setError(null);
            return true;
        }
    }


    public void createUser(View view) {

        final String registerName = registerNameWrapper.getEditText().getText().toString().trim();
        final String registerEmail = registerEmailWrapper.getEditText().getText().toString().trim();
        String registerPassword = registerPasswordWrapper.getEditText().getText().toString().trim();

        if (!validateRegisterName() | !validateRegisterEmail() | !validateRegisterPassword() | !validateRegisterConfirm()) {
            return;
        }

        registerButton.setEnabled(false);
        registerButton.setBackground(this.getResources().getDrawable(R.drawable.disable));

        closeKeyboard();

        progressBar.setVisibility(View.VISIBLE);


        fAuth.createUserWithEmailAndPassword(registerEmail, registerPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(Register.this, "User Created.", Toast.LENGTH_SHORT).show();

                    userID = fAuth.getCurrentUser().getUid();
                    DocumentReference documentReference = fStore.collection("users").document(userID);

                    Map<String, Object> user = new HashMap<>();
                    user.put("fName", registerName);
                    user.put("Email", registerEmail);

                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("TAG", "onSuccess: user Profile is created for " + userID);
                        }
                    });

                    startActivity(new Intent(getApplicationContext(), Home.class));

                }
                else{
                    Toast.makeText(Register.this, "Error!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);

                    registerButton.setEnabled(true);
                    registerButton.setBackgroundResource(R.drawable.round);

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
        Intent a = new Intent(Register.this,MainActivity.class);
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
