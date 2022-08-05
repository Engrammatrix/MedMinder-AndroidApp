package com.dal.medminder.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.dal.medminder.model.Reminder

@Dao
interface ReminderDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addReminder(reminder: Reminder)

    @Update
    suspend fun updateReminder(reminder: Reminder)

    @Delete
    suspend fun deleteReminder(reminder: Reminder)

    @Query("SELECT * FROM reminder_table")
    fun getReminders(): LiveData<List<Reminder>>

}