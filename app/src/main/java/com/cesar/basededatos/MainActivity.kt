package com.cesar.basededatos

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.cesar.basededatos.data.AppDatabase
import com.cesar.basededatos.data.Articulo
import com.cesar.basededatos.data.ArticuloDao
import com.cesar.basededatos.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import com.cesar.basededatos.data.ArticuloRepository
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var repository: ArticuloRepository

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val dao = AppDatabase.getInstance(this).articuloDao()

        repository = ArticuloRepository(dao)

        lifecycleScope.launch {

            repeatOnLifecycle(Lifecycle.State.STARTED) {

                dao.listarTodos().collect { lista ->

                    println("Cantidad de artículos: ${lista.size}")

                    lista.forEach {

                        println(it)
                    }
                }
            }
        }

        binding.btnArchivo.setOnClickListener {
            guardarArchivo()
        }

        binding.btnRegistrar.setOnClickListener {
            registrar()
        }

        binding.btnBuscar.setOnClickListener {
            buscar()
        }

        binding.btnModificar.setOnClickListener {
            modificar()
        }

        binding.btnEliminar.setOnClickListener {
            eliminar()
        }
    }

    private fun toast(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
    }

    private fun limpiarCampos() {
        binding.txtCodigo.setText("")
        binding.txtDescripcion.setText("")
        binding.txtPrecio.setText("")
    }

    private fun guardarArchivo() {

        try {

            val carpeta = getExternalFilesDir(null)

            val archivo = File(carpeta, "productos.txt")

            archivo.writeText("Archivo creado correctamente")

            toast("Archivo guardado:\n${archivo.absolutePath}")

        } catch (e: Exception) {

            toast("Error: ${e.message}")
        }
    }

    private fun registrar() {

        val codigo = binding.txtCodigo.text.toString()

        val descripcion = binding.txtDescripcion.text.toString()

        val precio = binding.txtPrecio.text.toString()

        if (codigo.isEmpty() || descripcion.isEmpty() || precio.isEmpty()) {

            toast("Debe llenar todos los campos")

            return
        }

        val articulo = Articulo(
            codigo = codigo.toInt(),
            descripcion = descripcion,
            precio = precio.toDouble()
        )

        lifecycleScope.launch {

            try {

                repository.insertar(articulo)

                limpiarCampos()

                toast("Registro exitoso")

            } catch (e: Exception) {

                toast("Error: ${e.message}")
            }
        }
    }

    private fun buscar() {

        val codigo = binding.txtCodigo.text.toString()

        if (codigo.isEmpty()) {

            toast("Ingrese código")

            return
        }

        lifecycleScope.launch {

            val articulo = repository.buscarPorCodigo(codigo.toInt())

            if (articulo != null) {

                binding.txtDescripcion.setText(articulo.descripcion)

                binding.txtPrecio.setText(articulo.precio.toString())

            } else {

                toast("No existe")
            }
        }
    }

    private fun eliminar() {

        val codigo = binding.txtCodigo.text.toString()

        if (codigo.isEmpty()) {

            toast("Ingrese código")

            return
        }

        lifecycleScope.launch {

            val filas = repository.eliminarPorCodigo(codigo.toInt())

            limpiarCampos()

            if (filas == 1) {

                toast("Eliminado")

            } else {

                toast("No existe")
            }
        }
    }

    private fun modificar() {

        val codigo = binding.txtCodigo.text.toString()

        val descripcion = binding.txtDescripcion.text.toString()

        val precio = binding.txtPrecio.text.toString()

        if (codigo.isEmpty() || descripcion.isEmpty() || precio.isEmpty()) {

            toast("Complete todos los campos")

            return
        }

        val articulo = Articulo(
            codigo.toInt(),
            descripcion,
            precio.toDouble()
        )

        lifecycleScope.launch {

            val filas = repository.actualizar(articulo)

            if (filas == 1) {

                toast("Modificado")

            } else {

                toast("No existe")
            }
        }
    }
}