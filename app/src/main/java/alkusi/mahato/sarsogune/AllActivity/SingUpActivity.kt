package alkusi.mahato.sarsogune.AllActivity

import alkusi.mahato.sarsogune.R
import alkusi.mahato.sarsogune.databinding.ActivitySingUpBinding
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class SingUpActivity : BaseActivity() {
    lateinit var toolbar: androidx.appcompat.widget.Toolbar;
    lateinit var binding: ActivitySingUpBinding;
    lateinit var firebaseAuth: FirebaseAuth;
    lateinit var db: FirebaseFirestore;
    val TAG = "SingUpActivity"
    var genderSpAdapter: ArrayAdapter<String>? = null;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySingUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar)
        db = FirebaseFirestore.getInstance();
        setTitle("SignUp");
        setBackEnable(true)
        init1();
        init();
    }

    private fun init1() {



        firebaseAuth = FirebaseAuth.getInstance();
        genderSpAdapter = ArrayAdapter(
            this,
            R.layout.item_spinner,
            R.id.edtSpinner,
            resources.getStringArray(R.array.array_gender)
        );
        binding.spinnerGender.adapter = genderSpAdapter;


        val textWatcher = object :TextWatcher
        {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {



            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                isValidPassword(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {

            }

        }
        binding.edtNewPass.addTextChangedListener(textWatcher)
    }

    private fun init() {
        binding.btnCreate.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {

                var isValid = true;
                var email = binding.edtEmail.text.toString();
                var name = binding.edtName.text.toString();
                var newPassword = binding.edtNewPass.text.toString();
                var confirmPassword = binding.edtConfirmPassword.text.toString();
                var genderStr: String = binding.spinnerGender.selectedItem.toString();

                if (TextUtils.isEmpty(name)) {
                    isValid = false;
                    binding.edtName.setError(resources.getString(R.string.txt_enterValid_name))
                }
                if (TextUtils.isEmpty(email) && !email.contains("@gmail.com")) {
                    binding.edtEmail.setError(resources.getString(R.string.txt_enterValid_email))
                    isValid = false
                }
                if (TextUtils.isEmpty(newPassword)) {
                    isValid = false
                    binding.edtNewPass.setError(resources.getString(R.string.txt_enterValid_password))
                }
                if (!TextUtils.isEmpty(binding.edtNewPass.error)) {
                    isValid = false
                    binding.edtNewPass.setError(resources.getString(R.string.txt_enterValid_password))
                    Toast.makeText(this@SingUpActivity,resources.getString(R.string.txt_enterValid_password),Toast.LENGTH_SHORT).show()

                }
                if (TextUtils.isEmpty(confirmPassword)) {
                    isValid = false
                    binding.edtConfirmPassword.setError(resources.getString(R.string.txt_enterValid_password))
                }

                if (!newPassword.equals(confirmPassword)) {
                    isValid = false;
                    binding.edtConfirmPassword.setError(resources.getString(R.string.text_password_doesNot_match))

                }
                if(!binding.checkAge.isChecked)
                {
                    isValid = false
                    Toast.makeText(this@SingUpActivity,"We are allowing above 18 years only you can not login",Toast.LENGTH_SHORT).show()

                }
                if(!binding.btnCheck.isChecked)
                {
                    isValid = false
                    Toast.makeText(this@SingUpActivity,"Please agree with the Terms Condition &amp; Privacy Policy",Toast.LENGTH_SHORT).show()

                }
                if (genderStr.toString().equals("Select Gender", ignoreCase = true)) {
                    isValid = false;
                    Toast.makeText(
                        this@SingUpActivity,
                        resources.getString(R.string.msg_plz_select_gender),
                        Toast.LENGTH_SHORT
                    ).show();
                }

                if (isValid && !TextUtils.isEmpty(binding.edtNewPass.error.toString())) {
                    if (isConnectionAvailable()) {
                        binding.btnCreate.isEnabled = false;
                        binding.progressbar.visibility = View.VISIBLE;
                        createAccount(name, email, confirmPassword, genderStr as String)
                    } else {
                        showNoNetworkMsg()
                        binding.btnCreate.isEnabled = true;
                        binding.progressbar.visibility = View.GONE;
                    }

                }


            }

        })


    }

    private fun createAccount(name: String, email: String, password: String, genderStr: String) {
        val map: MutableMap<String, Any> = HashMap();
        map.put("Name", name)
        map.put("Gender", genderStr)
        map.put(resources.getString(R.string.fir_isAdmin), false)
        map.put(resources.getString(R.string.fir_isSuperAdmin), false)
        map.put(resources.getString(R.string.fir_account_password),password)
        map.put(resources.getString(R.string.fir_notification_token), "")
        map.put(resources.getString(R.string.fir_time_stamp),Calendar.getInstance().timeInMillis)
        if (map != null) {
            db.collection("Users").document(email).set(map)
                .addOnSuccessListener(object : OnSuccessListener<Void> {
                    override fun onSuccess(p0: Void?) {
                        savePasswordInSharedPreference(password)
                        firebaseAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val user = firebaseAuth.currentUser;
                                    binding.btnCreate.isEnabled = true;
                                    Toast.makeText(
                                        baseContext,
                                        resources.getString(R.string.txt_success_account),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    binding.progressbar.visibility = View.GONE;
                                    finish()
                                } else {
                                    binding.progressbar.visibility = View.GONE;
                                    binding.btnCreate.isEnabled = true;
                                    Toast.makeText(
                                        baseContext,
                                        resources.getString(R.string.msg_something_went),
                                        Toast.LENGTH_SHORT
                                    ).show()

                                }
                            }
                        binding.progressbar.visibility = View.GONE;
                    }

                }).addOnFailureListener(
                object : OnFailureListener {
                    override fun onFailure(p0: Exception) {
                        binding.btnCreate.isEnabled = true;
                        Toast.makeText(
                            baseContext,
                            resources.getString(R.string.msg_something_went),
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.progressbar.visibility = View.GONE;
                    }

                })


        }

    }

    fun isValidPassword(password: String) {
        var isSpecialChar = false;
        var isUpper = false;
        var isLower = false;
        var isNumber = false;
          if(password.length<6)
          {
              binding.edtNewPass.setError("Enter minimum 6 digit password format")


          }
         if (password.length >= 6 ) {

              for(i in password)
              {
                  if(i.isLowerCase())
                  {
                      isLower = true;
                  }
                  if(i.isUpperCase())
                  {
                      isUpper = true;
                  }
                  if(i.isDigit())
                  {
                      isNumber = true;
                  }


              }
             if(password.contains("*")||password.contains("@")||password.contains("$"))
             {
                 isSpecialChar = true;
             }
             if(isSpecialChar && isUpper && isLower && isNumber)
             {
                 binding.edtNewPass.setError("")
             }
             else
             {

                 binding.edtNewPass.setError("plz Enter valid password format")
             }
        }




    }
    private fun savePasswordInSharedPreference(pass:String)
    {
        val sharedPreferences = getSharedPreferences(resources.getString(R.string.app_name),
            Context.MODE_PRIVATE)
        var editor = sharedPreferences.edit();
        editor.putString(resources.getString(R.string.shard_pre_pass),pass)
        editor.commit()

    }
}


