package alkusi.mahato.sarsogune.AllActivity

import alkusi.mahato.sarsogune.R
import alkusi.mahato.sarsogune.databinding.ActivityAboutUsBinding
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.FirebaseFirestore
import java.lang.Exception

class AboutUsActivity : BaseActivity() {
    lateinit var binding:ActivityAboutUsBinding;
    lateinit var toolbar: Toolbar;
    var firebaseFirestore:FirebaseFirestore?=null;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutUsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar)
        setBackEnable(true)
        setTitle(resources.getString(R.string.text_aboutUs))
        init();
    }
    private fun init()
    {
      binding.textAboutUs.setText(HomeActivity.aboutUs)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if(HomeActivity.isSuperAdamin)
        {
            menuInflater.inflate(R.menu.menu_edit,menu)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId)
        {
            R.id.menu_edit ->
            {
                showEditDialog();
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showEditDialog()
    {
        firebaseFirestore = FirebaseFirestore.getInstance();
        val dialog = Dialog(this);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.edit_text_dialog);
        val editHind = dialog.findViewById<TextInputLayout>(R.id.edtHint)
        val edtName = dialog.findViewById<TextInputEditText>(R.id.edtName);
        val saveBtn = dialog.findViewById<Button>(R.id.btnSave);
          saveBtn.setOnClickListener(object :View.OnClickListener
          {
              override fun onClick(p0: View?) {
                  val text = edtName.text.toString();
                  if(TextUtils.isEmpty(text))
                  {
                      edtName.setError("Enter About yours")
                      return;
                  }
                  saveAboutUs(text);
                  dialog.dismiss();
              }

          })
        editHind.setHint("Enter about yours");
        dialog.show()
    }
    private fun saveAboutUs(aboutUs:String)
    {
        if(isConnectionAvailable())
        {
            binding.progressbar.visibility = View.VISIBLE;

            val map:HashMap<String,Any> = HashMap();
            map.put("AboutUs",aboutUs);
            map.put("Editby",HomeActivity.UserName)
            if(firebaseFirestore==null)
            {
                firebaseFirestore = FirebaseFirestore.getInstance();
            }

            firebaseFirestore!!.collection("OurDetails").document("AboutUs").set(map).addOnSuccessListener(object :OnSuccessListener<Void>
            {
                override fun onSuccess(p0: Void?) {
                   binding.textAboutUs.setText(aboutUs)
                    HomeActivity.aboutUs = aboutUs;
                    binding.progressbar.visibility = View.GONE;
                }

            })
                .addOnFailureListener(object :OnFailureListener
                {
                    override fun onFailure(p0: Exception) {
                        binding.progressbar.visibility = View.GONE;
                        Toast.makeText(this@AboutUsActivity,resources.getString(R.string.msg_server_error),Toast.LENGTH_SHORT).show();
                        binding.progressbar.visibility = View.GONE;
                    }

                })
        }
        else
        {
            showNoNetworkMsg()
        }
    }
}