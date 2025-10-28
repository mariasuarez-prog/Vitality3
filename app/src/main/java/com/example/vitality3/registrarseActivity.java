package com.example.vitality3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Toast;

// Esta actividad maneja el registro de usuario y la personalización del perfil
public class registrarseActivity extends AppCompatActivity {

    // Vistas de REGISTRO (Campos de Usuario)
    private EditText edtNombre, edtEmailRegistro, edtPasswordRegistro, edtPasswordConfirmar;
    private ImageButton Volver;

    // Vistas de PERSONALIZACIÓN (Datos Biométricos y de Salud)
    private EditText etEdad, etPeso, etAltura;
    private RadioGroup rgSexo;
    private CheckBox cbDiabetes, cbHipertension, cbTiroides, cbColesterol, cbCeliaca, cbLactosa, cbOtros;
    private Button btnGuardarDatos; // Es el botón final del formulario unificado (Registrarse y Guardar Datos)

    private BDVitality dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Asumiendo que el layout unificado se llama activity_registro_personalizacion.xml
        setContentView(R.layout.activity_registrarse);

        // 1. Inicializar Base de Datos
        dbHelper = new BDVitality(registrarseActivity.this);

        // 2. Inicializar todas las vistas del layout unificado
        initViews();

        // 3. Configurar listeners
        // El botón final realiza todo el proceso de registro y perfil
        btnGuardarDatos.setOnClickListener(v -> realizarRegistroCompleto());

        // Botón de volver
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

        // VISTAS DE PERSONALIZACIÓN
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

        // BOTÓN FINAL (con ID btnGuardarDatos en el XML)
        btnGuardarDatos = findViewById(R.id.btnGuardarDatos);
    }

    /**
     * Ejecuta la validación del formulario de registro y perfil, y si es exitoso,
     * registra al usuario y guarda sus datos de salud.
     */
    private void realizarRegistroCompleto() {
        // --- 1. OBTENER DATOS DE REGISTRO ---
        String nombre = edtNombre.getText().toString().trim();
        String email = edtEmailRegistro.getText().toString().trim();
        String pass = edtPasswordRegistro.getText().toString().trim();
        String pass2 = edtPasswordConfirmar.getText().toString().trim();

        // --- 2. VALIDACIÓN DE DATOS DE REGISTRO ---
        if (!validarDatosRegistro(nombre, email, pass, pass2)) {
            return;
        }

        // --- 3. REGISTRAR USUARIO (Tabla 'users') ---
        if (dbHelper.checkUserEmail(email)) {
            Toast.makeText(this, "Este correo ya está registrado", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean userInsert = dbHelper.insertUser(nombre, email, pass);

        if (!userInsert) {
            Toast.makeText(this, "Error al crear la cuenta. Por favor, intenta de nuevo.", Toast.LENGTH_SHORT).show();
            return;
        }

        // --- 4. VALIDACIÓN DE DATOS DE PERFIL ---
        if (!validarDatosPerfil()) {
            // Nota: El usuario ya está registrado en la tabla 'users'.
            // En un sistema robusto, se debería manejar un rollback o la eliminación del usuario si falla el perfil.
            return;
        }

        // --- 5. GUARDAR DATOS DE PERFIL (Tabla 'perfil') ---
        boolean profileInsert = guardarDatosPerfil(email);

        // --- 6. RESULTADO FINAL ---
        if (profileInsert) {
            Toast.makeText(this, "Registro completo y perfil guardado exitosamente.", Toast.LENGTH_LONG).show();
            // Redirigir a la actividad principal (homeActivity)
            Intent intent = new Intent(registrarseActivity.this, homeActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Error al guardar los datos de perfil. Contacta soporte.", Toast.LENGTH_SHORT).show();
        }
    }

    // --- MÉTODOS DE UTILIDAD Y VALIDACIÓN ---

    private boolean validarDatosRegistro(String nombre, String email, String pass, String pass2) {
        // Validación de campos obligatorios
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

        // Verifica que las entradas sean números válidos
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
        // Se usa Double.parseDouble para aceptar decimales en el peso
        double peso = Double.parseDouble(etPeso.getText().toString());
        int altura = Integer.parseInt(etAltura.getText().toString());

        int selectedId = rgSexo.getCheckedRadioButtonId();
        String sexo = (selectedId == R.id.rbHombre) ? "Hombre" : "Mujer";

        // Concatena las enfermedades seleccionadas en una cadena de texto
        StringBuilder enfermedadesSeleccionadas = new StringBuilder();

        if (cbDiabetes.isChecked()) { enfermedadesSeleccionadas.append("Diabetes, "); }
        if (cbHipertension.isChecked()) { enfermedadesSeleccionadas.append("Hipertensión, "); }
        if (cbTiroides.isChecked()) { enfermedadesSeleccionadas.append("Problemas de Tiroides, "); }
        if (cbColesterol.isChecked()) { enfermedadesSeleccionadas.append("Colesterol Alto, "); }
        if (cbCeliaca.isChecked()) { enfermedadesSeleccionadas.append("Enfermedad Celíaca, "); }
        if (cbLactosa.isChecked()) { enfermedadesSeleccionadas.append("Intolerancia a la Lactosa, "); }
        if (cbOtros.isChecked()) { enfermedadesSeleccionadas.append("Otras Condiciones, "); }

        // Limpia la última coma y espacio, si existe
        String enfermedades;
        if (enfermedadesSeleccionadas.length() > 0) {
            enfermedades = enfermedadesSeleccionadas.substring(0, enfermedadesSeleccionadas.length() - 2);
        } else {
            enfermedades = "Ninguna";
        }

        // Llama al método de la BD para insertar el perfil
        return dbHelper.insertProfile(email, edad, peso, altura, sexo, enfermedades);
    }
}
