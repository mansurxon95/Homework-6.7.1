package com.example.a6711

import android.os.Bundle
import android.util.Log
import android.view.ContextMenu
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.a6711.adapters.RcAdapter
import com.example.a6711.database.PassportDatabase
import com.example.a6711.databinding.FragmentAllBinding
import com.example.a6711.entitiy.PassportEntity

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AllFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AllFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var binding: FragmentAllBinding
    lateinit var passportDatabase: PassportDatabase
    lateinit var adapter: RcAdapter
    var position:Long?=null
    var user:PassportEntity?= null

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
        binding = FragmentAllBinding.inflate(inflater,container,false)

        binding.btnBackTool.setOnClickListener {
            findNavController().popBackStack()
        }

        passportDatabase = PassportDatabase.getInstance(requireContext())
        val allPassport = passportDatabase.passportDao().getAllPassport()

        adapter = RcAdapter(object : RcAdapter.OnClik{

            override fun clickview(contact: PassportEntity) {
                super.clickview(contact)
                var bundle = Bundle()
                    bundle.putSerializable("key",contact)
                findNavController().navigate(R.id.action_allFragment_to_viewFragment,bundle)
            }

            override fun click(contact: PassportEntity, btn: View) {
                super.click(contact, btn)
                registerForContextMenu(btn)
                position = contact.passportId
                user = contact


            }
        })

        binding.rcView.adapter = adapter
        adapter.submitList(allPassport)



        return binding.root
    }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        MenuInflater(binding.root.context).inflate(R.menu.menu_contex,menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.edit -> {
                var bundle = Bundle()
                bundle.putSerializable("key", user!!)
                findNavController().navigate(R.id.action_allFragment_to_pasportFragment,bundle)
            }

            R.id.delete ->{
                passportDatabase.passportDao().deletePassportId(position!!)
                adapter.notifyDataSetChanged()
            }
        }

        return super.onContextItemSelected(item)

    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AllFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AllFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}