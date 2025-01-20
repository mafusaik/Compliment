package com.example.compliment.data.repositories

import android.content.Context
import androidx.compose.runtime.Immutable
import kotlinx.coroutines.flow.Flow

@Immutable
interface ComplimentsRepository {

   fun currentCompliment(): Flow<String>

   suspend fun restoreCompliments()

   suspend fun nextCompliment(): String

   suspend fun changeComplimentLang(newContext: Context)

  fun setCompliment(compliment: String)
}