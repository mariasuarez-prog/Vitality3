package com.example.vitality3;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import android.widget.TextView;
import android.graphics.drawable.Drawable;
import android.graphics.PorterDuff;
import android.util.Log;

public class homeActivity extends AppCompatActivity {

    TextView tvWelcome, tvCaloriasTotales;
    Button btnCalorias, btnCerrarSesion;
    LinearProgressIndicator progressCalorias;
    BDVitality dbHelper;
    String usuarioEmail;
    int metaDiaria = 2000; // Meta diaria de calorías

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        progressCalorias = findViewById(R.id.progressCalorias);
        progressCalorias.setIndeterminate(false);
        progressCalorias.setMax(2000);

        progressCalorias.setProgress(5);    // debería verse casi vacío
        Log.d("HomeActivityTest", "Progress después de set 5: " + progressCalorias.getProgress());

        progressCalorias.setProgress(500);  // debería verse ~25%
        Log.d("HomeActivityTest", "Progress después de set 500: " + progressCalorias.getProgress());

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
        int total = dbHelper.getTotalCaloriasUsuario(usuarioEmail);
        tvCaloriasTotales.setText("Calorías totales: " + total + " kcal");

        progressCalorias.setMax(metaDiaria);
        int progreso = Math.min(total, metaDiaria);
        progressCalorias.setProgressCompat(progreso, true);

        float porcentaje = (float) total / metaDiaria;

        // Cambiar color dinámicamente
        if (total <= 0) {
            progressCalorias.setIndicatorColor(Color.parseColor("#BDBDBD")); // gris
        } else if (porcentaje < 1.0f) {
            progressCalorias.setIndicatorColor(Color.parseColor("#4CAF50")); // verde
        } else {
            progressCalorias.setIndicatorColor(Color.parseColor("#F44336")); // rojo
        }

        Log.d("HomeActivity", "Progreso: " + progreso + " / " + metaDiaria + " (" + porcentaje + ")");
    }


    @Override
    protected void onResume() {
        super.onResume();
        mostrarCaloriasTotales();
    }
}

