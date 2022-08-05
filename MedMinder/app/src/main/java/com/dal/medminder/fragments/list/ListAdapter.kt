package com.dal.medminder.fragments.list

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.content.Context
import android.icu.text.SimpleDateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.dal.medminder.notification.NotificationScheduler
import com.dal.medminder.R
import com.dal.medminder.model.Reminder
import kotlinx.android.synthetic.main.list_row.view.*
/**
 * This is the adapter class for List fragment, handles the functionalities for
 * UI elements in the medication reminder card
 */
class ListAdapter(parentActivity: FragmentActivity?) :
    RecyclerView.Adapter<ListAdapter.MyViewHolder>() {
    private var reminderList = emptyList<Reminder>()
    private val parentActivity = parentActivity
    private val sharedPreferences = parentActivity?.getSharedPreferences("reminderActivation", Context.MODE_PRIVATE)


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.list_row, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return reminderList.size
    }

    //Logic for creating a new medication card
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = reminderList[position]
        val editor = sharedPreferences?.edit()

        holder.itemView.medicationName.text = currentItem.tag
        holder.itemView.medicationDosage.text = currentItem.dosage

        if (!currentItem.activation) {
            holder.itemView.activationToggle.toggle()
        }
        // Show the date and time on the card
        val timeFormat = SimpleDateFormat("h:mm a")
        val dateFormat = SimpleDateFormat("MMM dd, YYYY")
        val timeStr: String = timeFormat.format(currentItem.time)
        val dateStr: String = dateFormat.format(currentItem.date + AlarmManager.INTERVAL_DAY)
        holder.itemView.time.text = timeStr
        holder.itemView.duration.text = dateStr
        holder.itemView.activationToggle.isChecked = sharedPreferences!!.getBoolean("activationState", true)

        holder.itemView.medicationDetails.setOnClickListener {
            val action = ListFragmentDirections.actionListFragmentToUpdateFragment(currentItem)
            holder.itemView.findNavController().navigate(action)
        }

        //Check if the toggle is modified
        holder.itemView.activationToggle.setOnCheckedChangeListener { buttonView, isChecked ->
            if (!isChecked) {
                //If it is toggled on, we need to disable it
                NotificationScheduler.cancelAlarm(parentActivity, currentItem.reqCode)
                Toast.makeText(parentActivity, R.string.rem_cancelled, Toast.LENGTH_SHORT).show()
            } else {
                //If it is toggled off, we need to enable it
                NotificationScheduler.scheduleAlarm(
                    parentActivity,
                    currentItem.time,
                    currentItem.tag,
                    currentItem.dosage,
                    currentItem.repeat,
                    currentItem.frequency,
                    currentItem.reqCode
                )
                Toast.makeText(parentActivity, R.string.rem_activated, Toast.LENGTH_SHORT).show()
            }
            editor?.putBoolean("activationState", isChecked)
            editor?.apply()
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    //Update the list when data is set
    fun setData(reminder: List<Reminder>) {
        this.reminderList = reminder
        notifyDataSetChanged()
    }

}