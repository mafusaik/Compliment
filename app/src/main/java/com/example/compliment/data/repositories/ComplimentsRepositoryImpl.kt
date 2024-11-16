package com.example.compliment.data.repositories

import android.content.Context
import android.util.Log
import com.example.compliment.R
import com.example.compliment.data.sharedprefs.PrefsManager
import kotlin.random.Random

internal class ComplimentsRepositoryImpl(private val context: Context) : ComplimentsRepository {

    private val prefsManager = PrefsManager(context)

    private val complimentSet = LinkedHashSet<String>()
    private val maxSize = 20 // Максимальное количество комплиментов которые не будут повторяться


    override suspend fun getCompliment(): String {
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
    }

}