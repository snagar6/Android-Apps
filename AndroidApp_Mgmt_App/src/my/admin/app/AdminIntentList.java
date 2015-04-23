package my.admin.app;

import java.util.List;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

public class AdminIntentList {
    private static List<ResolveInfo> infoList = null;

    public static synchronized List<ResolveInfo> getRunableList(PackageManager pm, boolean reload) {
        if (infoList == null || reload == true) {
            Intent baseIntent = new Intent(Intent.ACTION_MAIN);
            baseIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            infoList = pm.queryIntentActivities(baseIntent, 0);
        }
        return infoList;
    }

    public static Intent getIntent(String packageName, PackageManager pm) {
        List<ResolveInfo> list = getRunableList(pm, false);
        for (ResolveInfo info : list) {
            // System.out.println(packageName + " == " + info.activityInfo.packageName);
            if (packageName.equals(info.activityInfo.packageName)) {
                Intent i = new Intent(Intent.ACTION_MAIN);
                i.addCategory(Intent.CATEGORY_LAUNCHER);
                i.setClassName(packageName, info.activityInfo.name);
                return i;
            }
        }
        return null;
    }

}