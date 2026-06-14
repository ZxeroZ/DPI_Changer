package com.example.dpi_changer;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class AppAdapter extends RecyclerView.Adapter<AppAdapter.AppViewHolder> {

    private List<ApplicationInfo> appList;
    private PackageManager packageManager;
    private OnItemClickListener listener;

    // Interfaz para detectar clics en cada tarjeta
    public interface OnItemClickListener {
        void onItemClick(ApplicationInfo appInfo);
    }

    public AppAdapter(List<ApplicationInfo> appList, PackageManager packageManager, OnItemClickListener listener) {
        this.appList = appList;
        this.packageManager = packageManager;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_app, parent, false);
        return new AppViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppViewHolder holder, int position) {
        ApplicationInfo appInfo = appList.get(position);

        // Cargamos el nombre real, el paquete y el ícono
        holder.tvAppName.setText(packageManager.getApplicationLabel(appInfo));
        holder.tvAppPackage.setText(appInfo.packageName);
        holder.imgAppIcon.setImageDrawable(packageManager.getApplicationIcon(appInfo));

        // Evento al tocar la tarjeta
        holder.itemView.setOnClickListener(v -> listener.onItemClick(appInfo));
    }

    @Override
    public int getItemCount() {
        return appList.size();
    }

    // La clase que enlaza los elementos visuales del XML
    public static class AppViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAppIcon;
        TextView tvAppName, tvAppPackage, tvAppDpi;

        public AppViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAppIcon = itemView.findViewById(R.id.imgAppIcon);
            tvAppName = itemView.findViewById(R.id.tvAppName);
            tvAppPackage = itemView.findViewById(R.id.tvAppPackage);
            tvAppDpi = itemView.findViewById(R.id.tvAppDpi);
        }
    }
}