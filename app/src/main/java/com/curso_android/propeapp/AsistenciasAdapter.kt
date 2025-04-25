package com.curso_android.propeapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.curso_android.propeapp.databinding.ListItemAsistenciaBinding

class AsistenciasAdapter(private val listaAsistencias: List<Asistencia>) :
    RecyclerView.Adapter<AsistenciasAdapter.AsistenciaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AsistenciaViewHolder {
        val binding = ListItemAsistenciaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AsistenciaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AsistenciaViewHolder, position: Int) {
        val asistencia = listaAsistencias[position]
        holder.bind(asistencia)
    }

    override fun getItemCount(): Int = listaAsistencias.size

    class AsistenciaViewHolder(private val binding: ListItemAsistenciaBinding) : RecyclerView.ViewHolder(binding.root) {
        //val textFecha: TextView = itemView.findViewById(R.id.textFecha)
        fun bind(asistencia: Asistencia) {
            binding.tvAsistenciaValor.text = asistencia.valor
        }
    }
}
