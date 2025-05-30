package com.curso_android.propeapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.curso_android.propeapp.databinding.ListItemEstudBinding

class EstudianteAdapter(private var alumnos: List<Estudiante> = emptyList(), private val alSeleccionarAlum:(String) -> Unit)
    : RecyclerView.Adapter<EstudianteAdapter.AlumnoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlumnoViewHolder {
        val binding = ListItemEstudBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AlumnoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlumnoViewHolder, position: Int) {
        val alumno = alumnos[position]
        holder.bind(alumno, alSeleccionarAlum)
    }

    override fun getItemCount(): Int = alumnos.size

    class AlumnoViewHolder(private val binding: ListItemEstudBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(alumno: Estudiante, alSeleccionarAlum: (String) -> Unit) {
            binding.tvNombre.text = alumno.nombre
            binding.tvRegistro.text = alumno.registro
            binding.tvGrupo.text = alumno.grupo
            binding.tvEstatus.text = alumno.estatus
            binding.root.setOnClickListener { alSeleccionarAlum(alumno.registro) }
        }
    }
}