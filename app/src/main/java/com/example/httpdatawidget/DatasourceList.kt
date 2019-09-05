package com.example.httpdatawidget

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.example.httpdatawidget.storage.DatasourceInfo
import com.example.httpdatawidget.storage.DatasourceInfoBase
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlin.properties.Delegates


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [DatasourceList.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [DatasourceList.newInstance] factory method to
 * create an instance of this fragment.
 */
class DatasourceList : Fragment() {
    private var db: DatasourceInfoBase? = null
    private var listView: ListView by Delegates.notNull()

    internal val onButtonClick = View.OnClickListener {
        Thread(Runnable {
            var item = DatasourceInfo()
            db!!.datasourceInfoDao().insert(item)
        }).start()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = DatasourceInfoBase.getInstance(context!!)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_datasource_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listView = view.findViewById(R.id.list)

        listView.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = listView.adapter.getItem(position) as DatasourceInfo
            val action = DatasourceListDirections.actionDatasourceListToDatasourceEdit(selectedItem.id!!)
            Navigation.findNavController(parent).navigate(action)
        }

        val adapter = DatasourceListAdapter(context!!, R.layout.list_item)
        listView.adapter = adapter

        db!!.datasourceInfoDao().getAll().observe(this, Observer {
            adapter.clear()
            adapter.addAll(it)
        })

        var el: FloatingActionButton = view.findViewById(R.id.add_button)
        el.setOnClickListener(onButtonClick)
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }
}
