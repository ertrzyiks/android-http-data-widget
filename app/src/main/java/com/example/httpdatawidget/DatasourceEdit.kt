package com.example.httpdatawidget

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

private const val ARG_ITEM_ID = "itemId"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [DatasourceEdit.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [DatasourceEdit.newInstance] factory method to
 * create an instance of this fragment.
 */
class DatasourceEdit : Fragment() {
    private var itemId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            itemId = it.getLong(ARG_ITEM_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        println("On Create View")
        println(itemId)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_datasource_edit, container, false)
    }

    companion object {

        @JvmStatic
        fun newInstance(itemId: Long) =
            DatasourceEdit().apply {
                arguments = Bundle().apply {
                    putLong(ARG_ITEM_ID, itemId)
                }
            }
    }
}
