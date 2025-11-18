package com.example.vitality3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BDVitality extends SQLiteOpenHelper {

    public static final String DBNAME = "vitality.db";

    public BDVitality(Context context) {
        super(context, DBNAME, null, 3);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //TABLA USUARIOS
        db.execSQL("CREATE TABLE users(" +
                "nombre TEXT," +
                "email TEXT PRIMARY KEY," +
                "password TEXT)");

        // TABLA PERFIL/DATOS PERSONALES
        db.execSQL("CREATE TABLE perfil(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT,"+
                "email TEXT UNIQUE," +
                "edad INTEGER," +
                "peso REAL," +
                "altura INTEGER," +
                "sexo TEXT," +
                "condiciones TEXT," +
                "FOREIGN KEY(email) REFERENCES users(email) ON DELETE CASCADE)");

        // TABLA CALORÍAS
        db.execSQL("CREATE TABLE calorias(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "email TEXT," +
                "fecha TEXT," +
                "cantidad INTEGER,"+
                "FOREIGN KEY(email) REFERENCES users(email) ON DELETE CASCADE)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Eliminar tablas si existe versión antigua
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS perfil");
        db.execSQL("DROP TABLE IF EXISTS calorias");
        onCreate(db);
    }
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    public boolean insertUser(String nombre, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nombre", nombre);
        values.put("email", email);
        values.put("password", password);
        long result = db.insert("users", null, values);
        return result != -1;
    }

    // Verificar si email existe
    public boolean checkUserEmail(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE email=?", new String[]{email});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // Verificar email y password
    public boolean checkEmailPassword(String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE email=? AND password=?", new String[]{email, password});
        boolean valid = cursor.getCount() > 0;
        cursor.close();
        return valid;
    }

    // Obtener usuario por email
    public Cursor getUsuarioPorEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM users WHERE email = ?", new String[]{email});
    }

    //Inserta los datos de perfil y salud del usuario.
    public boolean insertProfile(String email, int edad, double peso, int altura, String sexo, String condiciones) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("email", email);
        values.put("edad", edad);
        values.put("peso", peso);
        values.put("altura", altura);
        values.put("sexo", sexo);
        values.put("condiciones", condiciones);

        long result = db.insert("perfil", null, values);
        return result != -1;
    }

    // MÉTODOS CALORÍAS

    // Insertar calorías
    public boolean insertarCalorias(String email, String fecha, int cantidad) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("email", email);
        values.put("fecha", fecha);
        values.put("cantidad", cantidad);
        long resultado = db.insert("calorias", null, values);
        return resultado != -1;
    }

    public Cursor getCaloriasUsuario(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM calorias WHERE email = ? ORDER BY id DESC", new String[]{email});
    }

    // Obtener calorías por usuario
    public int getTotalCaloriasUsuario(String email){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(cantidad) AS total FROM calorias WHERE email = ?", new String[]{email});
        int total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getInt(cursor.getColumnIndexOrThrow("total"));
        }
        cursor.close();
        return total;
    }
}
