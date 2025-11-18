package com.example.vitality3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;

import android.content.Intent;
import android.database.Cursor;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class caloriasActivity extends AppCompatActivity {

    EditText edtCalorias;
    Button btnGuardar;
    ListView lvHistorial;
    BDVitality dbHelper;
    String usuarioEmail;
    ArrayList<String> historialList;
    ArrayAdapter<String> adapter;
    ImageButton Volver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calorias);

        edtCalorias = findViewById(R.id.edtCalorias);
        btnGuardar = findViewById(R.id.btnGuardar);
        lvHistorial = findViewById(R.id.lvHistorial);
        Volver = findViewById(R.id.back);

        dbHelper = new BDVitality(this);
        usuarioEmail = getIntent().getStringExtra("usuarioEmail");
        historialList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, historialList);
        lvHistorial.setAdapter(adapter);
        Prefs prefs = new Prefs(this);
        usuarioEmail = prefs.getEmail();


        mostrarHistorial();

        // Guardar calorías
        btnGuardar.setOnClickListener(v -> {
            String caloriasStr = edtCalorias.getText().toString().trim();

            if (caloriasStr.isEmpty()) {
                Toast.makeText(caloriasActivity.this, "Ingresa la cantidad de calorías", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                int cantidad = Integer.parseInt(caloriasStr);
                String fecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

                boolean registrado = dbHelper.insertarCalorias(usuarioEmail, fecha, cantidad);

                if (registrado) {
                    Toast.makeText(caloriasActivity.this, "Calorías registradas correctamente", Toast.LENGTH_SHORT).show();
                    edtCalorias.setText("");
                    mostrarHistorial();
                } else {
                    Toast.makeText(caloriasActivity.this, "Error al registrar las calorías", Toast.LENGTH_SHORT).show();
                }

            } catch (NumberFormatException e) {
                Toast.makeText(caloriasActivity.this, "Ingresa un número válido", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarHistorial() {
        historialList.clear();
        Cursor cursor = dbHelper.getCaloriasUsuario(usuarioEmail);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha"));
                int cantidad = cursor.getInt(cursor.getColumnIndexOrThrow("cantidad"));
                historialList.add(fecha + " → " + cantidad + " kcal");
            } while (cursor.moveToNext());
            cursor.close();
        } else {
            historialList.add("No hay registros aún.");
        }

        adapter.notifyDataSetChanged();

        Volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(caloriasActivity.this, homeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}