package com.example.recyclerviewcard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SiswaAdapter extends RecyclerView.Adapter<SiswaAdapter.SiswaViewHolder> {

    private Context context;
    private List<Siswa> siswaList;

    public SiswaAdapter(Context context, List<Siswa> siswaList) {
        this.context = context;
        this.siswaList = siswaList;
    }

    @NonNull
    @Override
    public SiswaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Membuat view baru dari layout item_siswa.xml
        View view = LayoutInflater.from(context).inflate(R.layout.item_siswa, parent, false);
        return new SiswaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SiswaViewHolder holder, int position) {
        // Mengambil data siswa pada posisi tertentu
        Siswa siswa = siswaList.get(position);

        // Menetapkan data ke komponen view di dalam ViewHolder
        holder.tvNama.setText(siswa.getNama());
        holder.tvAlamat.setText(siswa.getAlamat());

        // Menambahkan OnClickListener untuk menu titik tiga
        holder.tvMenu.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, holder.tvMenu);
            popupMenu.inflate(R.menu.menu_option); // Menggunakan menu_option.xml

            popupMenu.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.menu_simpan) {
                    Toast.makeText(context, "Simpan data " + siswa.getNama(), Toast.LENGTH_SHORT).show();
                    return true;
                } else if (itemId == R.id.menu_hapus) {
                    // Hapus item dari list
                    siswaList.remove(position);
                    // Beri tahu adapter bahwa ada item yang dihapus
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, siswaList.size());
                    Toast.makeText(context, siswa.getNama() + " telah dihapus", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            });
            popupMenu.show();
        });
    }

    @Override
    public int getItemCount() {
        // Mengembalikan jumlah total item dalam list
        return siswaList.size();
    }

    // ViewHolder class untuk menampung komponen view dari item_siswa.xml
    public static class SiswaViewHolder extends RecyclerView.ViewHolder {
        TextView tvNama, tvAlamat, tvMenu;

        public SiswaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNama = itemView.findViewById(R.id.tvNama);
            tvAlamat = itemView.findViewById(R.id.tvAlamat);
            tvMenu = itemView.findViewById(R.id.tvMenu);
        }
    }
}