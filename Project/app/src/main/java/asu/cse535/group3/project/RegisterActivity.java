package asu.cse535.group3.project;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class RegisterActivity extends AppCompatActivity {

    private DatabaseHandler dh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dh = new DatabaseHandler(this);

        final EditText etName = (EditText) findViewById(R.id.etName);
        final EditText etUsername = (EditText) findViewById(R.id.etUsername);
        final EditText etPassword = (EditText) findViewById(R.id.etPassword);
        final Button bRegister = (Button) findViewById(R.id.bRegister);

        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString();
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);

                if (password.equals("") || username.equals("") || name.equals(""))
                {
                    builder.setMessage("Fields cannot be empty")
                            .setNegativeButton("Retry", null)
                            .create()
                            .show();
                }
                else if (!dh.addUser(name, username, password))
                {
                    builder.setMessage("Username already exists")
                            .setNegativeButton("Retry", null)
                            .create()
                            .show();
                }
                else
                {
                    builder.setMessage("You have been successfully registered.")
                            .setPositiveButton("Close",null)
                            .setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialogInterface) {
                                    Log.d("ok", "ok2");
                                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                    RegisterActivity.this.startActivity(intent);
                                }
                            })
                            .create()
                            .show();
                }
            }
        });
    }



}