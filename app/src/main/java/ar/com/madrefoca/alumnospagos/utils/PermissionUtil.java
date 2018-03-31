package ar.com.madrefoca.alumnospagos.utils;

import android.content.pm.PackageManager;

/**
 * Created by fernando on 30/03/18.
 */

public class PermissionUtil {

    public static boolean verifyPermissions(int[] grantResults) {
        // At least one result must be checked.
        if (grantResults.length < 1) {
            return false;
        }

        // Verify that each required permission has been granted, otherwise return false.
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
}
