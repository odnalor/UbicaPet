package com.ubicapet.rolando.ubicapet_3;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;


public class Transferir extends Activity {
    protected ParseUser mCurrentUser, newUser;
    protected String mName, mNew, id;
    protected EditText mUsuarioNuevo;
    protected Button mTransferir, mPrueba;
    protected String[] PetsNames;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transferir);
        mCurrentUser = ParseUser.getCurrentUser();
        Intent intent = getIntent();
        mName= intent.getStringExtra("Pet");
        mUsuarioNuevo  = (EditText)findViewById(R.id.edit_usuario);
        mNew = mUsuarioNuevo.getText().toString();
        mNew = mNew.trim();


        mPrueba = (Button) findViewById(R.id.button2);
        mPrueba.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final ParseQuery<ParseObject> query = ParseQuery.getQuery("User");

                query.getFirstInBackground(new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject parseObject, ParseException e) {
                        if (e == null){

                            PetsNames = new String[2];

                            PetsNames[0] = parseObject.getString("username");
                            PetsNames[1] = parseObject.getString("email");
                            Toast.makeText(Transferir.this, PetsNames[0]+"\n "+PetsNames[1], Toast.LENGTH_LONG).show();


                        }
                        else {
                            Toast.makeText(Transferir.this, "ERROR", Toast.LENGTH_LONG).show();
                        }
                    }
                });


            }
        });



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_transferir, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }
}
