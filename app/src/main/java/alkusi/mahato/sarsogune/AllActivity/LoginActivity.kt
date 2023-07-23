package alkusi.mahato.sarsogune.AllActivity

import alkusi.mahato.sarsogune.R
import alkusi.mahato.sarsogune.databinding.ActivityLoginBinding
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging

class LoginActivity : BaseActivity() {
    lateinit var binding:ActivityLoginBinding;
    lateinit var firebaseAuth: FirebaseAuth;
    lateinit var db:FirebaseFirestore;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater);
        setContentView(binding.root)
        db = FirebaseFirestore.getInstance();
        init1();
        init()
    }
    private fun init1()
    {
        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.currentUser!=null)
        {

            val intent = Intent(this@LoginActivity,HomeActivity::class.java)
            startActivity(intent)
            finish();
        }
    }

private fun savePasswordInSharedPreference(pass:String)
{
    val sharedPreferences = getSharedPreferences(resources.getString(R.string.app_name),Context.MODE_PRIVATE)
    var editor = sharedPreferences.edit();
    editor.putString(resources.getString(R.string.shard_pre_pass),pass)
    editor.commit()


}

    private fun updateNotificationKeyToServer()
    {
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if(it.isSuccessful) {
               val key = it.result.toString()
                val map: MutableMap<String,Any> = HashMap();
                map.put(resources.getString(R.string.txt_notification),key)
                db.collection(resources.getString(R.string.fir_Users)).document(firebaseAuth.currentUser!!.email.toString()).update(map).addOnCompleteListener {

                }

            }
        }
//        db.collection("users").doc("frank").update({
//            "favorites.firebase": "Help")}
//    })
    }

private fun init()
{
    binding.btnLogin.setOnClickListener(object :View.OnClickListener
    {
        override fun onClick(p0: View?) {

            var isValid = true;
           val email = binding.edtEmail.text.toString();
            val password = binding.edtPassword.text.toString();
            if(TextUtils.isEmpty(email) || !email.contains("@gmail.com"))
            {
                isValid = false
                binding.edtEmail.setError(resources.getString(R.string.txt_enterValid_email))
            }
            if(TextUtils.isEmpty(password))
            {
                isValid = false
                binding.edtPassword.setError(resources.getString(R.string.txt_enterValid_password))
            }
            if(!binding.checkAge.isChecked)
            {
                isValid = false
                Toast.makeText(this@LoginActivity,"We are allowing above 18 years only you can not login",Toast.LENGTH_SHORT).show()

            }
            if(!binding.btnCheck.isChecked)
            {
                isValid = false
                Toast.makeText(this@LoginActivity,"Please agree with the Terms Condition &amp; Privacy Policy",Toast.LENGTH_SHORT).show()

            }
            if(isValid)
            {
                if(isConnectionAvailable())
                {

                      binding.progressbar.visibility = View.VISIBLE;
                    doLogin(email,password)
                }
                else
                {
                    binding.progressbar.visibility = View.GONE;
                    showNoNetworkMsg();
                }
                }

            }


    })

    binding.btnNewAccount.setOnClickListener(object:View.OnClickListener
    {
        override fun onClick(p0: View?) {
            val intent = Intent(this@LoginActivity,SingUpActivity::class.java)
            startActivity(intent);
        }

    })

    binding.txtForgotPassword.setOnClickListener(object :View.OnClickListener
    {
        override fun onClick(p0: View?) {

            if(!isConnectionAvailable())
            {
                showNoNetworkMsg();
                return;
            }

            val email = binding.edtEmail.text.toString();
            if(TextUtils.isEmpty(email) || !email.contains("@gmail.com"))
            {
                binding.edtEmail.setError(resources.getString(R.string.txt_enterValid_email))
            }
            else
            {
                binding.progressbar.visibility = View.VISIBLE;
                binding.txtForgotPassword.isEnabled = false
                firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(object:OnCompleteListener<Void>
                {
                    override fun onComplete(task: Task<Void>) {
                        if(task.isSuccessful)
                        {
                            showDialogResetPassword(email);
                            binding.progressbar.visibility = View.GONE;
                            binding.txtForgotPassword.isEnabled = true
                        }
                        else
                        {
                           binding.edtEmail.setError(resources.getString(R.string.txt_enterValid_email));
                            Toast.makeText(this@LoginActivity,resources.getString(R.string.txt_enterValid_email),Toast.LENGTH_SHORT).show();
                            binding.progressbar.visibility = View.GONE;
                            binding.txtForgotPassword.isEnabled = true
                        }


                    }
                })

            }



        }

    })



}
    private fun showDialogResetPassword(email: String)
    {
        val alertDialog = android.app.AlertDialog.Builder(this);
        alertDialog.setCancelable(true);
        alertDialog.setTitle(resources.getString(R.string.txt_forgotPassword));
        alertDialog.setMessage("please check your email $email to reset your password ")


        alertDialog.setPositiveButton(android.R.string.ok,object :DialogInterface.OnClickListener
        {
            override fun onClick(p0: DialogInterface?, p1: Int) {
                p0!!.dismiss()
            }

        })
        alertDialog.setNegativeButton(android.R.string.cancel,object :DialogInterface.OnClickListener
        {
            override fun onClick(p0: DialogInterface?, p1: Int) {
                p0!!.dismiss()
            }

        })
        alertDialog.show();


    }

private fun doLogin(email:String,password:String)
{
    firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this@LoginActivity,object:OnCompleteListener<AuthResult>
    {
        override fun onComplete(task: Task<AuthResult>) {
            if(task.isSuccessful)
            {
                savePasswordInSharedPreference(password)
                updateNotificationKeyToServer()
                binding.progressbar.visibility = View.GONE;
                val intent = Intent(this@LoginActivity,HomeActivity::class.java)
                startActivity(intent)
                finish();
            }
            else
            {
                binding.progressbar.visibility = View.GONE;
                binding.edtEmail.setError(resources.getString(R.string.txt_enterValid_email));
                binding.edtPassword.setError(resources.getString(R.string.txt_enterValid_password));
                Toast.makeText(this@LoginActivity,resources.getString(R.string.msg_plz_enter_valid_login_id),Toast.LENGTH_SHORT).show();

            }
        }

    })
}

    override fun onStart() {
        super.onStart()
        val currentUser = firebaseAuth.currentUser;
        if(currentUser!=null)
        {
            val intent = Intent(this@LoginActivity,HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}