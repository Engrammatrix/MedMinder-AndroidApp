package com.dal.medminder

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.text.Spannable
import android.text.format.DateFormat
import android.text.style.AbsoluteSizeSpan
import android.util.Log
import android.widget.TextView
import androidx.core.text.toSpannable
import androidx.fragment.app.FragmentManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.util.*
import java.util.concurrent.TimeUnit
/**
 * This class is utilised to handle date and time picker UI and various conversions
 */
class MedicationTimeDatePicker {
    private var time: Long = 0
    private var date: Long = 0
    private lateinit var datetime: Calendar

    constructor(time: Long, date: Long) {
        this.time = time
        this.date = date
    }
    // Getters and setters for time and date
    fun getTime(): Long {
        return time
    }

    fun setTime(time: Long) {
        this.time = time
    }

    fun getDate(): Long {
        return date
    }

    fun setDate(date: Long) {
        this.date = date
    }
    /**
     * This method is used to show the material date picker card
     * @param childFrag: FragmentManager This is the childfragment of the fragment over which the card is shown
     * @param tv: TextView The textview object which is tapped to activate the card
     */
    fun openDatePicker(childFrag: FragmentManager, tv: TextView) {
        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date").build()
        datePicker.show(childFrag, "tag")
        datePicker.addOnPositiveButtonClickListener {
            var first = datePicker.headerText
            tv.text = "$first"
            this.date = datePicker.selection!!
        }
    }

    /**
     * This method formats a given string in different font styles,
     * here it is used to show a smaller AM-PM next to a bigger time
     */
    var spannableFactory = object : Spannable.Factory() {
        override fun newSpannable(source: CharSequence?): Spannable {
            val spannable = source!!.toSpannable()
            val len1 = source.split(" ")[0].length
            val len2 = source.split(" ")[1].length

            spannable.setSpan(AbsoluteSizeSpan(75), 0, len1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannable.setSpan(
                AbsoluteSizeSpan(40),
                len1,
                len1 + len2 + 1,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            return spannable
        }
    }

    /**
     * This method is used to show the material time picker card
     * @param childFrag: FragmentManager This is the childfragment of the fragment over which the card is shown
     * @param tv: TextView The textview object which is tapped to activate the card
     * @param ctx: android.content.Context Current context of the fragment is used to check the time format
     */
    fun openTimePicker(childFrag: FragmentManager, tv: TextView, ctx: Context) {
        // Show the material time picker
        val clockFormat = TimeFormat.CLOCK_12H
        // Show the current system time in the picker
        var hours: Int = SimpleDateFormat("h").format(time).toInt()
        var minutes: Int = SimpleDateFormat("mm").format(time).toInt()
        val ampm: String = SimpleDateFormat("a").format(time)
        if(ampm == "PM") hours += 12

        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(clockFormat)
            .setHour(hours)
            .setMinute(minutes)
            .setTitleText("Set Time to Remind")
            .build()
        picker.show(childFrag, "TAG")

        // Get the picked time
        picker.addOnPositiveButtonClickListener {
            tv.setSpannableFactory(spannableFactory)
            tv.setText(timeString(picker.hour,picker.minute, ctx),
                TextView.BufferType.SPANNABLE)
            this.time = datetime.timeInMillis
        }

    }

    /**
     * This method is used to convert the time from the material time picker into string
     * @param hour: Int Picked hour
     * @param minute: Int Picked minute
     * @param ctx: android.content.Context Current context of the fragment is used to check the time format
     * @return Returns the time from the picker in h:mm a format
     */
    private fun timeString(hour: Int, minute: Int, ctx: Context): String {
        val isSystem24Hour = DateFormat.is24HourFormat(ctx)
        datetime = Calendar.getInstance()
        Log.d("DT", datetime.toString())

        datetime.set(Calendar.HOUR_OF_DAY, hour)
        datetime.set(Calendar.MINUTE, minute)
        var am_pm = ""
        if (datetime.get(Calendar.AM_PM) == Calendar.AM)
            am_pm = "AM"
        else if (datetime.get(Calendar.AM_PM) == Calendar.PM)
            am_pm = "PM"
        var hour = datetime.get(Calendar.HOUR_OF_DAY)
        var minute = datetime.get(Calendar.MINUTE)
        if (!isSystem24Hour && hour > 12) {
            hour -= 12
        } else if (!isSystem24Hour && hour == 0) {
            hour = 12
        }
        var minString = ""
        minString = if (minute == 0)
            "00"
        else minute.toString()
        return "$hour:$minString $am_pm"
    }

}
