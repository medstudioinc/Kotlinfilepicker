package com.androidbuffer.kotlinfilepickersample

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.androidbuffer.kotlinfilepicker.KotConstants
import com.androidbuffer.kotlinfilepicker.KotRequest
import com.androidbuffer.kotlinfilepicker.KotUtil
import java.io.File

class MainActivity : AppCompatActivity(), PickerAdapter.OnClickItemListener {

    val EXTRA_IMAGE_URI = "EXTRA_IMAGE_URI"
    lateinit var rvFilePickerMain: RecyclerView
    private val REQUEST_CAMERA = 101
    private val REQUEST_GALLERY = 102
    private val REQUEST_FILE = 103
    private val REQUEST_VIDEO = 104
    private var adapter: PickerAdapter? = null
    lateinit var titleArray: Array<String>

    private val drawableArray = arrayOf(R.drawable.ic_action_gallery,
            R.drawable.ic_action_gallery,
            R.drawable.ic_action_file,
            R.drawable.ic_action_file,
            R.drawable.ic_action_photo_camera,
            R.drawable.ic_action_camera)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //init the view elements
        rvFilePickerMain = findViewById(R.id.rvFilePickerMain);
        titleArray = resources.getStringArray(R.array.arrayOptions)
        setRecyclerView()
    }

    private fun setRecyclerView() {
        //here set the recycler view
        adapter = PickerAdapter(titleArray, drawableArray, this)
        rvFilePickerMain.layoutManager = LinearLayoutManager(this)
        rvFilePickerMain.adapter = adapter
    }

    private fun openCamera() {
        //opens camera from camera class
        KotRequest.Camera(this, REQUEST_CAMERA).pick()
    }

    private fun openVideo() {
        //opens a camera intent
        KotRequest.Video(this, REQUEST_VIDEO).getVideoIntent()
    }

    private fun openGallery(isMultiple: Boolean) {
        //opens a gallery intent
        KotRequest.Gallery(this, REQUEST_GALLERY).isMultiple(isMultiple).pick()
    }

    private fun openFile(isMultiple: Boolean) {
        //opens a file intent
        KotRequest.File(this, REQUEST_FILE).isMultiple(isMultiple).pick()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (REQUEST_CAMERA == requestCode && resultCode == Activity.RESULT_OK) {
            val uri = data?.getParcelableArrayListExtra<Uri?>(KotConstants.EXTRA_FILE_RESULTS)
            val intent = Intent(this, GalleryActivity::class.java)
            intent.putExtra(EXTRA_IMAGE_URI, uri)
            startActivity(intent)
        } else if (REQUEST_FILE == requestCode && resultCode == Activity.RESULT_OK) {
            val uri = data?.getParcelableArrayListExtra<Uri?>(KotConstants.EXTRA_FILE_RESULTS)
            val fileDetails = KotUtil.getFileDetails(this, uri?.get(0)!!)
            val messageBuilder = StringBuilder(" Name = ${fileDetails?.name}")
                    .append("\n size = ${fileDetails?.length()!! / 1024}")
            openDetailsDialog(messageBuilder.toString(), "File Details")
        } else if (REQUEST_GALLERY == requestCode && resultCode == Activity.RESULT_OK) {
            val uri = data?.getParcelableArrayListExtra<Uri?>(KotConstants.EXTRA_FILE_RESULTS)
            val intent = Intent(this, GalleryActivity::class.java)
            intent.putExtra(EXTRA_IMAGE_URI, uri)
            startActivity(intent)
        } else if (REQUEST_VIDEO == requestCode && resultCode == Activity.RESULT_OK) {
            val uri = data?.getParcelableArrayListExtra<Uri?>(KotConstants.EXTRA_FILE_RESULTS)
            val fileDetails = KotUtil.getFileDetails(this, uri?.get(0)!!)
            val messageBuilder = StringBuilder(" Name = ${fileDetails?.name}")
                    .append("\n size = ${fileDetails?.length()!! / 1024}")
            openDetailsDialog(messageBuilder.toString(), "Video Details")
        }
    }

    override fun onItemClick(position: Int) {
        //listener for items in adapter
        when (position) {
            0 -> openGallery(false)
            1 -> openGallery(true)
            2 -> openFile(false)
            3 -> openFile(true)
            4 -> openCamera()
            5 -> openVideo()
        }
    }

    fun openDetailsDialog(message: String, title: String) {
        AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Ok") { p0, p1 -> p0.dismiss() }
                .create()
                .show()
    }
}
