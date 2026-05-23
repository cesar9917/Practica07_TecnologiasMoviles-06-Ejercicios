package com.cesar.basededatos.data

class ArticuloRepository(
    private val dao: ArticuloDao
) {

    suspend fun insertar(articulo: Articulo): Long {

        return dao.insertar(articulo)
    }

    suspend fun actualizar(articulo: Articulo): Int {

        return dao.actualizar(articulo)
    }

    suspend fun eliminarPorCodigo(codigo: Int): Int {

        return dao.eliminarPorCodigo(codigo)
    }

    suspend fun buscarPorCodigo(codigo: Int): Articulo? {

        return dao.buscarPorCodigo(codigo)
    }

    fun listarTodos() = dao.listarTodos()
}