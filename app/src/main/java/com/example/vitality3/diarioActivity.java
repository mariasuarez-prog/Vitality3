package com.example.vitality3;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import android.widget.ImageButton;
import android.widget.TextView;
import android.graphics.drawable.Drawable;
import android.graphics.PorterDuff;
import android.util.Log;

public class diarioActivity extends AppCompatActivity {

    TextView tvWelcome, tvCaloriasTotales;
    Button btnCalorias, btnCerrarSesion;
    LinearProgressIndicator progressCalorias;
    BDVitality dbHelper;
    String usuarioEmail;
    ImageButton Volver;
    int metaDiaria = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diario);
        progressCalorias = findViewById(R.id.progressCalorias);
        Volver = findViewById(R.id.atras);
        progressCalorias.setIndeterminate(false);
        progressCalorias.setMax(2000);

        tvWelcome = findViewById(R.id.tvWelcome);
        tvCaloriasTotales = findViewById(R.id.tvCaloriasTotales);
        progressCalorias = findViewById(R.id.progressCalorias);
        btnCalorias = findViewById(R.id.btnCalorias);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        dbHelper = new BDVitality(this);
        usuarioEmail = getIntent().getStringExtra("usuarioEmail");
        Prefs prefs = new Prefs(this);
        usuarioEmail = prefs.getEmail();

        mostrarNombreUsuario();
        mostrarCaloriasTotales();

        btnCalorias.setOnClickListener(v -> {
            Intent intent = new Intent(diarioActivity.this, caloriasActivity.class);
            intent.putExtra("usuarioEmail", usuarioEmail);
            startActivity(intent);
        });

        btnCerrarSesion.setOnClickListener(v -> {
            Intent intent = new Intent(diarioActivity.this, loginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        Volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(diarioActivity.this, homeActivity.class);
                startActivity(intent);
                finish();
            }
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

        if (total <= 0) {
            progressCalorias.setIndicatorColor(Color.parseColor("#BDBDBD")); // gris
        } else if (porcentaje < 1.0f) {
            progressCalorias.setIndicatorColor(Color.parseColor("#4CAF50")); // verde
        } else {
            progressCalorias.setIndicatorColor(Color.parseColor("#F44336")); // rojo
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        mostrarCaloriasTotales();
    }
}