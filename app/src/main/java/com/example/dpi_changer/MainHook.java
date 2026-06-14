package com.example.dpi_changer;

import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.app.AndroidAppHelper;
import android.app.Application;
import android.database.Cursor;
import android.net.Uri;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class MainHook implements IXposedHookLoadPackage {

    // -1 significa que aún no hemos consultado la base de datos
    private int dpiCache = -1;

    @Override
    public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {

        // No nos inyectamos en nuestra propia app para evitar bucles infinitos
        if (lpparam.packageName.equals("com.example.dpi_changer")) {
            return;
        }

        XposedHelpers.findAndHookMethod(
                "android.content.res.ResourcesImpl",
                lpparam.classLoader,
                "updateConfiguration",
                Configuration.class,
                DisplayMetrics.class,
                "android.content.res.CompatibilityInfo",
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                        // Si no tenemos el DPI, usamos el ContentProvider para pedirlo
                        if (dpiCache == -1) {
                            Application app = AndroidAppHelper.currentApplication();
                            if (app != null) {
                                try {
                                    Uri uri = Uri.parse("content://com.example.dpi_changer.provider");
                                    Cursor cursor = app.getContentResolver().query(uri, null, null, new String[]{lpparam.packageName}, null);

                                    if (cursor != null && cursor.moveToFirst()) {
                                        dpiCache = cursor.getInt(0); // Guardamos el valor
                                        cursor.close();
                                    } else {
                                        dpiCache = 0;
                                    }
                                } catch (Exception e) {
                                    dpiCache = 0; // Si falla, usamos el normal
                                }
                            } else {
                                // La app apenas está naciendo, esperamos al siguiente ciclo
                                return;
                            }
                        }

                        // Si es 0, usamos el sistema por defecto
                        if (dpiCache <= 0) return;

                        // ¡Aplicamos la magia dinámicamente!
                        Configuration config = (Configuration) param.args[0];
                        if (config != null) {
                            config.densityDpi = dpiCache;
                        }

                        DisplayMetrics metrics = (DisplayMetrics) param.args[1];
                        if (metrics != null) {
                            metrics.densityDpi = dpiCache;
                            metrics.density = dpiCache * 0.00625f;
                        }
                    }
                }
        );
    }
}