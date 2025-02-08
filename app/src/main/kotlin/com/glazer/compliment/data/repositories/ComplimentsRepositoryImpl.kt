package com.glazer.compliment.data.repositories

import android.content.Context
import com.glazer.compliment.R
import com.glazer.compliment.data.sharedprefs.PrefsManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

internal class ComplimentsRepositoryImpl(newContext: Context) : ComplimentsRepository {
    private var context: Context = newContext
    private var prefsManager = PrefsManager(context)
    private val complimentSet = LinkedHashSet<String>()


    private val maxSize =
        if (prefsManager.isForWomen) 100 else 50 // Максимальное количество комплиментов которые не будут повторяться

    private val currentCompliment =
        MutableStateFlow(prefsManager.recentCompliments.lastOrNull() ?: "")

    override fun currentCompliment(): Flow<String> {
        return currentCompliment.asStateFlow()
    }

    override suspend fun nextCompliment(): String {
        val compliments = if (prefsManager.isForWomen){
            context.resources.getStringArray(R.array.compliments_women)
        } else {
            context.resources.getStringArray(R.array.compliments_men)
        }
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
        setCompliment(compliment)
    }

    override fun setCompliment(compliment: String) {
        currentCompliment.value = compliment
    }

    override suspend fun restoreCompliments() {
        val recentCompliments = prefsManager.recentCompliments
        if (recentCompliments.isNotEmpty()) complimentSet.addAll(recentCompliments)
        else complimentSet.add(nextCompliment())
    }


    override suspend fun changeComplimentLang(newContext: Context) {
        context = newContext
        prefsManager = PrefsManager(context)
        prefsManager.recentCompliments = emptySet()
        nextCompliment()
    }
}