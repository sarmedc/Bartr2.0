package com.example.sarmedchaudhry.bartr20;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;
import java.io.Serializable;

public class LogIn extends AppCompatActivity {

    private DBHelper dbHandler;
    private EditText user_email;
    private EditText user_password;
    private EditText signUpName;
    private EditText signUpEmail;
    private EditText signUpPassword;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sp = getSharedPreferences("userInfo", Context.MODE_PRIVATE);

        if(sp.contains("userName")){
            Intent i = new Intent(this, MainActivity.class);
            i.putExtra("users_name", sp.getString("userName", ""));
            i.putExtra("users_email", sp.getString("userEmail", ""));
            i.putExtra("users_password", sp.getString("userPassword", ""));
            i.putExtra("db", (Serializable) dbHandler);
            startActivity(i);
        }

        dbHandler = new DBHelper(this,null,null,1);

        user_email = (EditText) findViewById(R.id.user_email);
        user_password = (EditText) findViewById(R.id.user_password);

    }

    public void logInPressed(View view){
        String email = user_email.getText().toString();
        String password = user_password.getText().toString();

        String name = dbHandler.validLogIn(email, password);

        if(name != ""){
            //save preferences and go to main activity

            SharedPreferences.Editor editor = sp.edit();
            editor.putString("userName", name);
            editor.putString("userEmail", email);
            editor.putString("userPassword", password);

            editor.apply();

            Intent i = new Intent(this, MainActivity.class);
            i.putExtra("users_name", name);
            i.putExtra("users_email", email);
            i.putExtra("users_password", password);
            i.putExtra("db", dbHandler);
              startActivity(i);
        }
        else{
            //give error and try again
            Toast.makeText(this, "Could not log in. Try again", Toast.LENGTH_LONG).show();
        }
    }

    //still need:
        // check if email already in use
        //
    public void signUpPressed(View view) {

        View promptsView = View.inflate(this, R.layout.signup_dialog, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptsView);

        signUpEmail = (EditText) promptsView.findViewById(R.id.signup_email);
        signUpPassword = (EditText) promptsView.findViewById(R.id.signup_password);
        signUpName = (EditText) promptsView.findViewById(R.id.signup_name);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Sign Up", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // get user input and set it to result
                        // edit text
                        while (TextUtils.isEmpty(signUpEmail.getText().toString()) && TextUtils.isEmpty(signUpPassword.getText().toString()) && TextUtils.isEmpty(signUpName.getText().toString())) {
                            if (TextUtils.isEmpty(signUpEmail.getText().toString())) {
                                signUpEmail.setError("Enter Valid Email");
                            }
                            if (TextUtils.isEmpty(signUpPassword.getText().toString())) {
                                signUpPassword.setError("Enter Valid Password");
                            }
                            if (TextUtils.isEmpty(signUpName.getText().toString())) {
                                signUpName.setError("Enter Valid Name");
                            }
                        }

                        Users newUser = new Users(signUpName.getText().toString(), signUpEmail.getText().toString(), signUpPassword.getText().toString());
                        dbHandler.addUser(newUser);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.setTitle("Sign Up");

        // show it
        alertDialog.show();
    }

    /*public void stupidButton(View view) {
        dbHandler.stupidSpelling();
        Toast.makeText(this, "Stupid Spelling Fix Done", Toast.LENGTH_LONG).show();

    }*/
}
