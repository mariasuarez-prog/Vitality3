package com.example.vitality3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class registrarseActivity extends AppCompatActivity {

    EditText edtNombre, edtEmailRegistro, edtPasswordRegistro, edtPasswordConfirmar;
    Button btnRegistrarse;
    ImageButton Volver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrarse);

        edtNombre = findViewById(R.id.edtNombre);
        edtEmailRegistro = findViewById(R.id.edtEmailRegistro);
        edtPasswordRegistro = findViewById(R.id.edtPasswordRegistro);
        edtPasswordConfirmar = findViewById(R.id.edtPasswordConfirmar);
        btnRegistrarse = findViewById(R.id.btnRegistrarse);
        Volver = findViewById(R.id.volver2);

        BDVitality dbHelper = new BDVitality(registrarseActivity.this);

        btnRegistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String nombre = edtNombre.getText().toString().trim();
                String email = edtEmailRegistro.getText().toString().trim();
                String pass = edtPasswordRegistro.getText().toString().trim();
                String pass2 = edtPasswordConfirmar.getText().toString().trim();

                // --- VALIDACIONES ---
                if (nombre.isEmpty() || email.isEmpty() || pass.isEmpty() || pass2.isEmpty()) {
                    Toast.makeText(registrarseActivity.this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Validar formato de correo electrónico
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(registrarseActivity.this, "Ingresa un correo electrónico válido", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Validar contraseñas iguales
                if (!pass.equals(pass2)) {
                    Toast.makeText(registrarseActivity.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Validar fuerza de la contraseña
                if (!esContrasenaSegura(pass)) {
                    Toast.makeText(registrarseActivity.this,
                            "La contraseña debe tener al menos 8 caracteres, una mayúscula, un número y un símbolo",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                // Validar si el correo ya existe
                if (dbHelper.checkUserEmail(email)) {
                    Toast.makeText(registrarseActivity.this, "Este correo ya está registrado", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Si todo está correcto -> registrar
                boolean insert = dbHelper.insertUser(nombre, email, pass);
                if (insert) {
                    Toast.makeText(registrarseActivity.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(registrarseActivity.this, personalizarActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(registrarseActivity.this, "Error en el registro", Toast.LENGTH_SHORT).show();
                }
            }
        });
        Volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(registrarseActivity.this, loginActivity.class);
                startActivity(intent);
            }

        });
    }
    private boolean esContrasenaSegura(String password) {
        // Mínimo 8 caracteres, al menos una mayúscula, un número y un símbolo especial
        String patron = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+=|<>?{}\\[\\]~-]).{8,}$";
        return password.matches(patron);
    }
}