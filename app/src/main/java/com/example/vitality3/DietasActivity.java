package com.example.vitality3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class DietasActivity extends AppCompatActivity {

    CardView cardDash, cardMediterranea, cardVegana;
    ImageButton Volver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dietas);
        Volver = findViewById(R.id.atras);

        cardDash = findViewById(R.id.card_dieta_dash);
        cardMediterranea = findViewById(R.id.card_dieta_mediterranea);
        cardVegana = findViewById(R.id.card_dieta_vegana);


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

        // Listener para la Dieta TLC
        cardVegana.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirSitioWeb("https://my.clevelandclinic.org/health/articles/16867-cholesterol--nutrition-tlc");
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
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "http://" + url;
            }

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }}
