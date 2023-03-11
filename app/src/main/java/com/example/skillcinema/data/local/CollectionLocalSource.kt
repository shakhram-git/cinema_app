package com.example.skillcinema.data.local

import androidx.room.Transaction
import com.example.skillcinema.data.local.room.CollectionDao
import com.example.skillcinema.data.local.room.model.CollectionDbo
import com.example.skillcinema.data.local.room.model.CollectionWithFilmsDbo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CollectionLocalSource @Inject constructor(private val collectionDao: CollectionDao) {



    fun getCollectionFilmsIds(collectionId: Int): Flow<List<Long>>{
        return collectionDao.getCollectionFilmsIds(collectionId)
    }

    @Transaction
    fun getAllCollections(): Flow<List<CollectionWithFilmsDbo>> {
        return collectionDao.getAllCollectionsInfo()
    }

    suspend fun createNewCollection(collectionDbo: CollectionDbo): Long {
        return collectionDao.createNewCollection(collectionDbo)
    }

    @Transaction
    fun getCollectionWithFilmsFlow(collectionId: Int): Flow<CollectionWithFilmsDbo> {
        return collectionDao.getCollectionWithFilmsFlow(collectionId)
    }

    suspend fun cleanCollection(collectionId: Int){
        collectionDao.cleanCollection(collectionId)
    }

    suspend fun limitInterestedCollection(){
        collectionDao.limitInterestedCollection()
    }


    suspend fun deleteCollection(collectionId: Int){
        collectionDao.deleteCollection(collectionId)
    }

}