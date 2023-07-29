package alkusi.mahato.sarsogune.AllActivity
import alkusi.mahato.sarsogune.AllAdapter.AdapterHome
import alkusi.mahato.sarsogune.AllAdapter.HomeViewPagerAdapter
import alkusi.mahato.sarsogune.R
import alkusi.mahato.sarsogune.databinding.ActivityHomeBinding
import android.Manifest
import android.app.ActionBar.LayoutParams
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.models.SlideModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import dev.shreyaspatil.MaterialDialog.AbstractDialog
import dev.shreyaspatil.MaterialDialog.MaterialDialog
class HomeActivity : BaseActivity() {
    lateinit var binding: ActivityHomeBinding;
    lateinit var toolbar: androidx.appcompat.widget.Toolbar;
    lateinit var firebaseAuth: FirebaseAuth;
    lateinit var toggle: ActionBarDrawerToggle
    val REQ_DEVICE_PERMISSION = 234;
    var password: String = "";
    lateinit var drawerLayout: DrawerLayout;
    var currentUser: FirebaseUser? = null;
    var adapterHome: AdapterHome?=null;
    lateinit var firebaseFireStore:FirebaseFirestore;
    companion object
    {
        var UserName = ""
        var UserGender = ""
        var isAdmin = false;
        var isSuperAdamin = false;
        var aboutUs = "";
        var privacyPolicy = ""
        var email: String = "";
        var myData:DocumentSnapshot? = null;
    }

    val TAG = "HomeActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        drawerLayout = findViewById(R.id.drawerLayout)
        firebaseFireStore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        getAllPermission()
        init();
        getProfileData();
        getOurDetails();
        initViewPager();
       // setSlideImage();
        setDrawerLayout();

        getMyData();
        isFestival()

    }

    private fun getAllPermission()
    {
        val locationPermissionRequest = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions())
        {
            when
            {
              it.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION,false)->
              {

              }

              it.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION,false) ->
              {

              }
                it.getOrDefault(Manifest.permission.POST_NOTIFICATIONS,false) ->
                {

                }
                it.getOrDefault(Manifest.permission.READ_EXTERNAL_STORAGE,false) ->
                {

                }

                it.getOrDefault(Manifest.permission.WRITE_EXTERNAL_STORAGE,false) ->
                {

                }
                else ->
                {

                }

            }
        }
                locationPermissionRequest.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.POST_NOTIFICATIONS,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE))



    }


    private fun getMyData()
    {
        firebaseFireStore.collection(resources.getString(R.string.fir_Users)).document(email).get().addOnCompleteListener(object :OnCompleteListener<DocumentSnapshot>
        {
            override fun onComplete(task: Task<DocumentSnapshot>) {
                if(task.isSuccessful)
                {

                    myData = task.result

                    if(myData==null)
                    {
                        FirebaseAuth.getInstance().signOut();
                        val intent = Intent(this@HomeActivity,LoginActivity::class.java);
                        startActivity(intent)
                        finish()
                        return
                    }

                    else
                    {
                        if(myData!!.get(resources.getString(R.string.fir_isAdmin))!=null && !TextUtils.isEmpty(myData!!.get(resources.getString(R.string.fir_isAdmin)).toString()))
                        {

                            isAdmin = myData!!.get(resources.getString(R.string.fir_isAdmin)) as Boolean
                        }
                        if(myData!!.get(resources.getString(R.string.fir_isSuperAdmin)) !=null && !TextUtils.isEmpty(myData!!.get(resources.getString(R.string.fir_isSuperAdmin)).toString()))
                        {
                            isSuperAdamin = myData!!.get(resources.getString(R.string.fir_isSuperAdmin)) as Boolean
                        }
                    }
                    if(myData!!.get(resources.getString(R.string.fir_ContactNo))==null)
                    {
                        showDialogIfNoData()
                    }


                }
                else{

                }
            }

        })
    }
    private fun setDrawerLayout() {

        toggle = ActionBarDrawerToggle(this@HomeActivity, drawerLayout, toolbar, R.string.txt_openDrawer, R.string.txt_closeDrawer);
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
     val view = binding.navigationView.getHeaderView(0);
        val userName = view.findViewById<TextView>(R.id.txtName)
        val profile = view.findViewById<CircleImageView>(R.id.circularImageView);
        val imgEdit = view.findViewById<ImageView>(R.id.imgEdit);
        userName.setText(UserName)
        if(myData!=null)
        {
            val image = myData!!.get(resources.getString(R.string.fir_profileImage)).toString()
            if(image!=null && !TextUtils.isEmpty(image))
            {
                val firestore = FirebaseStorage.getInstance().getReference("images/"+image)
                firestore.downloadUrl.addOnCompleteListener {
                    //for crash
                    if(isFinishing) return@addOnCompleteListener
                    Glide.with(this@HomeActivity).load(it.toString()).placeholder(R.drawable.man_icon).into(profile)
                }
            }
        }

        imgEdit.setOnClickListener(object : View.OnClickListener
        {
            override fun onClick(p0: View?) {
                val intent = Intent(this@HomeActivity,MyProfileActivity::class.java)
                startActivity(intent);
            }

        })

        binding.navigationView.setNavigationItemSelectedListener(object :
            NavigationView.OnNavigationItemSelectedListener {
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                when (item.itemId) {
                    R.id.home ->
                    {
                        binding.drawerLayout.close()
                        return true;
                    }
                    R.id.logout -> {
                        showDialogLogout();

                    }

                    R.id.aboutUs ->
                    {
                        val intent = Intent(this@HomeActivity,AboutUsActivity::class.java)
                        startActivity(intent)
                        return true;
                    }
                    R.id.contactUs ->
                    {
                        val intent = Intent(this@HomeActivity,ContactUsActivity::class.java)
                        startActivity(intent)
                    }
                    R.id.privacyPolicy ->
                    {
                        val intent = Intent(this@HomeActivity,PrivacyPolicyActivity::class.java)
                        startActivity(intent)
                        return true;
                    }
                }
                return true;
            }


        })
    }
   private fun initViewPager()
   {
       binding.viewPager2.adapter = HomeViewPagerAdapter(supportFragmentManager,lifecycle);
       binding.tabLayout.addTab(binding.tabLayout.newTab().setIcon(resources.getDrawable(R.drawable.icon_home)))
       binding.tabLayout.addTab(binding.tabLayout.newTab().setIcon(resources.getDrawable(R.drawable.icon_post_liked)))
       binding.tabLayout.addTab(binding.tabLayout.newTab().setIcon(resources.getDrawable(R.drawable.icon_user)))

       binding.tabLayout.getTabAt(0)!!.setText("Home")
       binding.tabLayout.getTabAt(1)!!.setText("Requested")
       binding.tabLayout.getTabAt(2)!!.setText("Profile")
       binding.tabLayout.addOnTabSelectedListener(object :TabLayout.OnTabSelectedListener
       {
           override fun onTabSelected(tab: TabLayout.Tab?) {
               if(tab!!.position==0)
               {
                   setTitle("Home")
               }
               else if(tab!!.position==1)
               {
                   setTitle(resources.getString(R.string.txt_requested))
               }
               else if(tab!!.position==2)
               {
                   setTitle("My Profile")
               }
           binding.viewPager2.currentItem = tab!!.position
           }

           override fun onTabUnselected(tab: TabLayout.Tab?) {

           }

           override fun onTabReselected(tab: TabLayout.Tab?) {

           }

       })

       binding.viewPager2.registerOnPageChangeCallback(object :ViewPager2.OnPageChangeCallback()
       {
           override fun onPageSelected(position: Int) {
               super.onPageSelected(position)
               if(position==0)
               {
                   setTitle("Home")
               }
               else if(position==1)
               {
                   setTitle(resources.getString(R.string.txt_requested))
               }
               else if(position==2)
               {
                   setTitle("My Profile")
               }
               binding.tabLayout.selectTab(binding.tabLayout.getTabAt(position))
           }
       })
   }
    private fun init() {


        currentUser = firebaseAuth.currentUser;
        if (currentUser == null) {
            val intent = Intent(this@HomeActivity, LoginActivity::class.java)
            startActivity(intent)
            finish();
        } else {
            email = currentUser!!.email.toString();
        }


    }
    private fun getProfileData()
    {
        if(!isConnectionAvailable())
        {
            showNoNetworkMsg()
            return;
        }
        try {
            firebaseFireStore.collection("Users").document(email).get().addOnCompleteListener(object:OnCompleteListener<DocumentSnapshot>
            {
                override fun onComplete(task: Task<DocumentSnapshot>) {
                    if (task.isSuccessful)
                    {
                        var result = task.getResult();
                        if(result==null)
                        {
                            Toast.makeText(this@HomeActivity,resources.getString(R.string.msg_server_error),Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(result.get(resources.getString(R.string.fir_CandidateName)) != null)
                        {
                            UserName = result.get(resources.getString(R.string.fir_CandidateName)) as String;
                        }
                        if(result.get(resources.getString(R.string.fir_Gender))!=null)
                        {
                            UserGender = result.get(resources.getString(R.string.fir_Gender)) as String;
                        }


                        setDrawerLayout();
                    }
                    else
                    {
                        Toast.makeText(this@HomeActivity,resources.getString(R.string.msg_server_error),Toast.LENGTH_SHORT).show();
                    }
                }

            })
        }
        catch (e:Exception)
        {
            Toast.makeText(this@HomeActivity,resources.getString(R.string.msg_something_went),Toast.LENGTH_SHORT).show();

        }

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.logout_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
       when(item.itemId)
       {

           R.id.logout ->
           {

               showDialogLogout()
               return true;
           }

           R.id.menu_search ->
           {
               val intent = Intent(this@HomeActivity,SearchUserActivity::class.java);
               startActivity(intent)
           }



       }


        return super.onOptionsItemSelected(item)
    }
private fun getOurDetails()
{
    if(isConnectionAvailable())
    {
        if(firebaseFireStore!=null)
        {
            firebaseFireStore = FirebaseFirestore.getInstance();
        }
        firebaseFireStore.collection("OurDetails").get().addOnCompleteListener(object:OnCompleteListener<QuerySnapshot>
        {
            override fun onComplete(task: Task<QuerySnapshot>) {
                if(task.isSuccessful)
                {
                    var result:QuerySnapshot = task.getResult();
                    if(result==null)
                    {
                        return
                    }
                    for(document  in result.documents)
                    {
                        if(document.id == "PrivacyPolicy")
                        {
                            if(document.get("PrivacyPolicy")!=null)
                            {
                                privacyPolicy = document.get("PrivacyPolicy").toString()
                            }
                        }
                        else if(document.id.equals("AboutUs"))
                        {
                            if(document.get("AboutUs")!=null)
                            {
                                aboutUs = document.get("AboutUs").toString();
                            }
                        }


                    }

                }
                else
                {

                }
            }

        })


    }
    else
    {
        showNoNetworkMsg()
    }
}
private fun showDialogLogout()
{
    val mDialog = MaterialDialog.Builder(this)
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
                        Intent(this@HomeActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish();
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
private fun isFestival()
{
    firebaseFireStore.collection(resources.getString(R.string.fir_festival)).document(resources.getString(R.string.fir_festival_details)).get().addOnCompleteListener {
        if(it.isSuccessful)
        {
            val result = it.result
            if(result!=null)
            {
                val image = result.getString(resources.getString(R.string.fir_festival_image))
                val storageReference = FirebaseStorage.getInstance().getReference("images/"+image)
                storageReference.downloadUrl.addOnSuccessListener {
                    showFestivalDialog(it.toString())
                }
            }
        }
    }
}




    private fun showDialogIfNoData()
    {
        val dialog = Dialog(this);
        dialog.setContentView(R.layout.dialog_not_updated_account);
        val imgLogo = dialog.findViewById<ImageView>(R.id.imgLogo);
        val btnUpdateProfile = dialog.findViewById<Button>(R.id.btnUpdateProfile);
        dialog.setCancelable(false)
         Glide.with(this).load(R.drawable.icon_edit_g).into(imgLogo)
        btnUpdateProfile.setOnClickListener(object :View.OnClickListener
        {
            override fun onClick(p0: View?) {
                dialog.dismiss()
               val intent = Intent(this@HomeActivity,MyProfileActivity::class.java)
                startActivity(intent)
            }

        })

dialog.window!!.setLayout(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT)
        dialog.show()

    }
    private fun showFestivalDialog(url:String)
    {
        val dialog = Dialog(this);
        dialog.setContentView(R.layout.festival_dialog);
        val imgLogo = dialog.findViewById<ImageView>(R.id.imgLogo);

        val btnUpdateProfile = dialog.findViewById<Button>(R.id.btnUpdateProfile);
        btnUpdateProfile.setText("Skip")
        dialog.setCancelable(false)
        Glide.with(this).load(url).into(imgLogo)
        btnUpdateProfile.setOnClickListener(object :View.OnClickListener
        {
            override fun onClick(p0: View?) {
                dialog.dismiss()

            }

        })

        dialog.window!!.setLayout(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT)
        dialog.show()
    }
}