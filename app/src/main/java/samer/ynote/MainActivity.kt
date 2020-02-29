package samer.ynote

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import samer.ynote.MainActivity
import java.util.*

class MainActivity : AppCompatActivity() {
    //RecyclerView:
    private var recyclerView: RecyclerView? = null
    private var adapterRecyclerView: AdapterForRecyclerView? = null
    private val layoutManager: RecyclerView.LayoutManager? = null
    private var databaseHelper: DatabaseHelper? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupRecyclerView()
        databaseHelper = DatabaseHelper(this)
        //list_notes.clear();
        list_notes.addAll(databaseHelper!!.allItemsFromDatabase)
    } //END OF OnCreate

    private fun setupRecyclerView() { // Lookup the recyclerView in activity layout
        recyclerView = findViewById<View>(R.id.recyclerView_Notes) as RecyclerView
        // use this setting to improve performance if you know that changes
// in content do not change the layout size of the RecyclerView
        recyclerView!!.setHasFixedSize(true)
        // Create adapter passing in the sample user data
        adapterRecyclerView = AdapterForRecyclerView(this)
        // Attach the adapter to the recyclerView to populate items
        recyclerView!!.adapter = adapterRecyclerView
        // Set layout manager to position the items
        recyclerView!!.layoutManager = LinearLayoutManager(this)
        recyclerView!!.addOnItemTouchListener(
            RecyclerViewTouchListener(
                this,
                recyclerView,
                object : RecyclerViewTouchListener.OnItemClickListener {
                    //On normal click, edit note:
                    override fun onItemClick(
                        view: View,
                        position: Int
                    ) {
                        updateNote(position)
                    }

                    //On long click, delete note:
                    override fun onItemLongClick(
                        view: View,
                        position: Int
                    ) {
                        //Last chance for user to confirm if they want to delete note:
                        AlertDialog.Builder(this@MainActivity)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Delete selected note?")
                            .setMessage("Action cannot be undone.")
                            .setPositiveButton(
                                "Yes"
                            ) { dialogInterface, i -> deleteNote(position) }
                            .setNegativeButton("No", null).show()
                    } //END onItemLongClick
                })
        )
    }

    fun deleteNote(listIndex: Int) {
        try {
            val note = list_notes[listIndex]
            note.listIndex = listIndex
            databaseHelper!!.deleteItem(note) //remove from the database
            list_notes.removeAt(listIndex) //remove from the ArrayList
            adapterRecyclerView!!.notifyDataSetChanged()
        } catch (e: Exception) {}
    }

    fun createNote() {
        var noteID: Long = -1
        try {
            val listIndex =
                list_notes.size //index of new note is obv current size of list
            val note = Note()
            note.listIndex = listIndex
            list_notes.add(note)
            noteID = databaseHelper!!.addItem(note)
            note.iD = noteID
            startTextEditor(note)
        } catch (e: Exception) {}
    }

    fun updateNote(listIndex: Int) {
        try {
            val note = list_notes[listIndex]
            note.listIndex = listIndex
            startTextEditor(note)
        } catch (e: Exception) { }
    }

    fun startTextEditor(note: Note?) {
        val intent = Intent(this, NoteEditorActivity::class.java)
        intent.putExtra("note", note)
        onPause()
        startActivityForResult(intent, 1)
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) { //Gets note from NoteEditorActivity
                val note = data!!.getSerializableExtra("note") as Note
                val listIndex = note.listIndex //TODO NPE
                val title = note.title
                //If note is empty, set to default string
                if (note.note.isEmpty()) {
                    note.note = "Empty"
                }
                //If title is empty, set to num of note
                if (title.isEmpty()) {
                    note.title = "Note " + list_notes.size
                } else if (title.length > 25) {
                    note.title = title.substring(0, 25)
                }
                //Update SQLite database_notes
                databaseHelper!!.updateItem(note)
                //Update List
                list_notes[listIndex] = note
                //Update Recycler View Adapter
                adapterRecyclerView!!.notifyItemChanged(listIndex)
                adapterRecyclerView!!.notifyDataSetChanged()
            }
            if (resultCode == Activity.RESULT_CANCELED) {Toast.makeText(this, "Error. Note was not saved.", Toast.LENGTH_LONG)
                    .show()}
        }
    } //onActivityResult

    override fun onBackPressed() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        finish()
        startActivity(intent)
    }

    fun button_createNote(view: View?) { createNote()
    }

    fun button_delete(view: View?) { databaseHelper!!.deleteAllItemsFromDatabase()
    }

    companion object {
        //General:
        private const val TAG = "MainActivity"
        //Data:
        @JvmField
        var list_notes: MutableList<Note> =
            ArrayList()
    }
}