package com.example.memorygameandroid

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.button.MaterialButtonToggleGroup
import kotlinx.android.synthetic.main.fragment_set_up.*

class SetUpFragment : Fragment() {

    private var pairsCount: Int = 0
    private var winningCount: Int = 0
    private var model: Communicator ?= null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_set_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model = ViewModelProviders.of(activity!!).get(Communicator::class.java)
        setUpToggleGroups()
        setUpListeners()
    }

    private fun setUpToggleGroups() {
        toggleGroup.isSingleSelection = true
        toggleGroup.check(R.id.matching2)
        toggleGroupPairs.isSingleSelection = true
        toggleGroupPairs.check(R.id.pairs10)
    }

    private fun setUpListeners() {
        play_button.setOnClickListener {
            val checkedPairs = activity!!.findViewById<View>(toggleGroup.checkedButtonId) as Button
            pairsCount = checkedPairs.text.toString().toInt()
            val matchingTotal = activity!!.findViewById<View>(toggleGroupPairs.checkedButtonId) as Button
            winningCount = matchingTotal.text.toString().toInt()
            model!!.setMsgCommunicator(mapOf("pairCount" to pairsCount, "winningCount" to winningCount) as HashMap<String, Int>)
            val activity = activity!! as PlayActivity
            activity.loadFragment(R.id.play_menu)
        }
    }

    companion object {
        fun newInstance() =
            SetUpFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}
