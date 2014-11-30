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
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;


public class Lista_mascotas extends ListActivity {
    private static final String TAG = Lista_mascotas.class.getSimpleName();
    protected ParseUser mCurrentUser;
    protected Button mAgregar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_lista_mascotas);
        mCurrentUser = ParseUser.getCurrentUser();
        setProgressBarIndeterminateVisibility(true);
        if (isNetworkAvaible()){
            ListarInfo();
        }
        else {
            Toast.makeText(Lista_mascotas.this, R.string.error_conexion, Toast.LENGTH_LONG).show();
        }
        mAgregar = (Button)findViewById(R.id.button_edit_pet);
        mAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Lista_mascotas.this, RegistroMascota.class);
                startActivity(intent);


            }
        });
    }

    private void ListarInfo() {
        final ParseQuery<ParseObject> query = ParseQuery.getQuery("Pets");
        query.whereEqualTo("parent", mCurrentUser);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> pets, ParseException e) {
                setProgressBarIndeterminateVisibility(false);
                if (e == null) {

                    String[] PetsNames = new String[pets.size()];
                    int i = 0;
                    for (ParseObject pet : pets) {
                        PetsNames[i] = pet.getString("name");
                        i++;
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                            getListView().getContext(),
                            android.R.layout.simple_list_item_1, PetsNames);
                    setListAdapter(adapter);

                }

                else {
                    Log.e(TAG, getString(R.string.error_conexion), e);
                    AlertDialog.Builder builder = new AlertDialog.Builder(Lista_mascotas.this);
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
    public void onListItemClick (ListView l, View v,int position, long id){
        super.onListItemClick(l, v, position, id);

        String select = l.getItemAtPosition(position).toString();
        Intent intent = new Intent(Lista_mascotas.this, PetInfo.class);
        intent.putExtra("Pet", select);
        startActivity(intent);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_lista_mascotas, menu);
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
