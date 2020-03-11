package com.example.onlineshop;

import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class ProfileActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final Button button = findViewById(R.id.button_id);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onGoBackSelected();
            }
        });

        final Button button1 = findViewById(R.id.button_id1);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onChangePassword();
            }
        });
    }

    public void onGoBackSelected()
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void onChangePassword()
    {
        ChangePasswordDialog changePasswordDialog = new ChangePasswordDialog();
        changePasswordDialog.show(getSupportFragmentManager(), "example dialog");
    }
}
