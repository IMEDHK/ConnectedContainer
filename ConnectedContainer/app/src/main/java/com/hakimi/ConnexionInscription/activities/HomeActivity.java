package com.hakimi.ConnexionInscription.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.hakimi.ConnexionInscription.R;
import com.hakimi.ConnexionInscription.adapters.ContainerAdapter;
import com.hakimi.ConnexionInscription.model.Contenair;
import com.hakimi.ConnexionInscription.sql.DatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String DATABASE_NAME = "mycontenairdatabase";
    TextView textViewViewContainers;
    EditText editTextName, editTextLocalisation,editTextVolume,editTextPoid;
    Spinner spinnerZone,spinnerEtat;
    SQLiteDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewViewContainers = (TextView) findViewById(R.id.textViewViewContainers);
        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextLocalisation = (EditText) findViewById(R.id.editTextLocalisation);
        spinnerZone = (Spinner) findViewById(R.id.spinnerZone);
        spinnerEtat = (Spinner) findViewById(R.id.spinnerEtat);
        editTextVolume = (EditText) findViewById(R.id.editTextVolume);
        editTextPoid = (EditText) findViewById(R.id.editTextPoid);

        textViewViewContainers.setOnClickListener(this);
        findViewById(R.id.buttonUltrason).setOnClickListener(this);
        findViewById(R.id.buttonAddContainer).setOnClickListener(this);
        textViewViewContainers.setOnClickListener(this);

        //creating a database
        mDatabase = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
        createContainerTable();

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("SVP activer votre GPS")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("Retour", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();




    }


    //this method will create the table
    //as we are going to call this method everytime we will launch the application
    //I have added IF NOT EXISTS to the SQL
    //so it will only create the table when the table is not already created
    private void createContainerTable() {
        mDatabase.execSQL(
                "CREATE TABLE IF NOT EXISTS containers (\n" +
                        "    id INTEGER NOT NULL CONSTRAINT containers_pk PRIMARY KEY AUTOINCREMENT,\n" +
                        "    name varchar(200) NOT NULL,\n" +
                        "    zone varchar(200) NOT NULL,\n" +
                        "    joiningdate datetime NOT NULL,\n" +
                        "    localisation varchar(200) NOT NULL,\n" +
                        "    volume double NOT NULL,\n" +
                        "    poid double NOT NULL,\n" +
                        "    etat varchar(200) NOT NULL\n" +
                        ");"
        );
    }

    //this method will validate the name and salary
    //dept does not need validation as it is a spinner and it cannot be empty
    private boolean inputsAreCorrect(String name) {
        if (name.isEmpty()) {
            editTextName.setError("SVP entrer le nome de conteneur");
            editTextName.requestFocus();
            return false;
        }
        return true;
    }



    //In this method we will do the create operation
    private void addContenair() {

        String name = editTextName.getText().toString().trim();
        String zone = spinnerZone.getSelectedItem().toString();
        String localisation = editTextLocalisation.getText().toString().trim();
        String volume = editTextVolume.getText().toString().trim();
        String poid = editTextPoid.getText().toString().trim();
        String etat = spinnerEtat.getSelectedItem().toString();


        //getting the current time for joining date
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-mm-yyyy hh:mm:ss");
        String joiningDate = sdf.format(cal.getTime());

        //validating the inptus
        if (inputsAreCorrect(name)) {

            String insertSQL = "INSERT INTO containers \n" +
                    "(name, zone, joiningdate, localisation,volume,poid,etat)\n" +
                    "VALUES \n" +
                    "(?, ?, ?, ?, ?, ?, ?);";

            //using the same method execsql for inserting values
            //this time it has two parameters
            //first is the sql string and second is the parameters that is to be binded with the query
            mDatabase.execSQL(insertSQL, new String[]{name, zone, joiningDate, localisation,volume,poid,etat});


            Toast.makeText(this, "Conteneur ajouté avec succé", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonAddContainer:

                addContenair();

                break;
            case R.id.textViewViewContainers:

                startActivity(new Intent(this, ContainerActivity.class));

                break;



            case R.id.buttonUltrason:

                startActivity(new Intent(this, SensorActivity.class));

                break;
        }
    }

}
