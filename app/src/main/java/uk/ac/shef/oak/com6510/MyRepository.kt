package uk.ac.shef.oak.com6510

import android.app.Application
import android.app.PictureInPictureParams
import android.util.Log
import androidx.annotation.RestrictTo
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uk.ac.shef.oak.com6510.data.ImageData
import uk.ac.shef.oak.com6510.data.ImageDataDao
import uk.ac.shef.oak.com6510.data.ImageRoomDatabase

class MyRepository(application: Application):ViewModel() {
    private var mDBDao : ImageDataDao?=null

    init {
        val db: ImageRoomDatabase?= ImageRoomDatabase.getDatabase(application)
        if (db!=null){mDBDao=db.imageDataDao()}
    }

    companion object{
        private val scope = CoroutineScope(Dispatchers.IO)
        private class InsertAsyncTask(private val dao: ImageDataDao?):ViewModel(){
            suspend fun InsertInBackground(vararg params: ImageData):Int?{
                var id : Int?=null
                scope.launch { for (param in params){
                        val insertedID:Int?=this@InsertAsyncTask.dao?.insert(param)?.toInt()
                        Log.d("MyRepository","An Image has been inserted")
                    if (insertedID != null) {
                        id=insertedID
                    }
                } }
                return id
            }

        }
    }

    //Searching methods
    fun getAllImages(): List<ImageData>? {
        return mDBDao?.getItems()
    }

    fun getImageByID(id:Int):LiveData<ImageData>?{
        return mDBDao?.getItem(id)
    }

    fun getImagesBtTitle(title:String):LiveData<List<ImageData>>?{
        return  mDBDao?.getItemsByTitle(title)
    }

    fun getImagesByLonLat(Lon:String,Lat:String):List<ImageData>?{
        return  mDBDao?.getItemsByLonLat(Lon,Lat)
    }

    //Insert methods
    suspend fun addNewImage(ImageItem:ImageData):Long?{
        return mDBDao?.insert(ImageItem)
    }

    //Delete Methods
    fun deleteImage(ImageItem: ImageData){
        mDBDao?.delete(ImageItem)
    }

    fun deleteImageByTitle(Title:String){
        mDBDao?.deleteItemsByTitle(Title)
    }

    fun deleteImageByLonLat(Lon: String,Lat: String){
        mDBDao?.deleteItemsByLonLat(Lon,Lat)
    }

    //Update Methods
    fun updateItem(ImageItem: ImageData){
        mDBDao?.update(ImageItem)
    }

    suspend fun generateNewImage(imageData: ImageData):Int?{
        var id:Int?=null
        id = InsertAsyncTask(mDBDao).InsertInBackground(imageData)
        return id
    }



}