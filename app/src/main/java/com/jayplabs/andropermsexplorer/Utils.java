package com.jayplabs.andropermsexplorer;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Created by Chandra Gopalaiah on 22,April,2016.
 */
public class Utils {

    private static final String TAG = Utils.class.getSimpleName();
    /**
     * Uses PackageManager getAllPermissionGroups() and queryPermissionsByGroup()
     * to enumerate the Android permission hierarchy.
     */
    public static List<PermissionGroupInfo> getAllPermissionGroupsList(Context context) {
        PackageManager pm = context.getPackageManager();
        if (pm == null) {
            return null;
        }

        ArrayList<String> permissionGroupsNameList = new ArrayList<>();
        /*
        Get a list of all permission groups and sort them alphabetically.
        Then add to the end of the list the special case of a null group name. There can be
        numerous permissions that are not listed under a group name.
         */
        List<PermissionGroupInfo> groupInfoList = pm.getAllPermissionGroups(0);
        if (groupInfoList == null) {
            return null;
        }
        return groupInfoList;

        /*
        for (PermissionGroupInfo groupInfo : groupInfoList) {
            if (groupInfo.name != null) {
                permissionGroupsNameList.add(groupInfo.name);
            }

        }
        Collections.sort(permissionGroupsNameList);
        return permissionGroupsNameList;
        */
    }


    public static List<PermissionInfo> getPermissionInfoList(String groupName, Context context) {
        PackageManager pm = context.getPackageManager();

        if (pm == null) {
            return null;
        }

        List<PermissionInfo> permissionInfoList = null;
        try {
            permissionInfoList =
                    pm.queryPermissionsByGroup(groupName, PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return permissionInfoList;
    }

    /**
     * Gets and returns a list of all permission under the specified group, sorted alphabetically.
     *
     * N.B. groupName can be null. The docs for PackageManager.queryPermissionsByGroup() say
     * "Use null to find all of the permissions not associated with a group."
     */
    public static ArrayList<String> getPermissionsForGroup(String groupName, Context context) {
        PackageManager pm = context.getPackageManager();

        if (pm == null) {
            return null;
        }

        final ArrayList<String> permissionNameList = new ArrayList<>();

        try {
            List<PermissionInfo> permissionInfoList =
                    pm.queryPermissionsByGroup(groupName, PackageManager.GET_META_DATA);
            if (permissionInfoList != null) {
                for (PermissionInfo permInfo : permissionInfoList) {
                    String permName = permInfo.name;
                    if (permName == null) {
                        permName = "null";
                    } else if (permName.isEmpty()) {
                        permName = "empty";
                    }
                    permissionNameList.add(permName);
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            //e.printStackTrace();
            Log.d(TAG, "permissions not found for group = " + groupName);
        }
        Collections.sort(permissionNameList);

        return permissionNameList;
    }


    public static String toCanonicalName(String givenString) {
        String[] splitString = givenString.split("\\.");

        return splitString[splitString.length - 1];
        //String replacedString = splitString[splitString.length - 1].replace("_", " ").toLowerCase(Locale.ENGLISH);
        //return Utils.toTitleCase(replacedString);
    }

    public static String toCanonicalTitleCase(String givenString) {
        String canonicalString = toCanonicalName(givenString);
        String replacedString = canonicalString.replace("_", " ").toLowerCase(Locale.ENGLISH);
        return Utils.toTitleCase(replacedString);
    }

    public static String toTitleCase(String givenString) {
        String[] arr = givenString.split(" ");
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < arr.length; i++) {
            sb.append(Character.toUpperCase(arr[i].charAt(0)))
                    .append(arr[i].substring(1)).append(" ");
        }
        return sb.toString().trim();
    }

    public static String protectionToString(int level) {
        String protLevel = "????";
        switch (level&PermissionInfo.PROTECTION_MASK_BASE) {
            case PermissionInfo.PROTECTION_DANGEROUS:
                protLevel = "dangerous";
                break;
            case PermissionInfo.PROTECTION_NORMAL:
                protLevel = "normal";
                break;
            case PermissionInfo.PROTECTION_SIGNATURE:
                protLevel = "signature";
                break;
            case PermissionInfo.PROTECTION_SIGNATURE_OR_SYSTEM:
                protLevel = "signatureOrSystem";
                break;
        }
        if ((level&PermissionInfo.PROTECTION_FLAG_SYSTEM) != 0) {
            protLevel += "|system";
        }
        if ((level&PermissionInfo.PROTECTION_FLAG_DEVELOPMENT) != 0) {
            protLevel += "|development";
        }
        return protLevel;
    }


    //TODO Just for testing - Remove later
    public static void showPermissionTree(Context context) {
        PackageManager pm = context.getPackageManager();
        if (pm == null) {
            return;
        }

        /*
        Get a list of all permission groups and sort them alphabetically.
        Then add to the end of the list the special case of a null group name. There can be
        numerous permissions that are not listed under a group name.
         */
        List<PermissionGroupInfo> groupInfoList = pm.getAllPermissionGroups(0);
        if (groupInfoList == null) {
            return;
        }

        ArrayList<String> groupNameList = new ArrayList<>();
        ArrayList<String> simpleGroupNameList = new ArrayList<>();
        for (PermissionGroupInfo groupInfo : groupInfoList) {
            String groupName = groupInfo.name;

            String[] splitName = groupName.split("\\.");
            Log.d(TAG+"_CHANDRA", Utils.toTitleCase(splitName[2].replace("_", " ").toLowerCase(Locale.ENGLISH)));

            if (groupName != null) {
                if (Build.VERSION.SDK_INT >= 17) {
                    /*
                     * SDK 17 added the flags field. If non-zero, the permission group contains
                     * permissions that control access to user personal data.
                     * N.B. These are the permissions groups that are called "dangerous" in
                     * Marshmallow.
                     */
                    if (groupInfo.flags != 0) {
                        groupName += " (dangerous)";
                    }
                }
                groupNameList.add(groupName);
            }
        }
        Collections.sort(groupNameList);
        /*
        * Loop though each permission group, adding to the StringBuilder the group name and
        * the list of all permissions under that group.
        */
        StringBuilder sb = new StringBuilder(10000);
        final String INDENT = "     ";

        for (String groupName : groupNameList) {
            if (groupName == null) {
                groupName = "null";
            }

            sb.append("* ").append(groupName).append("\n");

            ArrayList<String> permissionNameList = getPermissionsForGroup(groupName, context);
            if (permissionNameList.size() > 0) {
                for (String permission : permissionNameList) {
                    sb.append(INDENT).append(permission).append("\n");
                }
            } else {
                sb.append(INDENT).append("no permissions in this group\n");
            }
            sb.append("\n");
        }
        Log.d(TAG+"_CHANDRA", sb.toString());
    }
}