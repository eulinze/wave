package com.ylz.waveform.tools;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import java.util.ArrayList;

public class PermissionUtils {
	private static final String TAG = "PermissionUtils";
	public static final int PERMISSION_SETTING_REQ_CODE = 0x1000;

	private static void requestPermissionsWrapper(Object cxt, String[] permission, int requestCode) {
		if (cxt instanceof Activity) {
			Activity activity = (Activity) cxt;
			ActivityCompat.requestPermissions(activity, permission, requestCode);
		} else if (cxt instanceof Fragment) {
			Fragment fragment = (Fragment) cxt;
			fragment.requestPermissions(permission, requestCode);
		} else {
			throw new RuntimeException("cxt is net a activity or fragment");
		}
	}

	@TargetApi(23)
	private static boolean checkSelfPermissionWrapper(Object cxt, String permission) {
		if (cxt instanceof Activity) {
			Activity activity = (Activity) cxt;
			return ActivityCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
		} else if (cxt instanceof Fragment) {
			Fragment fragment = (Fragment) cxt;
			return fragment.getActivity().checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
		} else {
			throw new RuntimeException("cxt is net a activity or fragment");
		}
	}

	private static String[] checkSelfPermissionArray(Object cxt, String[] permission) {
        ArrayList<String> permiList = new ArrayList<String>();
        for (String p : permission) {
            if (!checkSelfPermissionWrapper(cxt, p)) {
                permiList.add(p);
            }
        }

        return permiList.toArray(new String[permiList.size()]);
    }

	@TargetApi(Build.VERSION_CODES.M)
	public static boolean checkPermissionArray(Object cxt, String[] permission, int requestCode) {
		String[] permissionNo = checkSelfPermissionArray(cxt, permission);
		if (permissionNo.length > 0) {
			requestPermissionsWrapper(cxt, permissionNo, requestCode);
			return false;
		} else
			return true;
	}

	public static boolean verifyPermissions(int[] grantResults) {
		if (grantResults.length < 1) {
			return false;
		}

		for (int result : grantResults) {
			if (result != PackageManager.PERMISSION_GRANTED) {
				return false;
			}
		}
		return true;
	}
}
