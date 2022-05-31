package uk.ac.shef.oak.com6510
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.SystemClock
import android.util.Log
import androidx.lifecycle.MutableLiveData
import java.lang.Exception

class Pressure(context: Context) {
    private val PRESSURE_READING_FREQ_MICRO_SEC: Int = 20000
    private var samplingRateInMicroSec: Long = PRESSURE_READING_FREQ_MICRO_SEC.toLong()
    private var samplingRateInNanoSec: Long = samplingRateInMicroSec * 1000 * 1000
    private var timePhoneWasLastRebooted: Long = 0
    private var lastReportTime: Long = 0

   //private lateinit var accelerometer: Accelerometer
    private var sensorManager: SensorManager?
    private var pressureSensor: Sensor
    private var pressureEventListener: SensorEventListener? = null
    private var _isStarted = false
    val isStarted: Boolean
        get() {return _isStarted}

    var pressureReading: MutableLiveData<Float> = MutableLiveData<Float>()


    init{
        // http://androidforums.com/threads/how-to-get-time-of-last-system-boot.548661/
        timePhoneWasLastRebooted = System.currentTimeMillis() - SystemClock.elapsedRealtime()

        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        pressureSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_PRESSURE)!!

        /**
         * this inits the listener and establishes the actions to take when a sensor is available
         * It is not registere to listen at this point, but makes sure the object is available to
         * listen when registered.
         */
        pressureEventListener  = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                val diff = event.timestamp - this@Pressure.lastReportTime
                // time is in nanoseconds it represents the set reference times the first time we come here
                // set event timestamp to current time in milliseconds
                // see answer 2 at http://stackoverflow.com/questions/5500765/accelerometer-sensorevent-timestamp
                // the following operation avoids reporting too many events too quickly - the sensor may always
                // misbehave and start sending data very quickly

                if (diff >= this@Pressure.samplingRateInNanoSec) {
                    val actualTimeInMseconds =
                        this@Pressure.timePhoneWasLastRebooted + (event.timestamp / 1000000.0).toLong()
                    // In this example, only updating the LiveData (hence UI gets update),
                    // when there is a new pressue value - no need for a UI update otherwise
                    if(pressureReading.value != event.values[0]){pressureReading.value = event.values[0]}
                    val accuracy = event.accuracy
                    Log.i(
                        TAG,
                        Utilities.mSecsToString(actualTimeInMseconds) +
                                ": current barometric pressure: " +
                                pressureReading.value + "with accuracy: " + accuracy
                    )
                    this@Pressure.lastReportTime = event.timestamp
                    // if we have not see any movement on the side of the accelerometer, let's stop
                    //val timeLag = actualTimeInMseconds - accelerometer.getLastReportTime()
                    //val timeLag
                    //if (timeLag > STOPPING_THRESHOLD) stopPressureSensing()
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
    }

    companion object {
        private val TAG = Pressure::class.java.simpleName

        /**
         * this is used to stop the barometer if we have not seen any movement in the last 20 seconds
         */
        private const val STOPPING_THRESHOLD = 20000.toLong()
    }

    /**
     * it starts the pressure monitoring and updates the _isStarted status flag
     * @param accelerometer
     */
    fun startPressureSensing() {
        //this.accelerometer = accelerometer
        sensorManager?.let {
            // if the sensor is null,then mSensorManager is null and we get a crash
            Log.d(TAG, "Starting listener")
            // delay is in microseconds (1millisecond=1000 microseconds)
            // it does not seem to work though
            //stopBarometer();
            // otherwise we stop immediately because
            it.registerListener(
                pressureEventListener,
                pressureSensor,
                samplingRateInMicroSec.toInt()
            )
            _isStarted = true
        }
    }

    /**
     * this stops the barometer and updates the _isStarted status flag
     */
    fun stopPressureSensing() {
        sensorManager?.let {
            Log.d(TAG, "Stopping listener")
            try {
                it.unregisterListener(pressureEventListener)
                _isStarted = false
            } catch (e: Exception) {
                // probably already unregistered
                //Log.d(Accelerometer.TAG, "failed to unregister sensor, probably not running already")
            }
        }
    }


}