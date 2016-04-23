package com.jayplabs.andropermsexplorer;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * Created by Chandra Gopalaiah on 22,April,2016.
 */
public class MainActivity
        extends AppCompatActivity
        implements PermissionsGroupListFragment.OnPermissionsGroupSelected {

    private FragmentManager mFragmentManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFragmentManager =  getSupportFragmentManager();
        if (savedInstanceState == null) {
            mFragmentManager
                    .beginTransaction()
                    .add(R.id.root_layout,
                            PermissionsGroupListFragment.newInstance(),
                            "permissionsGroupList")
                    .commit();

        }
    }

    //TODO Save the initialized permissionGroups instances across instances
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onPermissionsGroupSelected(String name) {
        final PermissionsByGroupListFragment permissionsByGroupListFragment =
                PermissionsByGroupListFragment.newInstance(name);
        mFragmentManager
                .beginTransaction()
                .replace(R.id.root_layout,
                        permissionsByGroupListFragment,
                        "permissionsByGroupList")
                .addToBackStack(null)
                .commit();
    }
}
