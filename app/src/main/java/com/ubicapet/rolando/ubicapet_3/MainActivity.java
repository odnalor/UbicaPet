package com.ubicapet.rolando.ubicapet_3;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.parse.Parse;
import com.parse.ParseUser;


public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();
    ParseUser currentUser;
    protected Button mPet, mAvisos, mMapa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            Parse.initialize(this, "juPQxKUpvnsO2XpEnl2iNJ3IkFUvkzUA72g9054y", "JR1KMJZzc5Nx5gBggDkXxwR0cvhNO3eb5elYzFmb");
        }
        catch (Exception e){

            Log.e(TAG,  getString(R.string.error_conexion), e);
        }
        currentUser = ParseUser.getCurrentUser();
        if (currentUser == null) {
            navigateToLogin();
        }
        else
        {
            Log.i(TAG, currentUser.getUsername());
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPet = (Button)findViewById(R.id.button_misMascotas);
        mPet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Lista_mascotas.class);
                startActivity(intent);
            }
        });

        mMapa = (Button)findViewById(R.id.button3);
        mMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, busquedaMapa.class);

                startActivity(intent);


            }
        });

    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id){
            case R.id.action_logout:
                ParseUser.logOut();
                navigateToLogin();



        }
        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }
}
