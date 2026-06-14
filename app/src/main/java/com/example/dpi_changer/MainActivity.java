package com.example.dpi_changer;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<ApplicationInfo> appList;
    private PackageManager packageManager;
    private AppAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        packageManager = getPackageManager();
        appList = new ArrayList<>();

        // 1. Obtener las aplicaciones instaladas
        obtenerAplicaciones();

        // 2. Configurar la lista en pantalla (RecyclerView)
        RecyclerView recyclerView = findViewById(R.id.recyclerViewApps);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 3. Conectar el adaptador y escuchar el CLIC
        adapter = new AppAdapter(appList, packageManager, new AppAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ApplicationInfo appInfo) {
                // ¡Aquí es donde salta la magia al tocar la app!
                mostrarDialogoDpi(appInfo);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void obtenerAplicaciones() {
        List<ApplicationInfo> todasLasApps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo appInfo : todasLasApps) {
            // Filtro para mostrar solo apps de usuario (opcional)
            if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                appList.add(appInfo);
            }
        }
    }

    // Método para mostrar la ventanita donde escribes el DPI
    private void mostrarDialogoDpi(ApplicationInfo appInfo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String nombreApp = packageManager.getApplicationLabel(appInfo).toString();

        builder.setTitle("Configurar DPI");
        builder.setMessage("Ingresa el nuevo DPI para " + nombreApp + "\n(Deja vacío para restaurar el original)");

        // 1. Leer el DPI que ya estaba guardado
        SharedPreferences pref = getSharedPreferences("dpi_preferences", Context.MODE_PRIVATE);
        int dpiActual = pref.getInt(appInfo.packageName, 0);

        // 2. Crear el campo de texto numérico
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setGravity(Gravity.CENTER);

        // 3. Si hay un DPI guardado mayor a 0, mostrarlo en el cuadro
        if (dpiActual > 0) {
            input.setText(String.valueOf(dpiActual));
        }

        builder.setView(input);

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String dpiStr = input.getText().toString();
            int dpiValue = 0;

            if (!dpiStr.isEmpty()) {
                dpiValue = Integer.parseInt(dpiStr);
            }

            guardarDpiPorApp(appInfo.packageName, dpiValue);
            Toast.makeText(this, "DPI " + (dpiValue == 0 ? "restaurado" : "cambiado a " + dpiValue), Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    // Método que guarda y hace el archivo público para Xposed
    public void guardarDpiPorApp(String packageName, int dpiValue) {
        SharedPreferences pref = getSharedPreferences("dpi_preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(packageName, dpiValue);
        editor.apply();

        // TRUCO PARA XPOSED: Hacer que el archivo sea legible
        try {
            File prefsFile = new File(getApplicationInfo().dataDir + "/shared_prefs/dpi_preferences.xml");
            if (prefsFile.exists()) {
                prefsFile.setReadable(true, false);
                prefsFile.setExecutable(true, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}