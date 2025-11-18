package com.example.vitality3;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.google.android.material.card.MaterialCardView;

public class homeActivity extends AppCompatActivity {

    private MaterialCardView cardDiario;
    private MaterialCardView cardDietas;
    BDVitality dbHelper;
    String usuarioEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Prefs prefs = new Prefs(this);
        usuarioEmail = prefs.getEmail();


        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        cardDiario = findViewById(R.id.card_diario);
        cardDietas = findViewById(R.id.card_dietas);

        dbHelper = new BDVitality(this);
        usuarioEmail = getIntent().getStringExtra("usuarioEmail");

        cardDiario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // A DiarioActivity
                Intent intent = new Intent(homeActivity.this, diarioActivity.class);
                intent.putExtra("usuarioEmail", usuarioEmail);
                startActivity(intent);
            }
        });

        cardDietas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(homeActivity.this, DietasActivity.class);
                startActivity(intent);
            }
        });
    }
}