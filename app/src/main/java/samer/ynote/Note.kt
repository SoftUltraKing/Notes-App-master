package samer.ynote

import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

class Note : Serializable {
    var iD: Long = 0
    var title: String
    var note: String
    var timestamp: String
    var listIndex: Int

    internal constructor(title: String, note: String) {
        this.title = title
        this.note = note
        timestamp = timestamp()
        listIndex = 0
    }

    internal constructor() {
        title = ""
        note = ""
        timestamp = timestamp()
        listIndex = 0
    }

     fun timestamp(): String {
        var timeStampFormatted =
            SimpleDateFormat("MM/dd/yyyy").format(Date())
        timeStampFormatted += " "
        timeStampFormatted += SimpleDateFormat("HH:mm").format(Date())
        return timeStampFormatted
    }

    override fun toString(): String {
        return "Note{" +
                "ID=" + iD +
                ", title='" + title + '\'' +
                ", note='" + note + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}'
    }
}