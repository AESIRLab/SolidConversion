package com.example.solidconversion

import android.app.Application
import android.util.Log
import org.skCompiler.generatedModel.BlogItemDatabase
import org.skCompiler.generatedModel.BlogItemRepository
import org.skCompiler.generatedModel.BlogItemDao
import org.skCompiler.generatedModel.BlogItemDaoImpl

class SolidConversionApplication: Application() {
    init {
        appInstance = this
    }

    companion object {
        lateinit var appInstance: SolidConversionApplication
        const val FILE_PATH = "SolidConversionApplication"
        const val BASE_URI = "https://solidconversion.com"
    }

    private val database by lazy { BlogItemDatabase.getDatabase(appInstance, BASE_URI) }
    val repository by lazy { BlogItemRepository(database.BlogItemDao()) }

}