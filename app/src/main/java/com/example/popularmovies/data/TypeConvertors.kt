package com.example.popularmovies.data

import androidx.room.TypeConverter
import com.example.popularmovies.data.model.Genre
import com.example.popularmovies.data.model.ProductionCompany
import com.example.popularmovies.data.model.ProductionCountry
import com.example.popularmovies.data.model.SpokenLanguage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class GenreListConverter {
    @TypeConverter
    fun fromString(value: String?): List<Genre>? {
        return value?.let {
            Gson().fromJson(it, object : TypeToken<List<Genre>>() {}.type)
        }
    }

    @TypeConverter
    fun toString(value: List<Genre>?): String? {
        return value?.let {
            Gson().toJson(it)
        }
    }
}

class ProductionCompanyListConverter {
    @TypeConverter
    fun fromString(value: String?): List<ProductionCompany>? {
        return value?.let {
            Gson().fromJson(it, object : TypeToken<List<Genre>>() {}.type)
        }
    }

    @TypeConverter
    fun toString(value: List<ProductionCompany>?): String? {
        return value?.let {
            Gson().toJson(it)
        }
    }
}

class ProductionCountryListConverter {
    @TypeConverter
    fun fromString(value: String?): List<ProductionCountry>? {
        return value?.let {
            Gson().fromJson(it, object : TypeToken<List<Genre>>() {}.type)
        }
    }

    @TypeConverter
    fun toString(value: List<ProductionCountry>?): String? {
        return value?.let {
            Gson().toJson(it)
        }
    }
}

class SpokenLanguageListConverter {
    @TypeConverter
    fun fromString(value: String?): List<SpokenLanguage>? {
        return value?.let {
            Gson().fromJson(it, object : TypeToken<List<Genre>>() {}.type)
        }
    }

    @TypeConverter
    fun toString(value: List<SpokenLanguage>?): String? {
        return value?.let {
            Gson().toJson(it)
        }
    }
}

class IntegerListConverter {
    @TypeConverter
    fun fromString(value: String?): List<Int>? {
        return value?.let {
            Gson().fromJson(it, object : TypeToken<List<Int>>() {}.type)
        }
    }

    @TypeConverter
    fun toString(value: List<Int>?): String? {
        return value?.let {
            Gson().toJson(it)
        }
    }
}
