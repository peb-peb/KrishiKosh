package com.example.krishi_kosh;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class Login extends AppCompatActivity {
    EditText phone, otp;
    Button generate, verify;
    FirebaseAuth mAuth;
    String verificationId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        phone=(EditText) findViewById(R.id.phone);
        otp=(EditText) findViewById(R.id.otp);
        generate=(Button) findViewById(R.id.button1);
        verify=(Button) findViewById(R.id.button2);
        mAuth= FirebaseAuth.getInstance();
        generate.setOnClickListener(view -> {
            if(TextUtils.isEmpty((phone.getText())))
            {
                Toast.makeText(this, "Enter a valid phone number", Toast.LENGTH_SHORT).show();
            }
            else
            {
                String number=phone.getText().toString();
                senderverificationcode(number);
            }


        });
        verify.setOnClickListener(view -> {
            if(TextUtils.isEmpty((otp.getText())))
            {
                Toast.makeText(this, "Wrong OTP", Toast.LENGTH_SHORT).show();
            }
            else
            {
                verifycode(otp.getText().toString());
            }

        });
    }
    private void senderverificationcode(String phoneNumber) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+91"+phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
            final String code=credential.getSmsCode();
            if(code!=null)
            {
                verifycode(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(Login.this, "Login Verification Failed", Toast.LENGTH_SHORT).show();
        }

        // Show a message and update the UI


        @Override
        public void onCodeSent(@NonNull String s,
                               @NonNull PhoneAuthProvider.ForceResendingToken token) {
            super.onCodeSent(s,token);
            verificationId=s;
        }
    };
    private void verifycode(String code) {
        PhoneAuthCredential credential= PhoneAuthProvider.getCredential(verificationId,code);
        signinbyCredentials(credential);
    }

    private void signinbyCredentials(PhoneAuthCredential credential) {
        FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(Login.this, "Login Successfull", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Login.this,Homepage.class));
                }
            }
        });
    }

    /*@Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser=FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser!=null)
        {
            startActivity(new Intent(Login.this,Home.class));
        }

    }*/
}