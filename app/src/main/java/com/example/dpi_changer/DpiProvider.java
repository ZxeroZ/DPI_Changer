package com.example.dpi_changer;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;

public class DpiProvider extends ContentProvider {

    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        if (selectionArgs == null || selectionArgs.length == 0) return null;

        String packageName = selectionArgs[0];

        // Leemos el DPI de las preferencias de la app gestora
        SharedPreferences pref = getContext().getSharedPreferences("dpi_preferences", Context.MODE_PRIVATE);
        int dpi = pref.getInt(packageName, 0);

        // Enviamos el número de vuelta
        MatrixCursor cursor = new MatrixCursor(new String[]{"dpi"});
        cursor.addRow(new Object[]{dpi});
        return cursor;
    }

    // Métodos obligatorios que no necesitamos usar
    @Override public String getType(Uri uri) { return null; }
    @Override public Uri insert(Uri uri, ContentValues values) { return null; }
    @Override public int delete(Uri uri, String selection, String[] selectionArgs) { return 0; }
    @Override public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) { return 0; }
}