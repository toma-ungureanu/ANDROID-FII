package com.example.onlineshop;

import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import java.io.*;

public class ProfileActivity extends AppCompatActivity
{
    private static final String FILE_NAME = "example.txt";
    EditText mEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final Button button = findViewById(R.id.button_id);
        button.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                onGoBackSelected();
            }
        });

        final Button button1 = findViewById(R.id.button_id1);
        button1.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                onChangePassword();
            }
        });

        mEditText = findViewById(R.id.editTextDialogUserInput1);
    }

    public void save(View v)
    {
        String text = mEditText.getText().toString();
        FileOutputStream fos = null;

        try
        {
            fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
            fos.write(text.getBytes());

            mEditText.getText().clear();
            Toast.makeText(this, "Saved to " + getFilesDir() + "/" + FILE_NAME,
                    Toast.LENGTH_LONG).show();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (fos != null)
            {
                try
                {
                    fos.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    public void load(View v)
    {
        FileInputStream fis = null;
        try
        {
            fis = openFileInput(FILE_NAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text;

            while ((text = br.readLine()) != null)
            {
                sb.append(text).append("\n");
            }

            mEditText.setText(sb.toString().replace("\n"," "));

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (fis != null)
            {
                try
                {
                    fis.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
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
