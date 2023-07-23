package alkusi.mahato.sarsogune.AllFragments

import alkusi.mahato.sarsogune.AllAdapter.AdapterSelectGusti
import alkusi.mahato.sarsogune.R
import alkusi.mahato.sarsogune.databinding.FragmentSelectGustiBinding
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager


class SelectGustiFragment(var listener:OnGustiSelected) : DialogFragment(),AdapterSelectGusti.OnGustiCLick {

     var binding:FragmentSelectGustiBinding?=null;
    var gustiAdapter:AdapterSelectGusti?=null;
    val gustiList:ArrayList<String> = ArrayList<String>();
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSelectGustiBinding.inflate(inflater);
        init();
        return binding!!.root
    }
private fun init()
{
    gustiList.clear();
    gustiList.addAll(resources.getStringArray(R.array.gusti_list).asList())
    gustiAdapter = AdapterSelectGusti(requireContext(),gustiList,this);
      binding!!.recyclerView.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false);
    binding!!.recyclerView.adapter = gustiAdapter;
    gustiAdapter!!.notifyDataSetChanged();


    binding!!.edtSearchGusti.addTextChangedListener(object :TextWatcher
    {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(str: CharSequence?, p1: Int, p2: Int, p3: Int) {
            if(!TextUtils.isEmpty(str))
            {
                val newList = ArrayList<String>();
                for(i in gustiList.indices)
                {
                    val strMain = gustiList.get(i).toLowerCase();
                    val strSearch = str.toString().toLowerCase();
                    if(strMain.startsWith(strSearch,ignoreCase = true))
                    {
                        newList.add(gustiList.get(i));
                    }
                }
                gustiAdapter = AdapterSelectGusti(requireContext(),newList,this@SelectGustiFragment)
                binding!!.recyclerView.adapter = gustiAdapter;
                gustiAdapter!!.notifyDataSetChanged();
            }
            else
            {

                gustiAdapter = AdapterSelectGusti(requireContext(),gustiList,this@SelectGustiFragment)
                binding!!.recyclerView.adapter = gustiAdapter;
                gustiAdapter!!.notifyDataSetChanged();
            }
        }

        override fun afterTextChanged(p0: Editable?) {

        }

    })
}


    override fun onGustiClick(gusti: String) {
        listener.onGustiSelected(gusti)
        dismiss()
    }
interface OnGustiSelected
{
    fun onGustiSelected(gusti:String)
}

    override fun onStart() {
        super.onStart()
        val dialog = dialog;
        if(dialog!=null)
        {
            val width = ViewGroup.LayoutParams.MATCH_PARENT;
            val height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.window!!.setLayout(width,height)
        }

    }
}