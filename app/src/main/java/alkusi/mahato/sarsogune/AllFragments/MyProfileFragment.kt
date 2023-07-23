package alkusi.mahato.sarsogune.AllFragments
import alkusi.mahato.sarsogune.AllActivity.*
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import alkusi.mahato.sarsogune.R
import alkusi.mahato.sarsogune.databinding.FragmentMyProfileBinding
import android.content.DialogInterface
import android.content.Intent
import android.view.View.OnClickListener
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import dev.shreyaspatil.MaterialDialog.AbstractDialog
import dev.shreyaspatil.MaterialDialog.MaterialDialog
class MyProfileFragment : BaseFragment() {
lateinit var binding:FragmentMyProfileBinding;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentMyProfileBinding.inflate(inflater)
        init();
        return binding.root;
    }
private fun init()
{
    if(HomeActivity.isSuperAdamin)
    {
        binding.llAddFestival.visibility = View.VISIBLE
    }
    binding.llPrivacyPolicy.setOnClickListener(object :View.OnClickListener
    {
        override fun onClick(p0: View?) {
          val intent = Intent(requireContext(),PrivacyPolicyActivity::class.java)
            startActivity(intent)
        }

    })
    binding.textName.setText(HomeActivity.UserName)
    binding.llMyProfile.setOnClickListener(object :View.OnClickListener
    {
        override fun onClick(p0: View?) {
            val intent = Intent(requireContext(),MyProfileActivity::class.java)
            startActivity(intent);
        }

    })

    binding.llPrivacyPolicy.setOnClickListener(object :View.OnClickListener
    {
        override fun onClick(v: View?) {
          val intent = Intent(requireContext(),PrivacyPolicyActivity::class.java)
            startActivity(intent)
        }

    })

    binding.llLogout.setOnClickListener(object :View.OnClickListener
    {
        override fun onClick(p0: View?) {
            showDialogLogout();
        }

    })

    binding.llUserLocation.setOnClickListener(object :View.OnClickListener
    {
        override fun onClick(v: View?) {
            val intent = Intent(requireContext(),MapsActivity::class.java)
            intent.putExtra("isViewAll",true)
            startActivity(intent)
        }

    })
    binding.llAboutUs.setOnClickListener(object :View.OnClickListener
    {
        override fun onClick(p0: View?) {
            val intent = Intent(requireContext(),AboutUsActivity::class.java);
            startActivity(intent)
        }
    })
    binding.contactUsll.setOnClickListener(object :View.OnClickListener
    {
        override fun onClick(p0: View?) {
          val intent = Intent(requireContext(),ContactUsActivity::class.java)
            startActivity(intent)
        }

    })
    binding.llHelpSupport.setOnClickListener(object :View.OnClickListener
    {
        override fun onClick(p0: View?) {
            val intent = Intent(requireContext(),ContactUsActivity::class.java)
            startActivity(intent)
        }
    })
    binding.llAddFestival.setOnClickListener(object :View.OnClickListener
    {
        override fun onClick(p0: View?) {
            val intent = Intent(requireContext(),AccountVerificationActivity::class.java)
            startActivity(intent)
        }

    })
}
    private fun showDialogLogout()
    {

        val mDialog = MaterialDialog.Builder(requireActivity())
            .setTitle("Logout?")
            .setMessage(resources.getString(R.string.msg_are_you_sure_want_to_logout))

            .setCancelable(false)
            .setPositiveButton(
                "Yes",
                R.drawable.icon_logout,
                object : AbstractDialog.OnClickListener {
                    override fun onClick(
                        dialogInterface: dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface?,
                        which: Int
                    ) {
                        FirebaseAuth.getInstance().signOut();
                        val intent =
                            Intent(requireContext(), LoginActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish();
                        dialogInterface!!.dismiss()
                    }

                })
            .setNegativeButton(
                "Cancel",
                alkusi.mahato.sarsogune.R.drawable.icon_close,
                object : AbstractDialog.OnClickListener {
                    override fun onClick(
                        dialogInterface: dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface?,
                        which: Int
                    ) {
                        dialogInterface!!.dismiss()
                    }

                })
            .build()

        // Show Dialog

        // Show Dialog
        mDialog.show()


    }
}