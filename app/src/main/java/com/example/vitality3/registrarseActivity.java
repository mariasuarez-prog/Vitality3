package com.example.vitality3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class registrarseActivity extends AppCompatActivity {

    private EditText edtNombre, edtEmailRegistro, edtPasswordRegistro, edtPasswordConfirmar;
    private ImageButton Volver;
    private EditText etEdad, etPeso, etAltura;
    private RadioGroup rgSexo;
    private CheckBox cbDiabetes, cbHipertension, cbTiroides, cbColesterol, cbCeliaca, cbLactosa, cbOtros;
    private Button btnGuardarDatos; // Es el botón final del formulario unificado (Registrarse y Guardar Datos)

    private BDVitality dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrarse);
        dbHelper = new BDVitality(registrarseActivity.this);
        initViews();
        btnGuardarDatos.setOnClickListener(v -> realizarRegistroCompleto());

        Volver.setOnClickListener(v -> {
            Intent intent = new Intent(registrarseActivity.this, loginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void initViews() {
        // VISTAS DE REGISTRO
        edtNombre = findViewById(R.id.edtNombre);
        edtEmailRegistro = findViewById(R.id.edtEmailRegistro);
        edtPasswordRegistro = findViewById(R.id.edtPasswordRegistro);
        edtPasswordConfirmar = findViewById(R.id.edtPasswordConfirmar);
        Volver = findViewById(R.id.volver2);

        // VISTAS DE PERSONALIZACION
        etEdad = findViewById(R.id.etEdad);
        etPeso = findViewById(R.id.etPeso);
        etAltura = findViewById(R.id.etAltura);
        rgSexo = findViewById(R.id.rgSexo);
        cbDiabetes = findViewById(R.id.cbDiabetes);
        cbHipertension = findViewById(R.id.cbHipertension);
        cbTiroides = findViewById(R.id.cbTiroides);
        cbColesterol = findViewById(R.id.cbColesterol);
        cbCeliaca = findViewById(R.id.cbCeliaca);
        cbLactosa = findViewById(R.id.cbLactosa);
        cbOtros = findViewById(R.id.cbOtros);
        btnGuardarDatos = findViewById(R.id.btnGuardarDatos);
    }


    private boolean validarDatosRegistro(String nombre, String email, String pass, String pass2) {
        if (nombre.isEmpty() || email.isEmpty() || pass.isEmpty() || pass2.isEmpty()) {
            Toast.makeText(this, "Todos los campos de registro son obligatorios", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validación de formato de email
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Ingresa un correo electrónico válido", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validación de contraseñas coincidentes
        if (!pass.equals(pass2)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validación de fuerza de la contraseña
        if (!esContrasenaSegura(pass)) {
            Toast.makeText(this,
                    "La contraseña debe tener al menos 8 caracteres, una mayúscula, un número y un símbolo",
                    Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private boolean esContrasenaSegura(String password) {
        // Patrón Regex: Mínimo 8 caracteres, al menos una mayúscula, un número y un símbolo especial
        String patron = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+=|<>?{}\\[\\]~-]).{8,}$";
        return password.matches(patron);
    }

    private boolean validarDatosPerfil() {
        // Valida que los campos de texto biométricos no estén vacíos
        if (etEdad.getText().toString().isEmpty() ||
                etPeso.getText().toString().isEmpty() ||
                etAltura.getText().toString().isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos biométricos (Edad, Peso, Altura).", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Valida que se haya seleccionado una opción de sexo
        if (rgSexo.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Por favor, selecciona tu sexo biológico.", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Verifica que las entradas sean numeros válidos
        try {
            Double.parseDouble(etPeso.getText().toString());
            Integer.parseInt(etEdad.getText().toString());
            Integer.parseInt(etAltura.getText().toString());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Asegúrate de que Peso, Edad y Altura sean números válidos.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private boolean guardarDatosPerfil(String email) {
       int edad = Integer.parseInt(etEdad.getText().toString());
        double peso = Double.parseDouble(etPeso.getText().toString());
        int altura = Integer.parseInt(etAltura.getText().toString());

        int selectedId = rgSexo.getCheckedRadioButtonId();
        String sexo = (selectedId == R.id.rbHombre) ? "Hombre" : "Mujer";

        StringBuilder enfermedadesSeleccionadas = new StringBuilder();

        if (cbDiabetes.isChecked()) { enfermedadesSeleccionadas.append("Diabetes, "); }
        if (cbHipertension.isChecked()) { enfermedadesSeleccionadas.append("Hipertensión, "); }
        if (cbTiroides.isChecked()) { enfermedadesSeleccionadas.append("Problemas de Tiroides, "); }
        if (cbColesterol.isChecked()) { enfermedadesSeleccionadas.append("Colesterol Alto, "); }
        if (cbCeliaca.isChecked()) { enfermedadesSeleccionadas.append("Enfermedad Celíaca, "); }
        if (cbLactosa.isChecked()) { enfermedadesSeleccionadas.append("Intolerancia a la Lactosa, "); }
        if (cbOtros.isChecked()) { enfermedadesSeleccionadas.append("Otras Condiciones, "); }

        String enfermedades;
        if (enfermedadesSeleccionadas.length() > 0) {
            enfermedades = enfermedadesSeleccionadas.substring(0, enfermedadesSeleccionadas.length() - 2);
        } else {
            enfermedades = "Ninguna";
        }

        return dbHelper.insertProfile(email, edad, peso, altura, sexo, enfermedades);
    }
    private void realizarRegistroCompleto() {
        Prefs prefs = new Prefs(this);
        String nombre = edtNombre.getText().toString().trim();
        String email = edtEmailRegistro.getText().toString().trim();
        String pass = edtPasswordRegistro.getText().toString().trim();
        String pass2 = edtPasswordConfirmar.getText().toString().trim();

        if (!validarDatosRegistro(nombre, email, pass, pass2)) {
            return;
        }

        if (dbHelper.checkUserEmail(email)) {
            Toast.makeText(this, "Este correo ya está registrado", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean userInsert = dbHelper.insertUser(nombre, email, pass);

        if (!userInsert) {
            Toast.makeText(this, "Error al crear la cuenta. Por favor, intenta de nuevo.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!validarDatosPerfil()) {
            return;
        }

        boolean profileInsert = guardarDatosPerfil(email);

        if (profileInsert) {
            prefs.saveEmail(email);
            Toast.makeText(this, "Registro completo y perfil guardado exitosamente.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(registrarseActivity.this, homeActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Error al guardar los datos de perfil. Contacta soporte.", Toast.LENGTH_SHORT).show();
        }
    }
}
