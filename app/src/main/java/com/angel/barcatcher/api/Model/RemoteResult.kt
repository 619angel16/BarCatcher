package com.angel.barcatcher.api.Model

data class RemoteResult(
    val Results: List<Country>,
    val Includes: Map<String, Any> // O puedes usar Any si el contenido puede variar
)
