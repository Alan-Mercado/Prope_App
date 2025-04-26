package com.curso_android.propeapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.curso_android.propeapp.databinding.ListItemServicioSocialBinding

class AdminPersAdapter(private var personal: List<AdminPers> = emptyList(), private val alSeleccionarPersonal:(String) -> Unit) : RecyclerView.Adapter<AdminPersAdapter.AdminPersViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminPersViewHolder {
        val binding = ListItemServicioSocialBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdminPersViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdminPersViewHolder, position: Int) {
        val pers = personal[position]
        holder.bind(pers, alSeleccionarPersonal)
    }

    override fun getItemCount(): Int = personal.size

    class AdminPersViewHolder(private val binding: ListItemServicioSocialBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(personal: AdminPers, alSeleccionarPers: (String) -> Unit) {
            binding.tvNombre.text = personal.nombre
            binding.tvRegistro.text = personal.registro
            binding.tvAcceso.text = personal.acceso
            binding.root.setOnClickListener { alSeleccionarPers(personal.registro) }
        }
    }
}