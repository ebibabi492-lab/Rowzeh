package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.RowzehTrack
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {
    @Query("SELECT * FROM rowzeh_tracks WHERE isBuiltIn = 0 ORDER BY createdAt DESC")
    fun getAllTracks(): Flow<List<RowzehTrack>>

    @Query("SELECT * FROM rowzeh_tracks WHERE isBuiltIn = 0 AND isIncludedInRandom = 1")
    fun getIncludedTracks(): Flow<List<RowzehTrack>>

    @Query("SELECT * FROM rowzeh_tracks WHERE isBuiltIn = 0 AND isIncludedInRandom = 1")
    suspend fun getIncludedTracksSync(): List<RowzehTrack>

    @Query("SELECT * FROM rowzeh_tracks WHERE isBuiltIn = 0")
    suspend fun getAllUserTracksSync(): List<RowzehTrack>

    @Query("SELECT * FROM rowzeh_tracks WHERE id = :id LIMIT 1")
    suspend fun getTrackById(id: Long): RowzehTrack?

    @Query("SELECT COUNT(*) FROM rowzeh_tracks WHERE isBuiltIn = 0")
    suspend fun getTrackCount(): Int

    @Query("DELETE FROM rowzeh_tracks WHERE isBuiltIn = 1")
    suspend fun deleteBuiltInTracks()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: RowzehTrack): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTracks(tracks: List<RowzehTrack>)

    @Update
    suspend fun updateTrack(track: RowzehTrack)

    @Delete
    suspend fun deleteTrack(track: RowzehTrack)

    @Query("UPDATE rowzeh_tracks SET isIncludedInRandom = :included WHERE id = :id")
    suspend fun setTrackIncluded(id: Long, included: Boolean)
}
