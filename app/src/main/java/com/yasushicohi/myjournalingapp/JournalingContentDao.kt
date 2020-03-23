package com.yasushicohi.myjournalingapp


import androidx.room.*
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface JournalingContentDao{

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(JournalingContent: JournalingContent) : Single<Long>

    @Query("SELECT * FROM JournalingContent")
    // fun getAll(): Observable<List<JournalingContent>> これ、observableじゃなくて良くない？
    fun getAll(): Single<MutableList<JournalingContent>>

    @Query("SELECT * FROM JournalingContent WHERE journalingContentId = :JournalingContentId")
    fun findById(JournalingContentId : Int): Single<JournalingContent>

    @Update
    fun update(JournalingContent: JournalingContent)

    @Delete
    fun delete(JournalingContent: JournalingContent)

    @Query("DELETE FROM JournalingContent WHERE journalingContentId = :id")
    fun deleteById(id: Int) : Completable
}