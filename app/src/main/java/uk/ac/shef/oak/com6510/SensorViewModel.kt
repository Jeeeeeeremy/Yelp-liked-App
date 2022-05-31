/*
 * Copyright (c) 2021. This code has been developed by Temitope Adeosun, The University of Sheffield.
 * All rights reserved. No part of this code can be used without the explicit written permission by the author
 */

package uk.ac.shef.oak.com6510

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class SensorViewModel(application: Application): AndroidViewModel(application) {

    private var pressure: Pressure = Pressure(application)
    private var temperature: Temperature = Temperature(application)

    /**
     * Calls the needed sensor class to start monitoring the sensor data
     */
    fun startSensing() {
        pressure.startPressureSensing()
        temperature.startTemperatureSensing()
    }


    /**
     * Calls the needed sensor class to stop monitoring the sensor data
     */
    fun stopSensing() {
        pressure.stopPressureSensing()
        temperature.stopTemperatureSensing()
    }

    /**
     * Func that exposes the pressure as LiveData to the View object
     * @return
     */
    fun retrievePressureData(): LiveData<Float>{
        return pressure.pressureReading
    }

    /**
     * Func that exposes the Accelerometer data as LiveData to the View object
     * @return
     */
    fun retrieveTemperatureData(): LiveData<Float> {
        return temperature.temperatureReading
    }

    /**
     * Func that exposes the status change of the sensor monitoring
     */
    fun isStarted(): Boolean {
        return pressure.isStarted
    }
}