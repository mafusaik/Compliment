package com.example.compliment.data.repositories;

interface ComplimentsRepository {

   suspend fun getCompliment(): String
}