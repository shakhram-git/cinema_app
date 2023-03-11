package com.example.skillcinema.data.local.room.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.net.IDN

@Entity(
    tableName = "collections",
    indices = [Index("name", unique = true)]
    )
data class CollectionDbo(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    @ColumnInfo(collate = ColumnInfo.NOCASE)
    val name: String,
    @ColumnInfo(name = "is_default")
    val isDefault: Boolean = false
)