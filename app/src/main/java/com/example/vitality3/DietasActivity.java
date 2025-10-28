package com.example.vitality3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class DietasActivity extends AppCompatActivity {

    // Declaraciones
    CardView cardDash, cardMediterranea, cardVegana;
    ImageButton Volver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Asegúrate de usar el nombre correcto del layout
        setContentView(R.layout.activity_dietas);
        Volver = findViewById(R.id.atras);


        // 1. Vinculación de las CardView por su ID
        cardDash = findViewById(R.id.card_dieta_dash);
        cardMediterranea = findViewById(R.id.card_dieta_mediterranea);
        cardVegana = findViewById(R.id.card_dieta_vegana);

        // 2. Asignación de Listeners para cada CardView

        // Listener para la Dieta DASH (Presión arterial)
        cardDash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirSitioWeb("https://www.mayoclinic.org/es/healthy-lifestyle/nutrition-and-healthy-eating/in-depth/dash-diet/art-20048456");
            }
        });

        // Listener para la Dieta Mediterránea
        cardMediterranea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirSitioWeb("https://www.healthline.com/nutrition/mediterranean-diet-meal-plan");
            }
        });

        // Listener para la Dieta Vegana
        cardVegana.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirSitioWeb("hhttp://assets.cardiosource.com/cardiosmart/csp/spanish/abk6159.pdf");
            }
        });
        Volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DietasActivity.this, homeActivity.class);
                startActivity(intent);
            }
        });
    }


    private void abrirSitioWeb(String url) {
        try {
            // Se asegura de que la URL contenga el esquema de protocolo
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "http://" + url;
            }

            // Crea un Intent con la acción ACTION_VIEW y la URI del sitio web
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            // Opcional: Mostrar un Toast si la URL no es válida o no hay navegador.
            // Toast.makeText(this, "No se pudo abrir el enlace.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }}
