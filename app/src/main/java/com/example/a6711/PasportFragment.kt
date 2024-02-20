package com.example.a6711

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.concurrent.futures.await
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.a6711.database.PassportDatabase
import com.example.a6711.databinding.FragmentPasportBinding
import com.example.a6711.entitiy.PassportEntity
import com.example.a6711.entitiy.RegionEntity
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PasportFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PasportFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var binding: FragmentPasportBinding
    private var imageUri: Uri?=null
    private var imageCapture:ImageCapture?=null
    private val TAG = "CameaXApp"
    private val FILNAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    lateinit var passportDatabase: PassportDatabase
    var serializable:PassportEntity?=null
    

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPasportBinding.inflate(inflater,container,false)

        passportDatabase = PassportDatabase.getInstance(requireContext())
        try {
            serializable = arguments?.getSerializable("key") as PassportEntity
        }catch  (e:Exception){

        }


        addRegion()
        sexSpinner()
        regionspinner()
        if (serializable!=null){

            imageUri = serializable?.image?.toUri()
            binding.image.setImageURI(serializable?.image?.toUri())
            binding.lastName.setText(serializable?.lastName)
            binding.firstName.setText(serializable?.firstName)
            binding.fatherName.setText(serializable?.fatherName)
            binding.viloyatSpinner.setSelection(serializable?.region!!)
            binding.shaxarName.setText(serializable?.city)
            binding.uyManzil.setText(serializable?.homeAddress)
            binding.givenTime.setText(serializable?.passportGivenTime)
            binding.termTime.setText(serializable?.passportTakeTime)
            if (serializable?.sex=="Erkak"){
            binding.sex.setSelection(1)
            }else if (serializable?.sex=="Ayol"){
                binding.sex.setSelection(2)
            }
            binding.btnSaqlash.setText("O'zgarishlarni saqlash")


        }









        binding.btnSaqlash.setOnClickListener{

            var image = imageUri
            var lastName = binding.lastName.text.trim().toString()
            var firstName = binding.firstName.text.trim().toString()
            var fatherName = binding.fatherName.text.trim().toString()
            var viloyat:Int = binding.viloyatSpinner.selectedItemPosition
            var shaxar = binding.shaxarName.text.trim().toString()
            var uy = binding.uyManzil.text.trim().toString()
            var givenTime = binding.givenTime.text.trim().toString()
            var termTime = binding.termTime.text.trim().toString()
            var sex:Int = binding .sex.selectedItemPosition

            if (image!=null&&
                lastName.isNotEmpty()&&
                firstName.isNotEmpty()&&
                fatherName.isNotEmpty()&&
                viloyat!=0&&
                shaxar.isNotEmpty()&&
                uy.isNotEmpty()&&
                givenTime.isNotEmpty()&&
                termTime.isNotEmpty()&&
                sex!=0){


                if (sex==1){
                    passportDatabase.passportDao().addPassport(PassportEntity(null,
                        image.toString(),
                        lastName,
                        firstName,
                        fatherName,
                        viloyat,
                        shaxar,
                        uy,
                        givenTime,
                        termTime,
                        "Erkak"    ))

                } else {
                    passportDatabase.passportDao().addPassport(PassportEntity(null,
                        image.toString(),
                        lastName,
                        firstName,
                        fatherName,
                        viloyat,
                        shaxar,
                        uy,
                        givenTime,
                        termTime,
                        "Ayol"    ))
                }


                Toast.makeText(binding.root.context, "Ma'lumot saqlandi", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_pasportFragment_to_homFragment)

            }else Toast.makeText(binding.root.context, "Iltimos barcha maydonlar to'g'ri to'ldirilganligini qayta tekshiring", Toast.LENGTH_SHORT).show()

        }




        binding.btnAddImage.setOnClickListener {


            startcamera(android.Manifest.permission.CAMERA)

        }

        var count = false

        binding.btnCameraFront.setOnClickListener {


            if (count){
                lifecycleScope.launch {
                    startCamera(binding.previewViewCamera, CameraSelector.DEFAULT_BACK_CAMERA)
                }
                count = false
            }else{
                lifecycleScope.launch {
                    startCamera(binding.previewViewCamera, CameraSelector.DEFAULT_FRONT_CAMERA)
                }
                count = true
            }


        }

        binding.btnCamera.setOnClickListener {
            takePhoto()
            binding.layout1.visibility = View.VISIBLE
            binding.layout2.visibility = View.GONE
        }

        binding.btnCameraGallery.setOnClickListener {
            openGallery(android.Manifest.permission.READ_MEDIA_IMAGES)
            binding.layout1.visibility = View.VISIBLE
            binding.layout2.visibility = View.GONE


        }


        return binding.root
    }

    private fun regionspinner() {
        var list = ArrayList<String>()

        for (i in passportDatabase.regionDao().getAllRegionString())   {
            list.add(i.regionName.toString())
        }
        var adapter = ArrayAdapter(requireContext(),android.R.layout.simple_spinner_item,list)
        binding.viloyatSpinner.adapter = adapter

    }

    private fun addRegion() {
        if (passportDatabase.regionDao().getAllRegionString().isEmpty()){
            passportDatabase.regionDao().addRegion(RegionEntity(null,"Viloyat nomini tanlang"))
            passportDatabase.regionDao().addRegion(RegionEntity(null,"Andijon viloyati"))
            passportDatabase.regionDao().addRegion(RegionEntity(null,"Buxoro viloyati"))
            passportDatabase.regionDao().addRegion(RegionEntity(null,"Fargʻona viloyati"))
            passportDatabase.regionDao().addRegion(RegionEntity(null,"Jizzax viloyati"))
            passportDatabase.regionDao().addRegion(RegionEntity(null,"Xorazm viloyati"))
            passportDatabase.regionDao().addRegion(RegionEntity(null,"Namangan viloyati"))
            passportDatabase.regionDao().addRegion(RegionEntity(null,"Navoiy viloyati"))
            passportDatabase.regionDao().addRegion(RegionEntity(null,"Qashqadaryo viloyati"))
            passportDatabase.regionDao().addRegion(RegionEntity(null,"Qoraqalpogʻiston Respublikasi"))
            passportDatabase.regionDao().addRegion(RegionEntity(null,"Samarqand viloyati"))
            passportDatabase.regionDao().addRegion(RegionEntity(null,"Sirdaryo viloyati"))
            passportDatabase.regionDao().addRegion(RegionEntity(null,"Surxondaryo viloyati"))
            passportDatabase.regionDao().addRegion(RegionEntity(null,"Toshkent viloyati"))


        }
    }

    private fun sexSpinner() {
        var spinadapter = ArrayAdapter<String>(requireContext(),android.R.layout.simple_spinner_item,
            arrayOf("Jinsni tanlang", "Erkak","Ayol")
        )
        binding.sex.adapter = spinadapter
    }


    private fun startcamera(permission: String) {
        if (!checkReadContactsPermission(binding.root.context, permission)) {

            requestPermissions(permission)
        }else{
            binding.layout1.visibility = View.GONE
            binding.layout2.visibility = View.VISIBLE
            lifecycleScope.launch {
                startCamera(binding.previewViewCamera,CameraSelector.DEFAULT_BACK_CAMERA)
            }


        }

    }


    private fun openGallery(permission: String) {

        if (!checkReadContactsPermission(binding.root.context, permission))
        {
            requestPermissions(permission)
        }else {
            val pickImg = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            changeImage.launch(pickImg)
        }
    }

    private val changeImage =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                val data = it.data
                //qaytaradigan qiymat URI
                imageUri = data?.data
                binding.image.setImageURI(imageUri)
            }
        }



    private suspend fun startCamera(previewViewCamera: PreviewView, cameraSelector: CameraSelector){
        val cameraProvider = ProcessCameraProvider.getInstance(binding.root.context).await()

        val preview = Preview.Builder().build()

        preview.setSurfaceProvider(previewViewCamera.surfaceProvider) // TODO: 111111

        imageCapture = ImageCapture.Builder().build()


        try {

            cameraProvider.unbindAll()
            var camera = cameraProvider.bindToLifecycle(
                this,cameraSelector,preview,imageCapture
            )

        } catch (e:Exception){
            Toast.makeText(binding.root.context, "UseCase-ni ulash amalga oshmadi", Toast.LENGTH_SHORT).show()
        }

    }





    fun takePhoto(){

        val name = SimpleDateFormat(FILNAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())


        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME,name)
            put(MediaStore.MediaColumns.MIME_TYPE,"image/jpeg")
            if (Build.VERSION.SDK_INT> Build.VERSION_CODES.P){
                put(MediaStore.Images.Media.RELATIVE_PATH,"Pictures/CameraX-Image")
            }
        }

        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(binding.root.context.contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues)
            .build()


        imageCapture?.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(binding.root.context),
            object : ImageCapture.OnImageSavedCallback{

                override fun onError(exception: ImageCaptureException) {
                    Log.e(TAG, "suratga olishdagi xatolik: ${exception.message}",exception )
                }

                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val msg = "Suratga olish muvaffaqiyatli yakunlandi${outputFileResults.savedUri}"
                    imageUri = outputFileResults.savedUri
                    binding.image.setImageURI(outputFileResults.savedUri)       // TODO: o'rnatiladigan rasim joyini tanlang
                    Toast.makeText(binding.root.context, "$msg", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "$msg")
                }



            }
        )

    }


    fun checkReadContactsPermission(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    fun requestPermissions(permission: String) {

        if (permission==android.Manifest.permission.READ_MEDIA_IMAGES||
            permission==android.Manifest.permission.READ_MEDIA_AUDIO||
            permission==android.Manifest.permission.READ_MEDIA_VIDEO){

            if (Build.VERSION.SDK_INT<33){
                requestPermissions2(android.Manifest.permission.READ_EXTERNAL_STORAGE)

            }else {
                requestPermissions2(permission)
            }
        }else{
            requestPermissions2(permission)
        }




    }



    private fun requestPermissions2(permission: String) {

        // pastdagi satr joriy faoliyatda ruxsat so'rash uchun ishlatiladi.
        // bu usul ish vaqti ruxsatnomalarida xatoliklarni hal qilish uchun ishlatiladi
        Dexter.withContext(binding.root.context)
            // pastdagi satr ilovamizda talab qilinadigan ruxsatlar sonini so'rash uchun ishlatiladi.
            .withPermissions(
               permission
            )
            // ruxsatlarni qo'shgandan so'ng biz tinglovchi bilan usulni chaqiramiz.
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(multiplePermissionsReport: MultiplePermissionsReport) {
                    // bu usul barcha ruxsatlar berilganda chaqiriladi
                    if (multiplePermissionsReport.areAllPermissionsGranted()) {
                        // hozir ishlayapsizmi
                        startcamera(android.Manifest.permission.CAMERA)
                        Toast.makeText(binding.root.context, "Barcha ruxsatlar berilgan, xarakatni davom etirishingiz mumki", Toast.LENGTH_SHORT).show()

                        if (checkReadContactsPermission(binding.root.context,android.Manifest.permission.CAMERA)) {

                            // Ruxsat berilgan, kontakt ma'lumotlariga kirishingiz mumkin
                            // Kerakli harakatlar tugallanishi uchun shu yerga kod yozing
                        }

                    }
                    // har qanday ruxsatni doimiy ravishda rad etishni tekshiring
                    if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied) {
                        // ruxsat butunlay rad etilgan, biz foydalanuvchiga dialog xabarini ko'rsatamiz.
                        showSettingsDialog()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(list: List<PermissionRequest>, permissionToken: PermissionToken) {
                    // foydalanuvchi ba'zi ruxsatlarni berib, ba'zilarini rad qilganda bu usul chaqiriladi.
                    permissionToken.continuePermissionRequest()
                }
            }).withErrorListener {
                // biz xato xabari uchun tost xabarini ko'rsatmoqdamiz.
                Toast.makeText(binding.root.context, "Error occurred! ", Toast.LENGTH_SHORT).show()
            }
            // pastdagi satr bir xil mavzudagi ruxsatlarni ishga tushirish va ruxsatlarni tekshirish uchun ishlatiladi
            .onSameThread().check()

    }

    // quyida poyabzal sozlash dialog usuli
    // dialog xabarini ko'rsatish uchun ishlatiladi.
    private fun showSettingsDialog() {
        // biz ruxsatlar uchun ogohlantirish dialogini ko'rsatmoqdamiz
        val builder = AlertDialog.Builder(binding.root.context)

        // pastdagi satr ogohlantirish dialogining sarlavhasidir.
        builder.setTitle("Need Permissions")

        // pastdagi satr bizning muloqotimiz uchun xabardir
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.")
        builder.setPositiveButton("GOTO SETTINGS") { dialog, which ->
            // bu usul musbat tugmani bosganda va shit tugmasini bosganda chaqiriladi
            // biz foydalanuvchini ilovamizdan ilovamiz sozlamalari sahifasiga yo'naltirmoqdamiz.
            dialog.cancel()
            // quyida biz foydalanuvchini qayta yo'naltirish niyatimiz.
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", null, "AddFragment")
            intent.data = uri
            startActivityForResult(intent, 101)
        }
        builder.setNegativeButton("Cancel") { dialog, which ->
            // bu usul foydalanuvchi salbiy tugmani bosganda chaqiriladi.
            dialog.cancel()
        }
        // dialog oynamizni ko'rsatish uchun quyidagi qatordan foydalaniladi
        builder.show()
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PasportFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PasportFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}