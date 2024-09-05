package com.itskidan.core_api.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(
    tableName = "cached_total_balance_currencies",
    indices = [Index(value = ["currencyCode"], unique = true)]
)
data class TotalBalanceCurrency(
    var id: Int,
    @PrimaryKey(autoGenerate = false) val currencyCode: String,
    var currencyValue: Double,
) : Parcelable {
    override fun toString(): String {
        return "id:$id, Code:$currencyCode, Rate:$currencyValue"
    }
}