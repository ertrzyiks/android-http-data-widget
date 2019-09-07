package com.example.httpdatawidget

import android.app.Activity
import android.os.AsyncTask
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import com.google.android.material.textfield.TextInputEditText
import java.lang.Exception
import android.widget.*
import com.example.httpdatawidget.storage.DatasourceInfo
import com.example.httpdatawidget.storage.DatasourceInfoBase

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
    private lateinit var datasourceInfo: DatasourceInfo
    private var db: DatasourceInfoBase? = null

    private var requestVerified = true

    private lateinit var nameField: TextInputEditText
    private lateinit var urlField: TextInputEditText
    private lateinit var saveButton: Button
    private lateinit var testConnectionButton: Button

    private lateinit var responseCodeValue: TextView
    private lateinit var responseCodeIcon: ImageView
    private lateinit var contentTypeValue: TextView
    private lateinit var contentTypeIcon: ImageView
    private lateinit var contentValue: TextView
    private lateinit var contentIcon: ImageView

    private lateinit var scrollView: ScrollView

    private lateinit var progressBar: ProgressBar
    private lateinit var table: TableLayout

    private var lastValidate: LoadData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            itemId = it.getLong(ARG_ITEM_ID)
        }

        db = DatasourceInfoBase.getInstance(context!!)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_datasource_edit, container, false)

        scrollView = view.findViewById(R.id.datasource_edit_scroll_view)

        nameField = view.findViewById(R.id.field_datasource_name)
        nameField.setOnFocusChangeListener(onFocusChange)

        urlField = view.findViewById(R.id.field_datasource_url)
        urlField.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(text: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(text: Editable?) {
                requestVerified = false
            }
        })

        saveButton = view.findViewById(R.id.save_button)
        saveButton.setOnClickListener(onSave)

        testConnectionButton = view.findViewById(R.id.test_connection_button)
        testConnectionButton.setOnClickListener(onTestConnection)

        responseCodeValue = view.findViewById(R.id.response_code_value)
        responseCodeIcon = view.findViewById(R.id.response_code_icon)
        contentTypeValue = view.findViewById(R.id.content_type_value)
        contentTypeIcon = view.findViewById(R.id.content_type_icon)
        contentValue = view.findViewById(R.id.content_value)
        contentIcon = view.findViewById(R.id.content_icon)

        progressBar = view.findViewById(R.id.response_grade_progress_bar)
        table = view.findViewById(R.id.response_grade_table)

        table.visibility = View.INVISIBLE

        Thread(Runnable {
            datasourceInfo = db!!.datasourceInfoDao().findById(itemId!!)

            getActivity()?.runOnUiThread {
                nameField.setText(datasourceInfo!!.name)
                urlField.setText(datasourceInfo!!.url)

                saveButton.isEnabled = true
                testConnectionButton.isEnabled = true
                requestVerified = true
            }
        }).start()

        return view
    }


    internal fun serializeForm () {
        datasourceInfo.name = nameField.text.toString()
        datasourceInfo.url = urlField.text.toString()
    }

    internal fun validateUrl(callback: () -> Unit) {
        requestVerified = false
        progressBar.visibility = View.VISIBLE
        table.visibility = View.INVISIBLE

        lastValidate = LoadData(context!!, object: LoadDataCallback<Response>{
            override fun onSuccess(value: Response) {
                val grader = ResponseGrader(value)
                responseCodeValue.setText(value.statusCode.toString())
                responseCodeIcon.setImageResource(getResponseCodeIcon(grader))

                contentTypeValue.setText(value.contentType)
                contentTypeIcon.setImageResource(getContentTypeIcon(grader))

                contentValue.setText(grader.contentSample())
                contentIcon.setImageResource(getContentIcon(grader))

                requestVerified = grader.isStatusCodeOk()
            }

            override fun onFailure(e: Exception) {
                responseCodeValue.setText("?")
                responseCodeIcon.setImageResource(R.drawable.ic_warning_black_24dp)
                contentTypeValue.setText("?")
                contentTypeIcon.setImageResource(R.drawable.ic_warning_black_24dp)
                contentValue.setText(e.message)
                contentIcon.setImageResource(R.drawable.ic_warning_black_24dp)
            }

            override fun onDone() {
                table.visibility = View.VISIBLE
                progressBar.visibility = View.GONE

                callback.invoke()
            }
        })

        lastValidate?.execute(datasourceInfo)
    }

    internal val onTestConnection = View.OnClickListener {
        hideSoftKeyboard()
        serializeForm()

        validateUrl {
            scrollToElement(responseCodeValue)
        }
    }

    internal fun getResponseCodeIcon(grader: ResponseGrader): Int {
        if (grader.isStatusCodeOk()) {
            return R.drawable.ic_check_black_24dp
        }

        return R.drawable.ic_warning_black_24dp
    }

    internal fun getContentTypeIcon(grader: ResponseGrader): Int {
        if (grader.isContentTypeOk()) {
            return R.drawable.ic_check_black_24dp
        }

        return R.drawable.ic_warning_black_24dp
    }

    internal fun getContentIcon(grader: ResponseGrader): Int {
        if (grader.isContentOk()) {
            return R.drawable.ic_check_black_24dp
        }

        return R.drawable.ic_warning_black_24dp
    }

    internal val saveListener = object: LoadDataCallback<Response>{
        override fun onSuccess(value: Response) {
            val grader = ResponseGrader(value)
            datasourceInfo.online = grader.isStatusCodeOk()
        }

        override fun onFailure(e: Exception) {
            datasourceInfo.online = false
        }

        override fun onDone() {
            progressBar.visibility = View.GONE

            Thread(Runnable {
                db!!.datasourceInfoDao().update(datasourceInfo)

                getActivity()?.runOnUiThread {
                    Toast.makeText(context, "Saved!", Toast.LENGTH_SHORT)
                }
            }).start()
        }
    }

    internal val onSave = View.OnClickListener {
        hideSoftKeyboard()
        serializeForm()

        lastValidate?.cancel(true)

        if (requestVerified) {
            datasourceInfo.online = true
            saveListener.onDone()
        }
        else {
            table.visibility = View.INVISIBLE
            progressBar.visibility = View.VISIBLE

            LoadData(context!!, saveListener).execute(datasourceInfo)
        }
    }

    internal val onFocusChange = View.OnFocusChangeListener { view, focused ->
        if (focused) {
            if (nameField.text.toString() == DatasourceInfo.defaultName) {
                nameField.setText("")
            }
        } else {
            if (nameField.text.toString() == "") {
                nameField.setText(DatasourceInfo.defaultName)
            }
        }
    }

    internal fun scrollToElement(view: View) {
        scrollView.post {
            scrollView.smoothScrollTo(0, view.bottom)
        }
    }

    fun hideSoftKeyboard() {
        val inputMethodManager = activity!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager

        if (activity!!.currentFocus !== null) {
            inputMethodManager.hideSoftInputFromWindow(activity!!.currentFocus!!.windowToken, 0)
        }
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
