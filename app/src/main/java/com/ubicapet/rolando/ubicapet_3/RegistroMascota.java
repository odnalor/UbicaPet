package com.ubicapet.rolando.ubicapet_3;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;


public class RegistroMascota extends Activity {
    public static final String TAG = RegistroMascota.class.getSimpleName();
    public static final int TAKE_PHOTO_REQUEST = 0;
    public static final int PICK_PHOTO_REQUEST = 1;
    public static final int MEDIA_TYPE_IMAGE = 4;
    protected Uri mMediaUri;
    protected ParseUser mCurrentUser;
    protected ParseRelation<ParseObject> mPetRelation;
    protected EditText mName;
    protected Spinner spinner, spiner2;
    protected EditText mColor, mSize, mCharacteristic;
    protected Button mSignUpButton, Camara;
    protected String sex, mRace, race;


    protected DialogInterface.OnClickListener mDialogListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {

            switch (which){
                case 0: // elegir foto
                    Intent sacarFotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    mMediaUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                    if (mMediaUri == null){
                        //mensaje de error
                        Toast.makeText(RegistroMascota.this, getString(R.string.error_memoria), Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        sacarFotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
                        startActivityForResult(sacarFotoIntent, TAKE_PHOTO_REQUEST);
                    }
                    break;
                case 1: // tomar foto
                    Intent choosePhotoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    choosePhotoIntent.setType("image/*");
                    startActivityForResult(choosePhotoIntent, PICK_PHOTO_REQUEST);
                    break;
            }
        }

        private Uri getOutputMediaFileUri(int mediaTypeImage) {
            //revisar SD
            if(isExternalStorageAvaible()) {
                //get the uri
                String appName = RegistroMascota.this.getString(R.string.app_name);

                File mediaStorage = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), appName);

                if(! mediaStorage.exists()){
                    if (!mediaStorage.mkdirs()){
                        Log.e(TAG, "error al crear directorio");
                        return null;
                    }
                }


                File mediaFile;
                String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

                if (mediaTypeImage == MEDIA_TYPE_IMAGE){
                    mediaFile = new File(mediaStorage.getPath() + File.separator + "IMG_" + timestamp + ".jpg");

                }
                else {
                    return null;
                }

                Log.d(TAG, "File: "+ Uri.fromFile(mediaFile));

                return Uri.fromFile(mediaFile);


            }
            else {

                return null;
            }
        }

        private  boolean isExternalStorageAvaible(){
            String state = Environment.getExternalStorageState();
            if(state.equals(Environment.MEDIA_MOUNTED)){
                return true;
            }
            else {
                return false;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        mCurrentUser = ParseUser.getCurrentUser();
        setContentView(R.layout.activity_registro_mascota);

        Asociacion();

        mSignUpButton = (Button)findViewById(R.id.button_register);
        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isNetworkAvaible()) {
                    RegistrarInformacion();
                }
                else {
                    Toast.makeText(RegistroMascota.this, R.string.error_conexion, Toast.LENGTH_LONG).show();
                }
            }


        });

    }

    private void RegistrarInformacion() {
        String name = mName.getText().toString();
        String color = mColor.getText().toString();
        String size = mSize.getText().toString();
        String characteristic = mCharacteristic.getText().toString();


        name = name.trim();
        characteristic = characteristic.trim();
        race = mRace.trim();
        sex = sex.trim();
        color = color.trim();
        size = size.trim();

        if (name.isEmpty() || characteristic.isEmpty() || race.isEmpty() || sex.isEmpty() || color.isEmpty() || size.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(RegistroMascota.this);
            builder.setMessage(R.string.error_signup)
                    .setTitle(R.string.login_error_title)
                    .setPositiveButton(android.R.string.ok, null);
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            // crear Mascota
            setProgressBarIndeterminateVisibility(true);
            ParseObject newPet = new ParseObject("Pets");
            newPet.put("name", name);
            newPet.put("Char", characteristic);
            newPet.put("Race", race);
            newPet.put("Sex", sex);
            newPet.put("Color", color);
            newPet.put("Size", size);
            newPet.put("parent", mCurrentUser);
            newPet.put("Estado","En casa" );

            newPet.saveInBackground();

            newPet.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject parseObject, ParseException e) {
                    if (e == null){
                        Toast toast = Toast.makeText(RegistroMascota.this, getString(R.string.registro_exito), Toast.LENGTH_LONG);
                        toast.show();
                    }

                }
            });

            setProgressBarIndeterminateVisibility(false);

            Intent intent = new Intent(RegistroMascota.this, Lista_mascotas.class);
            startActivity(intent);
            finish();

        }
    }

    private void Asociacion() {
        mName = (EditText)findViewById(R.id.nameField);
        spiner2 = (Spinner) findViewById(R.id.spinner_raza);
        ArrayAdapter adapter2 = ArrayAdapter.createFromResource(this, R.array.Raza_perro, android.R.layout.simple_spinner_item);
        spiner2.setAdapter(adapter2);
        spiner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mRace=parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner = (Spinner) findViewById(R.id.spinner_select_sex);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.sexo, android.R.layout.simple_spinner_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                sex = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        mColor = (EditText)findViewById(R.id.edit_color);
        mSize = (EditText)findViewById(R.id.edit_size);
        mCharacteristic = (EditText)findViewById(R.id.edit_characteristic);
        Camara = (Button)findViewById(R.id.button_photograph);
        Camara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RegistroMascota.this);
                builder.setItems(R.array.camara, mDialogListener);
                AlertDialog dialog = builder.create();
                dialog.show();
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
        getMenuInflater().inflate(R.menu.menu_registro_mascota, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            //agregar a galeria
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(mMediaUri);
            sendBroadcast(mediaScanIntent);

        }
        else  if (resultCode != RESULT_CANCELED){
            Toast.makeText(this,R.string.login_error_title, Toast.LENGTH_LONG).show();

        }
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
