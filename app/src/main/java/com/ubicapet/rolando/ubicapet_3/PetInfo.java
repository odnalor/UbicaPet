package com.ubicapet.rolando.ubicapet_3;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;


public class PetInfo extends ListActivity {
    private static final String TAG = PetInfo.class.getSimpleName();
    protected ParseUser mCurrentUser;
    protected String mName, mRaza;
    protected Button mPerdida;
    protected String[] PetsNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_pet_info);
        mCurrentUser = ParseUser.getCurrentUser();
        Intent intent = getIntent();
        mName= intent.getStringExtra("Pet");
        setProgressBarIndeterminateVisibility(true);
        if (isNetworkAvaible()){
            ListarInfo();

        }
        else {
            Toast.makeText(PetInfo.this, R.string.error_conexion, Toast.LENGTH_LONG).show();
        }

        mPerdida = (Button) findViewById(R.id.button_perdida);
        mPerdida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(PetInfo.this, Registro_Perdida.class);
                intent.putExtra("Pet", mName);
                intent.putExtra("Raza", mRaza);
                startActivity(intent);


            }
        });


    }

    private void ListarInfo() {
        final ParseQuery<ParseObject> query = ParseQuery.getQuery("Pets");
        query.whereEqualTo("parent", mCurrentUser);
        query.whereEqualTo("name", mName);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                setProgressBarIndeterminateVisibility(false);
                if (e == null ){

                    PetsNames = new String[4];

                    PetsNames[0] = parseObject.getString("name");
                    PetsNames[1] = parseObject.getString("Race");
                    PetsNames[2] = parseObject.getString("Char");
                    PetsNames[3] = parseObject.getString("Estado");
                    mRaza = parseObject.getString("Race");

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                            getListView().getContext(),
                            android.R.layout.simple_list_item_1, PetsNames);
                    setListAdapter(adapter);




                }

                else {
                    Log.e(TAG, getString(R.string.error_conexion), e);
                    AlertDialog.Builder builder = new AlertDialog.Builder(PetInfo.this);
                    builder.setMessage(getString(R.string.no_encontrada))
                            .setTitle(R.string.login_error_title)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();

                }

            }
        });
    }

    private boolean isNetworkAvaible() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()){
            isAvailable= true;
        }

        return isAvailable;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pet_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id){
            case R.id.action_delete :
                // borrar mascota
                ParseQuery query = new ParseQuery("Pets");
                query.whereEqualTo("parent", mCurrentUser);
                query.whereEqualTo("name", mName);
                query.getFirstInBackground(new GetCallback() {
                    @Override
                    public void done(ParseObject pet, ParseException e) {
                        pet.deleteInBackground();
                    }
                });

                ParseQuery query2 = new ParseQuery("Advertise");
                query2.whereEqualTo("parent", mCurrentUser);
                query2.whereEqualTo("name", mName);
                query2.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> avisos, ParseException e) {
                        if (e == null){
                            for (ParseObject ads : avisos) {

                                ads.deleteInBackground();
                            }

                        }

                    }
                });
                Intent intent = new Intent(PetInfo.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;

        }



        return super.onOptionsItemSelected(item);
    }
}
