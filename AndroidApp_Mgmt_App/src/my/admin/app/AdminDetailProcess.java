package my.admin.app;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

@SuppressWarnings("rawtypes")
public class AdminDetailProcess implements Comparable {
	
	private AdminProcessInfo.PsRow psrow = null;
    private ApplicationInfo appinfo = null;
    private PackageInfo pkginfo = null;
    private ActivityManager.RunningAppProcessInfo runinfo = null;
    // private ActivityManager.RunningTaskInfo taskinfo = null;
    private String title = null;
    private PackageManager pm;
    private Intent intent = null;
    
    // SHREYAS	
    public PackageInfo sharedpkginfo = null;
    public ApplicationInfo sharedappinfo = null;

    public AdminDetailProcess(Context ctx, ActivityManager.RunningAppProcessInfo runinfo) {
        this.runinfo = runinfo;
        pm = ctx.getApplicationContext().getPackageManager();
    }

    public AdminProcessInfo.PsRow getPsrow() {
        return psrow;
    }

    public void setPsrow(AdminProcessInfo.PsRow psrow) {
        this.psrow = psrow;
    }

    public ApplicationInfo getAppinfo() {
        return appinfo;
    }

    public void setAppinfo(ApplicationInfo appinfo) {
        this.appinfo = appinfo;
    }

    public PackageInfo getPkginfo() {
        return pkginfo;
    }

    public void setPkginfo(PackageInfo pkginfo) {
        this.pkginfo = pkginfo;
    }

    public ActivityManager.RunningAppProcessInfo getRuninfo() {
        return runinfo;
    }

    public void setRuninfo(ActivityManager.RunningAppProcessInfo runinfo) {
        this.runinfo = runinfo;
    }

    public void fetchApplicationInfo(AdminPackageInfo pkg) {
        if (appinfo == null) appinfo = pkg.getInfo(runinfo.processName);
    }

    // SHREYAS
    
    public void fetchPackageInfo() {
        if (appinfo != null) {
        	pkginfo = AdminMiscUtil.getPackageInfo(pm, appinfo.packageName);
        	sharedpkginfo = pkginfo;               	
        }
    }

    public void fetchPackageInfoPerms() {
        if (appinfo != null) {
        	pkginfo = AdminMiscUtil.getPackageInfoPerms(pm, appinfo.packageName);
        	sharedpkginfo = pkginfo;
        }
    }
    
    
    public void fetchPackageInfoSharedLibs() {
        if (appinfo != null) {
        	sharedappinfo = AdminMiscUtil.getApplicationInfoSharedLibs(pm, appinfo.packageName);           	
        }
    }
    
    
    public void fetchPackageInfoSigns() {
        if (appinfo != null) {
        	pkginfo = AdminMiscUtil.getPackageInfoSigns(pm, appinfo.packageName);
        	sharedpkginfo = pkginfo;
        }
    }
    
    
    public void fetchPsRow(AdminProcessInfo pi) {
        if (psrow == null) psrow = pi.getPsRow(runinfo.processName);
    }

    public boolean isGoodProcess() {
        return runinfo != null && appinfo != null && pkginfo != null && pkginfo.activities != null
                && (pkginfo.activities.length > 0);
    }

    public String getPackageName() {
        return appinfo.packageName;
    }

    public String getBaseActivity() {
        return pkginfo.activities[0].name;
    }

    public Intent getIntent() throws NameNotFoundException {
        if (intent != null) return intent;
        intent = null;
        intent = pm.getLaunchIntentForPackage(pkginfo.packageName);
		if (intent != null) {
		    intent = intent.cloneFilter();
		    intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
		    return intent;
		}
		if (pkginfo.activities.length == 1) {
		    intent = new Intent(Intent.ACTION_MAIN);
		    intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
		    intent.setClassName(pkginfo.packageName, pkginfo.activities[0].name);
		    return intent;
		}
		intent = AdminIntentList.getIntent(pkginfo.packageName, pm);
		if (intent != null) {
		    intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
		    return intent;
		}
		
		return null;
    }

    public String getTitle() {
        if (title == null) title = appinfo.loadLabel(pm).toString();
        return title;
    }

    @Override
    public int compareTo(Object another) {
        if (another instanceof AdminDetailProcess && another != null) {
            return this.getTitle().compareTo(((AdminDetailProcess) another).getTitle());
        }
        return -1;
    }

}
