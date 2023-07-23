package alkusi.mahato.sarsogune.AllAdapter

import alkusi.mahato.sarsogune.AllFragments.HomeFragment
import alkusi.mahato.sarsogune.AllFragments.MessagesFragment
import alkusi.mahato.sarsogune.AllFragments.MyProfileFragment
import alkusi.mahato.sarsogune.AllFragments.RequestFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class HomeViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 3;
    }

    override fun createFragment(position: Int): Fragment {
        when(position)
        {
            0 ->
            {
                return HomeFragment();
            }
            1 ->
            {
                return RequestFragment();
            }


            2 ->
            {
                return MyProfileFragment();
            }
            else ->
            {
                return HomeFragment();
            }

        }
    }
}