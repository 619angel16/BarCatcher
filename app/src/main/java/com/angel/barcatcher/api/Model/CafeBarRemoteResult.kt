package com.angel.barcatcher.api.Model

data class CafeBarRemoteResult(
    val Results: List<Cafebar>,
    val Includes: Map<String, Any>
)
