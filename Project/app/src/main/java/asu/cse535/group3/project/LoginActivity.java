package asu.cse535.group3.project;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class LoginActivity extends AppCompatActivity {
    private DatabaseHandler dh;
    private String username = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        dh = new DatabaseHandler(this);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            username = getIntent().getStringExtra("USERNAME");
        }

        if (username.equals("")) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);

            final EditText etUsername = (EditText) findViewById(R.id.etUsername);
            final EditText etPassword = (EditText) findViewById(R.id.etPassword);
            final TextView tvRegisterLink = (TextView) findViewById(R.id.tvRegisterLink);
            final Button bLogin = (Button) findViewById(R.id.bSignIn);

            tvRegisterLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                    LoginActivity.this.startActivity(registerIntent);
                }
            });

            bLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String username = etUsername.getText().toString();
                    final String password = etPassword.getText().toString();

                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);

                    if (!dh.isLoginCorrect(username, password)) {
                        builder.setMessage("Username and/or password is incorrect.")
                                .setNegativeButton("Retry", null)
                                .create()
                                .show();
                    } else {
                        builder.setMessage("Thank you for logging in.")
                                .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        Intent settingsIntent = new Intent(LoginActivity.this, MainActivity.class);
                                        settingsIntent.putExtra("USERNAME", username);
                                        startActivity(settingsIntent);

                                    }
                                })
                                .create()
                                .show();
                    }
                }

            });
        } else {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.logged_in);
            TextView welcome = (TextView) findViewById(R.id.welcome);
            welcome.setText("Welcome, " + username);

            Button home = (Button) findViewById(R.id.loggedInRedirect);
            home.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("USERNAME", username);
                    LoginActivity.this.startActivity(intent);
                }
            });

            Button logout = (Button) findViewById(R.id.logout);
            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);

                    builder.setMessage("You have been logged out.")
                            .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    Intent settingsIntent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(settingsIntent);
                                }
                            })
                            .create()
                            .show();


                }
            });
        }
    }
}
