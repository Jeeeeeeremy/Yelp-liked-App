package uk.ac.shef.oak.com6510.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

/**
 * Database access object to access the Inventory database
 */
@Dao
interface ImageDataDao {
    @Query("SELECT * from image ORDER by id ASC")
    fun getItems(): List<ImageData>

    @Query("SELECT * from image WHERE id = :id LIMIT 1")
    fun getItem(id: Int): LiveData<ImageData>

    @Query("SELECT * from image WHERE title = :title")
    fun getItemsByTitle(title:String):LiveData<List<ImageData>>

    @Query("SELECT * from image WHERE longitude=:lon and latitude=:lat")
    fun getItemsByLonLat(lon:String, lat:String): List<ImageData>

    @Query("delete from image where title=:title")
    fun deleteItemsByTitle(title: String)

    @Query("DELETE FROM image WHERE longitude=:Lon and latitude=:Lat")
    fun deleteItemsByLonLat(Lon:String,Lat:String)

    // Specify the conflict strategy as REPLACE,
    // when the trying to add an existing Item
    // into the database.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(singleImageData: ImageData): Long

    @Update
    fun update(imageData: ImageData)

    @Delete
    fun delete(imageData: ImageData)
}