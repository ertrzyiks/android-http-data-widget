package com.example.httpdatawidget

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import com.example.httpdatawidget.storage.DatasourceInfo
import java.net.URI


class DatasourceListAdapter : ArrayAdapter<DatasourceInfo> {
    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    constructor(context: Context, resource: Int) : super(context, resource) {}

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: inflater.inflate(R.layout.list_item, parent, false)

        val item = getItem(position)
        val nameTextView = view.findViewById<TextView>(R.id.listitem_name)
        val domainTextView = view.findViewById<TextView>(R.id.listitem_domain)
        val imageView = view.findViewById<ImageView>(R.id.imageView)

        nameTextView.text = item?.name
        domainTextView.text = getDomain(item?.url)

        if (domainTextView.text == "") {
            domainTextView.visibility = View.GONE
        } else {
            domainTextView.visibility = View.VISIBLE
        }

        if (item?.online == true) {
            imageView.setImageResource(R.drawable.ic_cloud_done_black_24dp)
        } else {
            imageView.setImageResource(R.drawable.ic_cloud_off_black_24dp)
        }
        return view
    }

    private fun getDomain(url: String?): String {
        if (url === null) {
            return ""
        }

        try {
            val uri = URI(url)
            return uri.host
        } catch (e: Exception) {
            return ""
        }

        return ""
    }
}