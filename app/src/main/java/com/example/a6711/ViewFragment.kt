package com.example.a6711

import android.R
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.net.toUri
import androidx.navigation.fragment.findNavController
import com.example.a6711.database.PassportDatabase
import com.example.a6711.databinding.FragmentViewBinding
import com.example.a6711.entitiy.PassportEntity

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ViewFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ViewFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var binding: FragmentViewBinding
    var arg:PassportEntity?=null
    lateinit var passportDatabase:PassportDatabase

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
        binding = FragmentViewBinding.inflate(inflater,container,false)
        passportDatabase = PassportDatabase.getInstance(requireContext())
        binding.btnBackTool.setOnClickListener {
            findNavController().popBackStack()
        }

        arg = arguments?.getSerializable("key") as PassportEntity

        if (arg!=null){

            sexSpinner()
            regionspinner()

            binding.image.setImageURI(arg?.image?.toUri())
            binding.lastName.setText(arg?.lastName)
            binding.firstName.setText(arg?.firstName)
            binding.fatherName.setText(arg?.fatherName)
            binding.viloyatSpinner.setSelection(arg?.region!!)
            binding.shaxarName.setText(arg?.city)
            binding.uyManzil.setText(arg?.homeAddress)
            binding.givenTime.setText(arg?.passportGivenTime)
            binding.termTime.setText(arg?.passportTakeTime)
            if (arg?.sex=="Erkak"){
                binding.sex.setSelection(1)
            }else if (arg?.sex=="Ayol"){
                binding.sex.setSelection(2)
            }


        }

        return binding.root
    }

    private fun sexSpinner() {
        var spinadapter = ArrayAdapter<String>(requireContext(), R.layout.simple_spinner_item,
            arrayOf("Jinsni tanlang", "Erkak","Ayol")
        )
        binding.sex.adapter = spinadapter
    }

    private fun regionspinner() {
        var list = ArrayList<String>()

        for (i in passportDatabase.regionDao().getAllRegionString())   {
            list.add(i.regionName.toString())
        }
        var adapter = ArrayAdapter(requireContext(),android.R.layout.simple_spinner_item,list)
        binding.viloyatSpinner.adapter = adapter

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ViewFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ViewFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}