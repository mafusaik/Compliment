package com.example.compliment.data.repositories

import android.content.Context
import kotlinx.coroutines.flow.Flow

interface ComplimentsRepository {

   fun currentCompliment(): Flow<String>

   suspend fun restoreCompliments()

   suspend fun nextCompliment(): String

   suspend fun changeComplimentLang(newContext: Context)
}