package com.hakimi.ConnexionInscription.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.hakimi.ConnexionInscription.R;
import com.hakimi.ConnexionInscription.model.Contenair;

import java.util.List;

/**
 * Created by hakimi imed on 3/11/2017.
 */

public class ContainerAdapter extends ArrayAdapter<Contenair> {

    Context mCtx;
    int listLayoutRes;
    List<Contenair> contenairList;
    SQLiteDatabase mDatabase;


  public   EditText editTextName ;

    public AppCompatTextView textViewName2,textViewZones,textViewLocalisation,textViewJoiningDate,textViewVolume,textViewPoid,textViewEtat;
        public AppCompatButton buttonEdit,buttonDelete;

    public ContainerAdapter(Context mCtx, int listLayoutRes, List<Contenair> contenairList, SQLiteDatabase mDatabase) {
        super(mCtx, listLayoutRes, contenairList);

        this.mCtx = mCtx;
        this.listLayoutRes = listLayoutRes;
        this.contenairList = contenairList;
        this.mDatabase = mDatabase;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(listLayoutRes, null);

        final Contenair contenair = contenairList.get(position);


        textViewName2 = (AppCompatTextView) view.findViewById(R.id.textViewName2);
        textViewZones = (AppCompatTextView) view.findViewById(R.id.textViewZones);
        textViewLocalisation = (AppCompatTextView) view.findViewById(R.id.textViewLocalisation);
        textViewJoiningDate = (AppCompatTextView) view.findViewById(R.id.textViewJoiningDate);
        textViewVolume = (AppCompatTextView) view.findViewById(R.id.textViewVolume);
        textViewPoid = (AppCompatTextView) view.findViewById(R.id.textViewPoid);
        textViewEtat = (AppCompatTextView) view.findViewById(R.id.textViewEtat);




        textViewName2.setText(contenair.getName());
        textViewZones.setText(contenair.getZone());


        textViewJoiningDate.setText(contenair.getJoiningDate());
        textViewEtat.setText(contenair.getEtat());



        buttonDelete =(AppCompatButton) view.findViewById(R.id.buttonDeleteEmployee);
        buttonEdit =(AppCompatButton) view.findViewById(R.id.buttonEditEmployee);


        //adding a clicklistener to button
        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateEmployee(contenair);
            }
        });

        //the delete operation
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mCtx);
                builder.setTitle("Êtes-vous sûr?");
                builder.setPositiveButton("Supprimer", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String sql = "DELETE FROM containers WHERE id = ?";
                        mDatabase.execSQL(sql, new Integer[]{contenair.getId()});
                        reloadEmployeesFromDatabase();
                    }
                });
                builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        return view;
    }

    private void updateEmployee(final Contenair contenair) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mCtx);

        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.dialog_update_container, null);
        AlertDialog.Builder builder1 = builder.setView(view);


      final   EditText  editTextName = (EditText) view.findViewById(R.id.editTextName);
         final  EditText editTextVolume =(EditText) view.findViewById(R.id.editTextPoid);
       final   EditText editTextPoid =(EditText)  view.findViewById(R.id.editTextVolume);
        final Spinner spinnerZone =(Spinner) view.findViewById(R.id.spinnerZone);
        final Spinner spinnerEtat = (Spinner)view.findViewById(R.id.spinnerEtat);
        final EditText editTextLocalisation = (EditText) view.findViewById(R.id.editTextLocalisation);

        editTextName.setText(contenair.getName());

        final AlertDialog dialog = builder.create();
        dialog.show();

        view.findViewById(R.id.buttonUpdateEmployee).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editTextName.getText().toString().trim();
                String zone = spinnerZone.getSelectedItem().toString();
                String etat = spinnerEtat.getSelectedItem().toString();



                if (name.isEmpty()) {
                    editTextName.setError("le champ Nom n'est peut pas étre vide");
                    editTextName.requestFocus();
                    return;
                }





                String sql = "UPDATE containers \n" +
                        "SET name = ?, \n" +
                        "zone = ?, \n" +
                        "etat = ? \n" +
                        "WHERE id = ?;\n";

                mDatabase.execSQL(sql, new String[]{name, zone,etat, String.valueOf(contenair.getId())});
                Toast.makeText(mCtx, "Conteneur modifié", Toast.LENGTH_SHORT).show();
                reloadEmployeesFromDatabase();

                dialog.dismiss();
            }
        });
    }

    private void reloadEmployeesFromDatabase() {
        Cursor cursorContenairs = mDatabase.rawQuery("SELECT * FROM containers", null);
        if (cursorContenairs.moveToFirst()) {
            contenairList.clear();
            do {
                contenairList.add(new Contenair(
                        cursorContenairs.getInt(0),
                        cursorContenairs.getString(1),
                        cursorContenairs.getString(2),
                        cursorContenairs.getString(3),
                        cursorContenairs.getString(4)
                ));
            } while (cursorContenairs.moveToNext());
        }
        cursorContenairs.close();
        notifyDataSetChanged();
    }

}
