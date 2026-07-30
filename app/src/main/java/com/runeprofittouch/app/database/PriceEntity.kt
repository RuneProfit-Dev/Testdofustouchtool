package com.runeprofittouch.app.database

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "prices",
    indices = [
        Index(value = ["subjectType", "subjectId"]),
        Index(value = ["server"]),
        Index(value = ["recordedAt"])
    ]
)
data class PriceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    /**
     * Type de donnée concernée par le prix.
     *
     * Valeurs prévues :
     * ITEM
     * RESOURCE
     * RUNE
     */
    val subjectType: String,

    /**
     * Identifiant de l'objet, de la ressource ou de la rune.
     */
    val subjectId: Int,

    /**
     * Nom du serveur DOFUS Touch.
     */
    val server: String,

    /**
     * Quantité du lot vendu en hôtel de vente.
     *
     * Généralement 1, 10 ou 100.
     */
    val lotSize: Int = 1,

    /**
     * Prix total du lot en kamas.
     */
    val price: Long,

    /**
     * Date d'enregistrement en millisecondes Unix.
     */
    val recordedAt: Long = System.currentTimeMillis()
)