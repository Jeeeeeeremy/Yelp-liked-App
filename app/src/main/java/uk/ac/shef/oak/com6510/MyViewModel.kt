package uk.ac.shef.oak.com6510

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uk.ac.shef.oak.com6510.data.ImageData

class MyViewModel(application: Application) :AndroidViewModel(application){

    private var mRepository:MyRepository= MyRepository(application)
    private var Image:LiveData<ImageData>?=null
    private var ImageList:List<ImageData>?=null

    init {
        ImageList=getAllImages()
        Image=getImageByID(0)
    }

    /** get methods*/
    fun getAllImages():List<ImageData>?{
        ImageList=mRepository.getAllImages()
        return ImageList
    }

    fun getImageByID(id:Int):LiveData<ImageData>?{
        Image=mRepository.getImageByID(id)
        return Image
    }

//    fun getImageByTitle(title:String):List<ImageData>?{
//        ImageList=mRepository.getImagesBtTitle(title)
//        return ImageList
//    }

    fun getImageByLonLat(lon:String,lat:String):List<ImageData>?{
        ImageList=mRepository.getImagesByLonLat(lon,lat)
        return ImageList
    }

    /** Insert Method */
    suspend fun insertNewImage(ImageItem:ImageData):Long?{
       return mRepository.addNewImage(ImageItem)
    }

    /** Delete Methods */
    fun deleteImage(ImageItem: ImageData){
        mRepository.deleteImage(ImageItem)
    }

    fun deleteImageByTitle(title:String){
        mRepository.deleteImageByTitle(title)
    }

    fun deleteImageByLonLat(lon: String,lat: String){
        mRepository.deleteImageByLonLat(lon,lat)
    }

    /** Update Method */
    fun updateImageInfo(ImageItem: ImageData){
        mRepository.updateItem(ImageItem)
    }

    /** AsyncTask insert*/
    fun generateNewImage(imageData: ImageData):Int?{
        var id:Int?=null
        viewModelScope.launch (Dispatchers.IO){  id= mRepository.generateNewImage(imageData) }
    return id
    }
}