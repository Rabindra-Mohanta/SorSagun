package alkusi.mahato.sarsogune.AllFragments

import alkusi.mahato.sarsogune.R
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

open class BaseFragment:Fragment() {
    fun isConnectionAvailable():Boolean
    {
        val connectivityManager = requireActivity().applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            val network = connectivityManager.activeNetwork?:return false;
            val activeNetwork =connectivityManager.getNetworkCapabilities(network)?:return  false;
            return when
            {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true;
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true;
                else -> {false}
            }
        }
        else
        {
            val networkInfo = connectivityManager.activeNetworkInfo ?: return false;
            return networkInfo.isConnected;
        }
    }

    fun showNoNetworkMsg()
    {
        val snackbar = Snackbar.make(requireActivity().findViewById(R.id.toolbar),resources.getString(R.string.msg_please_check_internet),
            Snackbar.ANIMATION_MODE_SLIDE).setAction(android.R.string.ok,object: View.OnClickListener {
            override fun onClick(p0: View?) {
                var intent = Intent(Settings.ACTION_NETWORK_OPERATOR_SETTINGS);
                startActivity(intent)
            }
        })

        snackbar.setActionTextColor(resources.getColor(R.color.yellow ))
        val snackbarView = snackbar.view;
        val textView = snackbarView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        textView.setTextColor(resources.getColor(R.color.white));
        snackbarView.setBackgroundColor(resources.getColor(R.color.black))
        snackbar.show();

    }

    fun setBackEnable(b:Boolean)
    {
        if(requireActivity().actionBar!=null)
        {
            requireActivity().actionBar!!.setDisplayHomeAsUpEnabled(b)
            requireActivity().actionBar!!.setHomeButtonEnabled(b);

        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId)
        {
            android.R.id.home ->
            {
                hideKeyboard()
                requireActivity().finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item)
    }


    fun hideKeyboard()
    {

        val inputMethodManager = requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager;
        var view: View? = requireActivity().currentFocus ;
        if(view==null)
        {
            view = View(requireContext());
        }
        inputMethodManager.hideSoftInputFromWindow(view.windowToken,0);
    }

}