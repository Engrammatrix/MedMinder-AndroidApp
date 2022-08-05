package com.dal.medminder.fragments.list

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dal.medminder.R
import com.dal.medminder.viewmodel.ReminderViewModel
import kotlinx.android.synthetic.main.fragment_list.view.*
/**
 * This class handles functionalities for each medication reminder card
 */
class ListFragment : Fragment() {
    private lateinit var reminderViewModel: ReminderViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Logic for showing the medication reminder cards in a recycler view list
        val view = inflater.inflate(R.layout.fragment_list, container, false)
        val adapter = ListAdapter(activity)
        val recyclerView = view.recyclerview
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        reminderViewModel = ViewModelProvider(this).get(ReminderViewModel::class.java)
        reminderViewModel.getReminders.observe(viewLifecycleOwner, Observer { reminder ->
            adapter.setData(reminder)
        })

        setHasOptionsMenu(true)
        return view
    }
    // Check if the create options menu is selected, i.e., the add button is tapped
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_menu, menu)
    }

    // When the add button is tapped, navigate to the add fragment
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_add) {
            findNavController().navigate(R.id.action_listFragment_to_addFragment)
        }
        return super.onOptionsItemSelected(item)
    }

}