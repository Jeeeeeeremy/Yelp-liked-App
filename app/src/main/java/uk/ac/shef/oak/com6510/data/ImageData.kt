package uk.ac.shef.oak.com6510.data

import android.graphics.Bitmap
import androidx.room.*
import pl.aprilapps.easyphotopicker.MediaFile

/**
 * Entity data class represents a single row in the database.
 */
@Entity(tableName = "image", indices = [Index(value = ["id","title"])])
data class ImageData(
    @PrimaryKey(autoGenerate = true)var id: Int = 0,
    @ColumnInfo(name="uri") val imageUri: String,
    @ColumnInfo(name="title") var imageTitle: String,
    @ColumnInfo(name="description") var imageDescription: String? = null,
    @ColumnInfo(name="thumbnailUri") var thumbnailUri: String? = null,
    @ColumnInfo(name="longitude") var longitude: String? = null,
    @ColumnInfo(name="latitude") var latitude: String? = null,
    @ColumnInfo(name="pressure") var pressure: String? = null,
    @ColumnInfo(name="temperature") var temperature: String? = null,)
{
    @Ignore
    var thumbnail: Bitmap? = null
}