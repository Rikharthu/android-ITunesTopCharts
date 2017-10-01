package com.example.uberv.itunestopcharts.data.database

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import com.example.uberv.itunestopcharts.data.models.Track

@Dao
abstract class TrackDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(vararg tracks: Track)
}