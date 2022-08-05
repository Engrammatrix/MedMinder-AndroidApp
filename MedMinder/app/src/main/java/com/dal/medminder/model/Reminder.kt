package com.dal.medminder.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "reminder_table")
data class Reminder(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "drug_name") val tag: String,
    @ColumnInfo(name = "drug_dosage") val dosage: String,
    @ColumnInfo(name = "time") val time: Long,
    @ColumnInfo(name = "date") val date: Long,
    @ColumnInfo(name = "frequency") val frequency: Int = 1,
    @ColumnInfo(name = "is_repeated") val repeat: Boolean = false,
    @ColumnInfo(name = "request_code") val reqCode: Int,
    @ColumnInfo(name = "is_activated") val activation: Boolean = true,
) : Parcelable