package com.naicoits.trainingkotlin

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.ReturnCode
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.util.*
import kotlin.random.Random


class MainActivity : AppCompatActivity() {
    val TAG= "FFMPEG EXECUTION"
    val REQUEST_CODE = 1;
    var outputVideoPath = "";
    var selectedVideoPath = "";
    var selectedWaterMark = "";
    var inputImage = "";
    val pathInputImg="file:///storage/emulated/0/Download/";
    val pathInputImg2="/storage/emulated/0/Download/";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val languages = resources.getStringArray(R.array.WaterMarks)

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item, languages
        )
        waterMarkSpinner.adapter = adapter

        waterMarkSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                selectedWaterMark = languages[position]
                if(selectedWaterMark=="Test Image"){
                    inputImage="TestImage.png"
                }else if(selectedWaterMark=="Insta"){
                    inputImage="Insta.png"
                }else if(selectedWaterMark=="Apple"){
                    inputImage="Apple.png"
                }else if(selectedWaterMark=="Netflix"){
                    inputImage="Netflix.png"
                }
                val imgFile="${pathInputImg2}${inputImage}"
                Log.i("Value +++++++++++++++",imgFile);
                val imgFil = File(imgFile);
                val myBitmap = BitmapFactory.decodeFile(imgFil.getAbsolutePath())
                inputImageView.setImageBitmap(myBitmap);
//                inputImageView.setImageURI(Uri.fromFile(imgFil));
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        buttonExecute.setOnClickListener {
            if(selectedVideoPath!=="") {
                executeFunc(selectedVideoPath, inputImage);
                Toast.makeText(this, "processing.....", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this, "select one video first", Toast.LENGTH_SHORT).show()
            }
        }

        buttonAddVideo.setOnClickListener {
            openVideo();
            Toast.makeText(this, "clicked video", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openImage() {
        TODO("Not yet implemented")
    }

    private fun openVideo() {
        val intent = Intent()
        intent.type = "video/*"
        intent.action = Intent.ACTION_PICK
        startActivityForResult(Intent.createChooser(intent, "Select Video"), REQUEST_CODE)
    }

    private fun executeFunc(pathInVdo: Any, inputImg: Any) {
        val randomInts = Random.nextInt()
        val pathOut="file:///storage/emulated/0/Download/TestFolder/";
        val Vformat=".mp4"
        outputVideoPath="${pathOut}${randomInts}${Vformat}"

        val session = FFmpegKit.execute("-i ${pathInVdo} -i ${pathInputImg}${inputImg} -filter_complex [1:v]scale=70:50,format=rgba,colorchannelmixer=aa=0.5[ovr1];[0:v][ovr1]overlay=(main_w-overlay_w):(main_h-overlay_h) -c:a copy ${pathOut}${randomInts}${Vformat}")
        if (ReturnCode.isSuccess(session.returnCode)) {

            // SUCCESS

            Log.d(
                TAG,
                String.format(
                    "Command success with state %s and rc %s.%s",
                    session.state,
                    session.returnCode,
                    session.failStackTrace
                )
            )
            outputVideo.setVideoPath(outputVideoPath)
            outputVideo.start();

        } else if (ReturnCode.isCancel(session.returnCode)) {

            // CANCEL
        } else {

            // FAILURE
            Log.d(
                TAG,
                String.format(
                    "Command failed with state %s and rc %s.%s",
                    session.state,
                    session.returnCode,
                    session.failStackTrace
                )
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE) {
            if (data?.data != null) {
                val uriPathHelper = URIPathHelper()
                val videoFullPath = uriPathHelper.getPath(this, data?.data!!) // Use this video path according to your logic
                // if you want to play video just after picking it to check is it working
                if (videoFullPath != null) {
                    Log.i("video =================", videoFullPath)
                    Log.i("video =================", data?.data!!.toString())
//                    playVideoInDevicePlayer(videoFullPath);
                    selectedVideoPath="file://${videoFullPath}";
                    inputVideo.setVideoPath(selectedVideoPath)
                    inputVideo.start()
                }
            }
        }
    }

    fun playVideoInDevicePlayer(videoPath: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(videoPath))
        intent.setDataAndType(Uri.parse(videoPath), "video/mp4")
        startActivity(intent)
    }

}