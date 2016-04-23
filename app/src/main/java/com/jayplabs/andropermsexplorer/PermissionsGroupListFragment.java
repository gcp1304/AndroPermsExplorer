package com.jayplabs.andropermsexplorer;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PermissionGroupInfo;
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

/**
 * Created by Chandra Gopalaiah on 4/21/16.
 */
public class PermissionsGroupListFragment extends Fragment {

    private static final String TAG = PermissionsGroupListFragment.class.getSimpleName();


    private List<PermissionGroupInfo> mPermissionGroupInfoList;


    private OnPermissionsGroupSelected mListener;
    private PackageManager mPackageManager = null;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon
     * screen orientation changes).
     */
    public PermissionsGroupListFragment() {
        // Required empty public constructor
    }

    public static PermissionsGroupListFragment newInstance() {
        Log.d(TAG, "newInstance");
        return new PermissionsGroupListFragment();
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
                R.layout.fragment_permissions_group_list,
                container,
                false);

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
                    //String groupName = mPermissionGroupInfoList.get(position).name;
                    mListener.onPermissionsGroupSelected(mPermissionGroupInfoList.get(position).name);
                }

                @Override
                public void onLongClick(View view, int position) {
                    Toast.makeText(getActivity(), "Long Prezzzzz!!!!!", Toast.LENGTH_LONG).show();
                }
            }
            ));
            recyclerView.setAdapter(new PermissionsGroupAdapter());
        }

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach");
        if (context instanceof OnPermissionsGroupSelected) {
            mListener = (OnPermissionsGroupSelected) context;
        } else {
            throw new RuntimeException(context.toString() +
                    " must implement OnPermissionGroupSelected");
        }

        mPermissionGroupInfoList = Utils.getAllPermissionGroupsList(getActivity());
        mPackageManager = getActivity().getPackageManager();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.app_name);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    class PermissionsGroupAdapter extends RecyclerView.Adapter<PermissionsGroupHolder> {

        @Override
        public PermissionsGroupHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Log.d(TAG, "onCreateViewHolder");
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_permissions_group_list_item, parent, false);
            return new PermissionsGroupHolder(view);
        }

        @Override
        public void onBindViewHolder(PermissionsGroupHolder holder, final int position) {
            Log.d(TAG, "onBindViewHolder");
            PermissionGroupInfo groupInfo = mPermissionGroupInfoList.get(position);
            holder.setData(groupInfo);
        }

        @Override
        public int getItemCount() {
            return mPermissionGroupInfoList.size();
        }
    }

    class PermissionsGroupHolder extends RecyclerView.ViewHolder {

        private TextView mPermissionGroupNameTextView;
        private ImageView mPermissionGroupIconImageView;
        private TextView mPermissionGroupDescTextView;

        public PermissionsGroupHolder(View itemView) {
            super(itemView);
            mPermissionGroupNameTextView = (TextView) itemView.findViewById(R.id.permission_group_name);
            mPermissionGroupIconImageView = (ImageView) itemView.findViewById(R.id.permission_group_icon);
            mPermissionGroupDescTextView = (TextView) itemView.findViewById(R.id.permission_group_desc);
        }

        private void setData(PermissionGroupInfo permissionGroupInfo) {
            mPermissionGroupNameTextView.setText(Utils.toCanonicalName(permissionGroupInfo.name));
            mPermissionGroupIconImageView.setImageDrawable(permissionGroupInfo.loadIcon(mPackageManager));
            mPermissionGroupDescTextView.setText(permissionGroupInfo.loadDescription(mPackageManager).toString());
        }

    }

    /**
     * This interface must be implemented by activities that contain this fragment to allow an
     * interaction in this fragment to be communicated to the activity and potentially other
     * fragments contained in that activity.
     * <p/>
     * See the Android Training lesson <a href= "http://developer.android
     * .com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnPermissionsGroupSelected {
        void onPermissionsGroupSelected(String name);
    }


}
