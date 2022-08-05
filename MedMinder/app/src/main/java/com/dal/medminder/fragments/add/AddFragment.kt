package com.dal.medminder.fragments.add

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.dal.medminder.MedicationTimeDatePicker
import com.dal.medminder.notification.NotificationScheduler
import com.dal.medminder.R
import com.dal.medminder.model.Reminder
import com.dal.medminder.viewmodel.ReminderViewModel
import kotlinx.android.synthetic.main.fragment_add.*
import kotlinx.android.synthetic.main.fragment_add.view.*
import java.lang.Integer.parseInt
import java.util.*

/**
 * This class handles functionalities in the add fragment
 */
class AddFragment : Fragment() {
    private lateinit var reminderViewModel: ReminderViewModel
    private var reqCode: Int = (0..9999).random()
    private var repeatCheck = false
    private var repeatFreq = 0
    private val currTimeMS = Calendar.getInstance().timeInMillis
    private val medTDPickerObj = MedicationTimeDatePicker(
        currTimeMS,
        currTimeMS
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add, container, false)
        reminderViewModel = ViewModelProvider(this).get(ReminderViewModel::class.java)
        setHasOptionsMenu(true)

        // Fired when user clicks on "Add" button.
        view.addButton.setOnClickListener {
            if(addMedicationName.text.toString().isNotEmpty() and addMedicationDosage.text.toString().isNotEmpty()) {
                NotificationScheduler.scheduleAlarm(
                    activity,
                    medTDPickerObj.getTime(),
                    addMedicationName.text.toString(),
                    addMedicationDosage.text.toString(),
                    repeatCheck,
                    repeatFreq,
                    reqCode
                )
                saveReminder()
                //Notify user
                Toast.makeText(requireContext(), R.string.successful_add, Toast.LENGTH_LONG).show()
                findNavController().navigate(R.id.action_addFragment_to_listFragment)
            } else {
                Toast.makeText(requireContext(), R.string.no_add, Toast.LENGTH_LONG).show()
            }


        }
        // Logic for checking the repeat checkbox
        view.repeatCheckBox.setOnCheckedChangeListener { _, isChecked ->
            repeatCheck = isChecked
            val freqCount = view.frequency.text.toString()
            repeatFreq = if (freqCount.isEmpty()) {
                0
            } else {
                parseInt(freqCount)
            }
        }
        // Open the time picker
        view.editTime.setOnClickListener {
            medTDPickerObj.openTimePicker(childFragmentManager, view.editTime, requireContext())
        }
        // Show the current time
        view.editTime.setSpannableFactory(medTDPickerObj.spannableFactory)
        val timeFormat = SimpleDateFormat("h:mm a")
        val timeStr: String = timeFormat.format(currTimeMS)
        view.editTime.setText(timeStr, TextView.BufferType.SPANNABLE)

        // Open the date picker
        view.editDate.setOnClickListener {
            medTDPickerObj.openDatePicker(childFragmentManager, view.editDate)
        }
        // Show the current date
        val dateFormat = SimpleDateFormat("MMM dd, YYYY")
        val date: String = dateFormat.format(medTDPickerObj.getDate())
        view.editDate.text = date
        return view
    }

    /**
    * This method saves a new reminder to the database
    */
    private fun saveReminder() {
        val medicationName = addMedicationName.text.toString()
        val medicationDosage = addMedicationDosage.text.toString()
        val medicationTime = medTDPickerObj.getTime()
        val medicationDate = medTDPickerObj.getDate()
        var medicationFrequency = 0

        if (frequency.text.toString().isNotEmpty()) {
            medicationFrequency = frequency.text.toString().toInt()
        }

        val frequencyRepeated = repeatCheckBox.isChecked
        val reminder = Reminder(
            0,
            medicationName,
            medicationDosage,
            medicationTime,
            medicationDate,
            medicationFrequency,
            frequencyRepeated,
            reqCode
        )
        reminderViewModel.addReminder(reminder)

    }

    //Back button
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            activity?.onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}





