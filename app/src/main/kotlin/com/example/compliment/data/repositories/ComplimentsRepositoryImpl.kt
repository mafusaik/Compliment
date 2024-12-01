package com.example.compliment.data.repositories

import android.content.Context
import android.util.Log
import com.example.compliment.R
import com.example.compliment.data.sharedprefs.PrefsManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlin.random.Random

internal class ComplimentsRepositoryImpl(private val context: Context) : ComplimentsRepository {

    private val prefsManager = PrefsManager(context)

    private val complimentSet = LinkedHashSet<String>()
    private val maxSize = 20 // Максимальное количество комплиментов которые не будут повторяться

    private val currentCompliment = MutableStateFlow(complimentSet.lastOrNull() ?: "")

    override fun currentCompliment(): Flow<String> {
        return currentCompliment.asStateFlow()
    }

    override suspend fun nextCompliment(): String {
        val compliments = context.resources.getStringArray(R.array.compliments)
        val recentCompliments = prefsManager.recentCompliments
        complimentSet.addAll(recentCompliments)

        var randomCompliment: String
        do {
            randomCompliment = compliments[Random.nextInt(compliments.size)]
        } while (recentCompliments.contains(randomCompliment))

        addCompliment(randomCompliment)

        return randomCompliment
    }

    private fun addCompliment(compliment: String) {
        if (complimentSet.size >= maxSize) {
            val iterator = complimentSet.iterator()
            if (iterator.hasNext()) {
                iterator.next()
                iterator.remove()
            }
        }
        complimentSet.add(compliment)
        prefsManager.recentCompliments = complimentSet
        currentCompliment.value = compliment
    }

}