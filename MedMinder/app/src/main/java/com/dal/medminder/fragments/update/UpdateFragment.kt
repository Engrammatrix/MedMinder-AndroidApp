package com.dal.medminder.fragments.update

import android.app.AlarmManager
import android.app.AlertDialog
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.dal.medminder.MedicationTimeDatePicker
import com.dal.medminder.notification.NotificationScheduler
import com.dal.medminder.R
import com.dal.medminder.model.Reminder
import com.dal.medminder.viewmodel.ReminderViewModel
import kotlinx.android.synthetic.main.fragment_update.*
import kotlinx.android.synthetic.main.fragment_update.view.*
import java.util.*

/**
 * This class handles functionalities in the update fragment
 */
class UpdateFragment : Fragment() {
    private val args by navArgs<UpdateFragmentArgs>()
    private lateinit var reminderViewModel: ReminderViewModel
    private val medTDPickerObj = MedicationTimeDatePicker(
        Calendar.getInstance().timeInMillis,
        Calendar.getInstance().timeInMillis
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_update, container, false)
        reminderViewModel = ViewModelProvider(this)[ReminderViewModel::class.java]

        view.updateMedicationName.setText(args.currentReminder.tag)
        view.updateMedicationDosage.setText(args.currentReminder.dosage)
        view.updateFrequency.setText(args.currentReminder.frequency.toString())
        view.updateRepeatCheckBox.isChecked = args.currentReminder.repeat
        medTDPickerObj.setTime(args.currentReminder.time)
        medTDPickerObj.setDate(args.currentReminder.date + AlarmManager.INTERVAL_DAY)
        // Open the time picker card
        view.editTime_update.setOnClickListener {
            medTDPickerObj.openTimePicker(
                childFragmentManager,
                view.editTime_update,
                requireContext()
            )
        }
        // Show the previously set time
        val timeFormat = SimpleDateFormat("h:mm a")
        val timeStr: String = timeFormat.format(args.currentReminder.time)
        view.editTime_update.setSpannableFactory(medTDPickerObj.spannableFactory)
        view.editTime_update.setText(timeStr, TextView.BufferType.SPANNABLE)

        // Open the date picker card
        view.editDate_update.setOnClickListener {
            medTDPickerObj.openDatePicker(childFragmentManager, view.editDate_update)
        }
        setHasOptionsMenu(true)
        // Show the previously set date
        val dateFormat = SimpleDateFormat("MMM dd, YYYY")
        val date: String = dateFormat.format(medTDPickerObj.getDate())
        view.editDate_update.text = date

        view.updateButton.setOnClickListener {
            updateItem()
        }

        return view
    }

    /**
     * This method is used to update the chosen reminder
     */
    private fun updateItem() {
        val medicationName = updateMedicationName.text.toString()
        val medicationDosage = updateMedicationDosage.text.toString()
        val repeatFreq = updateFrequency.text.toString().toInt()
        val repeatCheck = updateRepeatCheckBox.isChecked
        val medicationTime = medTDPickerObj.getTime()
        val medicationDate = medTDPickerObj.getDate()
        val reqCode = args.currentReminder.reqCode

        NotificationScheduler.cancelAlarm(activity, reqCode)

        val newReqCode = (0..9999).random()

        NotificationScheduler.scheduleAlarm(
            activity,
            medTDPickerObj.getTime(),
            updateMedicationName.text.toString(),
            updateMedicationDosage.text.toString(),
            repeatCheck,
            repeatFreq,
            newReqCode
        )

        if (inputCheck(medicationName, medicationDosage)) {
            val updateReminder = Reminder(
                args.currentReminder.id,
                medicationName,
                medicationDosage,
                medicationTime,
                medicationDate,
                repeatFreq,
                repeatCheck,
                newReqCode
            )
            reminderViewModel.updateReminder(updateReminder)
            Toast.makeText(requireContext(), R.string.successful_update, Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_updateFragment_to_listFragment)
        } else {
            Toast.makeText(requireContext(), R.string.no_add, Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun inputCheck(medicationName: String, medicationDosage: String): Boolean {
        return !(TextUtils.isEmpty(medicationName) || TextUtils.isEmpty(medicationDosage))
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.delete_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_delete) {
            deleteReminder()
        } else if (item.itemId == android.R.id.home) {
            activity?.onBackPressed()
        }

        return super.onOptionsItemSelected(item)
    }

    /**
     * This method is used to delete the chosen reminder
     */
    private fun deleteReminder() {
        val builder = AlertDialog.Builder(requireContext())

        NotificationScheduler.cancelAlarm(activity, args.currentReminder.reqCode)
        val delSuccess = resources.getString(R.string.del_success)
        val optionYes = resources.getString(R.string.yes)
        val optionNo = resources.getString(R.string.no)
        builder.setPositiveButton(optionYes) { _, _ ->
            reminderViewModel.deleteReminder(args.currentReminder)
            Toast.makeText(
                requireContext(),
                "$delSuccess ${args.currentReminder.tag}",
                Toast.LENGTH_SHORT
            ).show()
            findNavController().navigate(R.id.action_updateFragment_to_listFragment)
        }
        builder.setNegativeButton(optionNo) { _, _ -> }
        val del = resources.getString(R.string.del_dialog1)
        builder.setTitle("$del ${args.currentReminder.tag}?")
        val delAssurance = resources.getString(R.string.del_dialog2)
        builder.setMessage("$delAssurance ${args.currentReminder.tag}?")
        builder.create().show()
    }
}