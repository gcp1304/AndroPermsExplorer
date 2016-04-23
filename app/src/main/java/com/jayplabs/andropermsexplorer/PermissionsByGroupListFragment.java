package com.jayplabs.andropermsexplorer;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;


public class PermissionsByGroupListFragment extends Fragment {

    private final static String TAG = PermissionsByGroupListFragment.class.getSimpleName();

    private static final String ARGUMENT_NAME = "permission_group_name";


    public String mPermissionGroupName;
    public List<PermissionInfo> mPermissionInfoList;
    public PackageManager mPackageManager;

    public static PermissionsByGroupListFragment newInstance(String name) {
        Log.d(TAG, "newInstance");
        final Bundle args = new Bundle();
        args.putString(ARGUMENT_NAME, name);
        final PermissionsByGroupListFragment fragment = new PermissionsByGroupListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public PermissionsByGroupListFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        Log.d(TAG, "onCreateView");
        final View view = inflater.inflate(
                R.layout.fragment_permissions_by_group_list,
                container,
                false
        );

        // Set the adapter
        if (view instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
            recyclerView.addOnItemTouchListener(new RecyclerTouchListener(
                    getActivity(),
                    recyclerView, new ClickListener() {
                @Override
                public void onClick(View view, int position) {
                    Toast.makeText(getActivity(), "Clicked!!!!!", Toast.LENGTH_LONG).show();

                }

                @Override
                public void onLongClick(View view, int position) {
                    Toast.makeText(getActivity(), "Long Prezzzzz!!!!!", Toast.LENGTH_LONG).show();
                }
            }
            ));
            recyclerView.setAdapter(new PermissionsByGroupAdapter());
        }

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach");

        // Retrieve PermissionGroup name passed from activity
        if (getArguments() != null) {
            mPermissionGroupName = getArguments().getString(ARGUMENT_NAME);
            Log.d("TAG", "Selected Permission Group Name:"+mPermissionGroupName);
        }
        mPackageManager = getActivity().getPackageManager();
        mPermissionInfoList = Utils.getPermissionInfoList(mPermissionGroupName, getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(Utils.toCanonicalTitleCase(mPermissionGroupName) + " permissions");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "Detached");
    }

    class PermissionsByGroupAdapter extends RecyclerView.Adapter<PermissionsByGroupHolder> {

        @Override
        public PermissionsByGroupHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_permissions_by_group_list_item, parent, false);
            return new PermissionsByGroupHolder(view);
        }

        @Override
        public void onBindViewHolder(PermissionsByGroupHolder holder, int position) {
            holder.setData(mPermissionInfoList.get(position));
        }

        @Override
        public int getItemCount() {
            return mPermissionInfoList.size();
        }
    }

    class PermissionsByGroupHolder extends RecyclerView.ViewHolder {

        private TextView mPermissionNameTextView;
        private ImageView mPermissionIconImageView;
        private TextView mPermissionProtectionLevelTextView;

        public PermissionsByGroupHolder(View itemView) {
            super(itemView);
            mPermissionIconImageView = (ImageView) itemView.findViewById(R.id.permission_icon);
            mPermissionNameTextView = (TextView) itemView.findViewById(R.id.permission_name);
            mPermissionProtectionLevelTextView = (TextView) itemView.findViewById(
                    R.id.permission_protection_level);
        }

        private  void setData(PermissionInfo permInfo) {
            mPermissionIconImageView.setImageDrawable(permInfo.loadIcon(mPackageManager));
            mPermissionNameTextView.setText(Utils.toCanonicalName(permInfo.name));
            mPermissionProtectionLevelTextView.setText(
                    Utils.protectionToString(permInfo.protectionLevel));
        }
    }

}
