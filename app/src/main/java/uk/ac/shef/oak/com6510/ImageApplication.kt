package uk.ac.shef.oak.com6510

import android.app.Application
import uk.ac.shef.oak.com6510.data.ImageRoomDatabase

class ImageApplication: Application() {
    val databaseObj: ImageRoomDatabase by lazy { ImageRoomDatabase.getDatabase(this) }
}
