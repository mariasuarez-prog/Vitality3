package com.example.vitality3; // ¡Verifica que este paquete sea correcto!

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

public class personalizarActivity extends AppCompatActivity {

    private EditText etEdad, etPeso, etAltura;
    private RadioGroup rgSexo;

    // TODAS LAS CHECKBOXES DECLARADAS
    private CheckBox cbDiabetes, cbHipertension, cbTiroides, cbColesterol, cbCeliaca, cbLactosa, cbOtros;

    private Button btnGuardarDatos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personalizar);

        // 1. Inicializar todas las vistas
        initViews();

        // 2. Configurar el evento de clic del botón
        btnGuardarDatos.setOnClickListener(v -> guardarDatos());
    }

    private void initViews() {
        // Datos Biométricos
        etEdad = findViewById(R.id.etEdad);
        etPeso = findViewById(R.id.etPeso);
        etAltura = findViewById(R.id.etAltura);
        rgSexo = findViewById(R.id.rgSexo);

        // Condiciones de Salud (TODOS INICIALIZADOS)
        cbDiabetes = findViewById(R.id.cbDiabetes);
        cbHipertension = findViewById(R.id.cbHipertension);
        cbTiroides = findViewById(R.id.cbTiroides);
        cbColesterol = findViewById(R.id.cbColesterol);
        cbCeliaca = findViewById(R.id.cbCeliaca);
        cbLactosa = findViewById(R.id.cbLactosa);
        cbOtros = findViewById(R.id.cbOtros);

        // Botón
        btnGuardarDatos = findViewById(R.id.btnGuardarDatos);
    }

    private void guardarDatos() {
        // Validación: verifica que los campos obligatorios no estén vacíos
        if (!validarEntradas()) {
            return;
        }

        // --- RECOLECCIÓN DE DATOS ---

        int edad = Integer.parseInt(etEdad.getText().toString());
        double peso = Double.parseDouble(etPeso.getText().toString());
        double altura = Double.parseDouble(etAltura.getText().toString());
        boolean esHombre = rgSexo.getCheckedRadioButtonId() == R.id.rbHombre;
        String sexo = esHombre ? "Hombre" : "Mujer";

        // Obtener Enfermedades Seleccionadas (como una cadena de texto)
        StringBuilder enfermedadesSeleccionadas = new StringBuilder();

        // Añade todas las condiciones si están marcadas
        if (cbDiabetes.isChecked()) {
            enfermedadesSeleccionadas.append("Diabetes, ");
        }
        if (cbHipertension.isChecked()) {
            enfermedadesSeleccionadas.append("Hipertensión, ");
        }
        if (cbTiroides.isChecked()) {
            enfermedadesSeleccionadas.append("Problemas de Tiroides, ");
        }
        if (cbColesterol.isChecked()) {
            enfermedadesSeleccionadas.append("Colesterol Alto, ");
        }
        if (cbCeliaca.isChecked()) {
            enfermedadesSeleccionadas.append("Enfermedad Celíaca, ");
        }
        if (cbLactosa.isChecked()) {
            enfermedadesSeleccionadas.append("Intolerancia a la Lactosa, ");
        }
        if (cbOtros.isChecked()) {
            enfermedadesSeleccionadas.append("Otras Condiciones, ");
        }

        // Limpia la última coma y espacio de la cadena de enfermedades
        String enfermedades;
        if (enfermedadesSeleccionadas.length() > 0) {
            enfermedades = enfermedadesSeleccionadas.substring(0, enfermedadesSeleccionadas.length() - 2);
        } else {
            enfermedades = "Ninguna";
        }

        // --- LÓGICA DE BASE DE DATOS PENDIENTE ---
        // Aquí debes guardar los datos en Room.

        // Muestra un Toast final de prueba
        Toast.makeText(this,
                "Datos listos para la BD:\n" +
                        "Edad: " + edad + ", Peso: " + peso + " kg, Altura: " + altura + " cm\n" +
                        "Enfermedades: " + enfermedades,
                Toast.LENGTH_LONG).show();
        Intent intent = new Intent(personalizarActivity.this, homeActivity.class);
        startActivity(intent);
    }

    private boolean validarEntradas() {
        // Valida que los campos de texto no estén vacíos
        if (etEdad.getText().toString().isEmpty() ||
                etPeso.getText().toString().isEmpty() ||
                etAltura.getText().toString().isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos biométricos.", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Valida que se haya seleccionado una opción de sexo
        if (rgSexo.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Por favor, selecciona tu sexo biológico.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}