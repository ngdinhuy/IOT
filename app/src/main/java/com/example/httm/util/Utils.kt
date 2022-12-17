package com.example.httm.util

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore

class Utils {
    companion object{
        fun getRealPathFromURI(context:Context,contentURI: Uri): String? {
            val result: String
            val cursor: Cursor = context.getContentResolver().query(contentURI, null, null, null, null)!!
            if (cursor == null) { // Source is Dropbox or other similar local file path
                result = contentURI.getPath()!!
            } else {
                cursor.moveToFirst()
                val idx: Int = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                result = cursor.getString(idx)
                cursor.close()
            }
            return result
        }
    }

}