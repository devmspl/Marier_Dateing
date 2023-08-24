package com.app.marier.fragments

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.app.marier.R
import com.app.marier.activities.SettingActivity
import com.app.marier.databinding.FragmentEditProfileBinding
import com.app.marier.datamodel.adduserdatamodel.request.AdduserDataRequest
import com.app.marier.datamodel.adduserdatamodel.request.AgeRange
import com.app.marier.datamodel.adduserdatamodel.request.Location
import com.app.marier.datamodel.adduserdatamodel.request.Setting
import com.app.marier.datamodel.getcurrentuserbyid.GetCurrentUserByIdExample
import com.app.marier.datamodel.updateUserModel.request.UpdateUserRequest
import com.app.marier.utils.CSPreferences
import com.app.marier.utils.Resource
import com.app.marier.utils.Utils
import com.app.marier.viewmodel.UserModuleviewModel
import java.util.Calendar


class EditProfileFragment : Fragment(), View.OnClickListener {
    private var binding: FragmentEditProfileBinding? = null
    lateinit var viewmodel: UserModuleviewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEditProfileBinding.inflate(layoutInflater, container, false)
        val view =binding!!.root
        listeners()


        val genders = arrayOf("Male", "Female", "Other")

        val colorSpinner = view.findViewById<Spinner>(R.id.spinner)
        val editText = view.findViewById<EditText>(R.id.etgender)

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, genders)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        colorSpinner.adapter = adapter

        colorSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedGender = genders[position]
                editText.setText(selectedGender)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }



        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewmodel = (activity as SettingActivity).viewModel
        var token = CSPreferences.readString(requireActivity(), Utils.TOKEN)
        var userid = CSPreferences.readString(requireActivity(), Utils.USERID)
        viewmodel.getcurrentuserByid(token!!, userid!!)
        bindobserver()

    }

    private fun listeners() {
        binding?.tvdone?.setOnClickListener(this)
        binding?.imgback?.setOnClickListener(this)
        binding?.tvdob?.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.tvdone -> {
                var token = CSPreferences.readString(requireActivity(), Utils.TOKEN)
                var userid = CSPreferences.readString(requireActivity(), Utils.USERID)
                viewmodel.updateUserFun(token!!, userid!!, updateData())
                bindobserverr()
            }

            R.id.imgback -> findNavController().navigateUp()
            R.id.tvdob -> pickdate()
        }
    }

    private fun updateData(): UpdateUserRequest {


        val name = binding?.tvaboutname?.text.toString()
        val dob = binding?.tvdob?.text.toString()
        val gender = binding?.etgender?.text.toString().lowercase()
        val phoneNumber = binding?.tvphonenum?.text.toString()
        val address = binding?.etcity?.text.toString()
        val email = binding?.etEmail?.text.toString()
        val bio = binding?.etBio?.text.toString()
        val type = "Point"
        val coordinates = arrayListOf<Double>(30.7046, 76.7179)
        val location = Location(coordinates, type)
        val to = 0
        val from = 0
        val ageRange = AgeRange(to, from)
        val setting = Setting(ageRange, location)
        val smallcasegender = gender?.lowercase()


        return UpdateUserRequest(name, phoneNumber, dob, email, bio, gender, address)
    }

    private fun bindobserverr() {
        viewmodel.updateuserliveData.value = null
        viewmodel.updateuserliveData.removeObservers(viewLifecycleOwner)
        viewmodel.updateuserliveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    Utils.hideDialog()
                    Toast.makeText(
                        requireContext(),
                        "Done",
                        Toast.LENGTH_SHORT
                    ).show()

                }

                is Resource.Error -> {
                    Utils.hideDialog()
                    Toast.makeText(
                        requireContext(),
                        "Failed",
                        Toast.LENGTH_SHORT
                    ).show()

                }

                is Resource.Loading -> {
                    Utils.showDialogMethod(requireActivity(), activity?.fragmentManager)

                }
            }
        })
    }

    private fun bindobserver() {
        viewmodel.getcurrentuserByidliveData.value = null
        viewmodel.getcurrentuserByidliveData.removeObservers(viewLifecycleOwner)
        viewmodel.getcurrentuserByidliveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    Utils.hideDialog()
                    Toast.makeText(
                        requireContext(),
                        it.data?.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                    setData(it.data)

                }

                is Resource.Error -> {
                    Utils.hideDialog()
                    Toast.makeText(
                        requireContext(),
                        it.data?.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()

                }

                is Resource.Loading -> {
                    Utils.showDialogMethod(requireActivity(), activity?.fragmentManager)

                }
            }
        })
    }

    private fun setData(data: GetCurrentUserByIdExample?) {
        binding?.tvaboutname?.setText(data?.data?.name)
        binding?.tvphonenum?.setText(data?.data?.phoneNumber)
        binding?.etEmail?.setText(data?.data?.email)
        binding?.etBio?.setText(data?.data?.bio)
        binding?.etcity?.setText(data?.data?.address)
        val getdob = data?.data?.dob
        val dob = getdob?.take(10)
        binding?.tvdob?.text = dob
        binding?.etgender?.setText(data?.data!!.sex)
    }

    private fun pickdate() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val maxDate = Calendar.getInstance()
        maxDate.add(Calendar.YEAR, 1)
        val datePickerDialog = DatePickerDialog(
            // on below line we are passing context.
            activity as FragmentActivity,
            { view, year, monthOfYear, dayOfMonth ->
                binding?.tvdob?.text =
                    (year.toString() + "-" + (monthOfYear + 1) + "-" + dayOfMonth.toString())
                binding?.tvdob?.setTextColor(Color.BLACK)
//                            (dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year)
            },
            year,
            month,
            day
        )
        datePickerDialog.datePicker.maxDate = maxDate.timeInMillis
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        datePickerDialog.show()
    }


}