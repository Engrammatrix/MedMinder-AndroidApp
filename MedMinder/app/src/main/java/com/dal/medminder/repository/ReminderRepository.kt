package com.dal.medminder.repository

import androidx.lifecycle.LiveData
import com.dal.medminder.data.ReminderDao
import com.dal.medminder.model.Reminder

class ReminderRepository(private val reminderDao: ReminderDao) {

    val getReminders: LiveData<List<Reminder>> = reminderDao.getReminders()

    suspend fun addReminder(reminder: Reminder) {
        reminderDao.addReminder(reminder)
    }

    suspend fun updateReminder(reminder: Reminder) {
        reminderDao.updateReminder(reminder)
    }

    suspend fun deleteReminder(reminder: Reminder) {
        reminderDao.deleteReminder(reminder)
    }

}