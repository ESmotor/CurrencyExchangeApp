package com.itskidan.core_api.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(
    tableName = "cached_currency_rates",
    indices = [Index(value = ["currencyCode"], unique = true)]
)
data class Currency(
    @PrimaryKey(autoGenerate = false) val currencyCode: String,
    val currencyName: String,
    val currencyFlagId: Int,
    val currencyAskValue: Double,
    val currencyBidValue: Double
) : Parcelable {
    override fun toString(): String {
        return "Code:$currencyCode, Name:$currencyName, Value:$currencyAskValue"
    }
}