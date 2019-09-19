package com.example.memorygameandroid

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_set_up.*

class SetUpFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_set_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpToggleGroup()
    }

    private fun setUpToggleGroup() {
        toggleGroup.isSingleSelection = true
        toggleGroup.check(R.id.matching2)
    }

    companion object {
        fun newInstance() =
            SetUpFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}
