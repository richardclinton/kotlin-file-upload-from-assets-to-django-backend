package com.beanworth.mycms.ui

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log

class UriPathHelper {
    fun getPath(context:Context, uri:Uri):String?{
        val isKitKatorAbove = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
        Log.i("IsKitKat",isKitKatorAbove.toString())
        Log.i("DocumentContract",DocumentsContract.isDocumentUri(context,uri).toString())
        Log.i("DocumentUri",uri.toString())
//        Document Provider
        if (isKitKatorAbove && DocumentsContract.isDocumentUri(context,uri)){
//            External Storage provider
            if (isExternalStorageDocument(uri)){
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)){
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }
            }
            else if (isDownloadDocument(uri)){
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
                java.lang.Long.valueOf(id))
                return getDataColumn(context, contentUri, null, null)
            }
            else if (isMediaDocument(uri)){
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).toTypedArray()
                val type = split[0]
                var contentUri:Uri? = null

                if ("image" == type){
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                }
                else if ("video" == type){
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                }
                else if ("audio" == type){
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])

                return getDataColumn(context, contentUri, selection, selectionArgs)
            }
            else if ("content".equals(uri.scheme, ignoreCase = true)){
                return getDataColumn(context, uri, null, null)
            }
            else if("file".equals(uri.scheme, ignoreCase = true)){
                return uri.path
            }
        }
        return null
    }

    fun getDataColumn(context: Context, uri: Uri?, selection:String?,
    selctionArgs:Array<String>?):String?{

        var cursor:Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)
        try {
            cursor = context.contentResolver.query(uri!!, projection,selection,
            selctionArgs, null)

            if (cursor != null && cursor.moveToFirst()){
                val column_index:Int = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(column_index)
            }
        }finally {
            if (cursor != null) cursor.close()
        }
        return null
    }

    fun isExternalStorageDocument(uri: Uri):Boolean{
        return "com.android.externalstorage.documents" == uri.authority
    }

    fun  isDownloadDocument(uri: Uri):Boolean{
        return "com.android.providers.downloads.documents" == uri.authority
    }

    fun  isMediaDocument(uri: Uri):Boolean{
        return "com.android.providers.media.documents" == uri.authority
    }
}