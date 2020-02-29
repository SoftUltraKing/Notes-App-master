package samer.ynote

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class NoteEditorActivity : AppCompatActivity() {
    //Data:
    private var note: Note? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_editor)
        note = intent.getSerializableExtra("note") as Note
        //Title:
        val editText_title = findViewById<EditText>(R.id.editText_title)
        editText_title.setText(note!!.title)
        //Note:
        val editText_note = findViewById<EditText>(R.id.editText_note)
        editText_note.setText(note!!.note)
        //Checks for text changes for title:
        editText_title.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {
            }

            //Used to save text entered by the user as it is being entered
            override fun onTextChanged(
                charSequence: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) { //Calls checkTitleLength
                note!!.title = charSequence.toString()
                Log.i(TAG, "Title: $charSequence")
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        //Checks for text changes for note:
        editText_note.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {
            }

            //Used to save text entered by the user as it is being entered
            override fun onTextChanged(
                charSequence: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
                note!!.note = charSequence.toString()
                Log.i(TAG, "Note: :$charSequence")
            }

            override fun afterTextChanged(editable: Editable) {}
        })
    }

    //Returns note and navigates back to MainActivity:
    override fun onBackPressed() {
        Log.i(TAG, "onBackPressed Called")
        val returnIntent = Intent()
        returnIntent.putExtra("note", note)
        Log.d(
            TAG,
            "putExtra: " + note.toString()
        ) //TODO REMOVE after testing
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }

    companion object {
        //General:
        private const val TAG = "NoteEditorActivity"
    }
}