package ma.ma;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Nitharani on 04/04/2018.
 */

class SectionsPagerAdapter extends FragmentPagerAdapter{
    public SectionsPagerAdapter(FragmentManager fm)
    {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
    //each fragment is instantiated for each tab.
        switch (position){
//            case 0:
//                ChatFragment chatsFragment = new ChatFragment();
//                return chatsFragment;

            case 0:
                FriendFragment friendsFragment = new FriendFragment();
                return friendsFragment;

                default:
                    return null;

        }
    }

    @Override
    //number of tabs
    public int getCount() {
        return 1;
    }
    //naming the tabs using a switch statement
    public CharSequence getPageTitle(int position){
        switch (position){
//            case 0:
//                return "Chats";
            case 0:
                return "Friends";
            default:
                return null;
        }
    }
}
