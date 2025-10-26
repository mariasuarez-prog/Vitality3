package com.example.vitality3;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.graphics.drawable.Drawable;
import android.graphics.PorterDuff;
import android.util.Log;

public class homeActivity extends AppCompatActivity {

    TextView tvWelcome, tvCaloriasTotales;
    Button btnCalorias, btnCerrarSesion;
    ProgressBar progressCalorias;
    BDVitality dbHelper;
    String usuarioEmail;
    int metaDiaria = 2000; // Meta diaria de calorías

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        tvWelcome = findViewById(R.id.tvWelcome);
        tvCaloriasTotales = findViewById(R.id.tvCaloriasTotales);
        progressCalorias = findViewById(R.id.progressCalorias);
        btnCalorias = findViewById(R.id.btnCalorias);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        dbHelper = new BDVitality(this);
        usuarioEmail = getIntent().getStringExtra("usuarioEmail");

        mostrarNombreUsuario();
        mostrarCaloriasTotales();

        btnCalorias.setOnClickListener(v -> {
            Intent intent = new Intent(homeActivity.this, caloriasActivity.class);
            intent.putExtra("usuarioEmail", usuarioEmail);
            startActivity(intent);
        });

        btnCerrarSesion.setOnClickListener(v -> {
            Intent intent = new Intent(homeActivity.this, loginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void mostrarNombreUsuario() {
        Cursor cursor = dbHelper.getUsuarioPorEmail(usuarioEmail);
        if (cursor != null && cursor.moveToFirst()) {
            try {
                String nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"));
                tvWelcome.setText("Hola, " + nombre);
            } catch (IllegalArgumentException e) {
                Log.e("HomeActivity", "Columna 'nombre' no encontrada", e);
                tvWelcome.setText("Hola");
            }
            cursor.close();
        } else {
            tvWelcome.setText("Hola");
        }
    }

    private void mostrarCaloriasTotales() {
        Cursor cursor = dbHelper.getCaloriasUsuario(usuarioEmail);
        int total = 0;
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int cantidad = cursor.getInt(cursor.getColumnIndexOrThrow("cantidad"));
                total += cantidad;
            } while (cursor.moveToNext());
            cursor.close();
        }

        tvCaloriasTotales.setText("Calorías totales: " + total + " kcal");

        progressCalorias.setMax(metaDiaria);
        int progreso = total > metaDiaria ? metaDiaria : total;
        progressCalorias.setProgress(progreso);

        // Cambiar color según porcentaje
        float porcentaje = (float) total / metaDiaria;
        Drawable progressDrawable = progressCalorias.getProgressDrawable().mutate();

        if (porcentaje <= 1.0) { // dentro de la meta
            progressDrawable.setColorFilter(0xFF4CAF50, PorterDuff.Mode.SRC_IN); // verde
        } else { // sobrepasó la meta
            progressDrawable.setColorFilter(0xFFF44336, PorterDuff.Mode.SRC_IN); // rojo
        }
        progressCalorias.setProgressDrawable(progressDrawable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mostrarCaloriasTotales();
    }
}

