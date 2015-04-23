package my.admin.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PermissionInfo;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import android.content.pm.ActivityInfo;

import my.admin.app.NewActivity;


public class AdminMiscUtil {

    public static final int MENU_CANCEL = 0;
    public static final int MENU_SHOW_ACTIVITIES = 1;
    public static final int MENU_SHOW_SHARED_LIBRARY_FILES = 2;
    public static final int MENU_SHOW_PERMISSIONS = 3;
    public static final int MENU_SHOW_SIGNATURES = 4;
    public static final int MENU_KILL = 5;
    public static final int MENU_DETAIL = 6;
    public static final int MENU_UNINSTALL = 7;
    public static PackageInfo localpkginfo = null;
    public static ApplicationInfo localappinfo = null;
       
    public static PackageInfo getPackageInfo(PackageManager pm, String name) {
        PackageInfo ret = null;
        try {
            ret = pm.getPackageInfo(name, PackageManager.GET_ACTIVITIES);
        } catch (NameNotFoundException e) {
            // e.printStackTrace();
        }
        return ret;
    }
    
    public static PackageInfo getPackageInfoPerms (PackageManager pm, String name) {
        PackageInfo ret = null;
        try {
            ret = pm.getPackageInfo(name, PackageManager.GET_PERMISSIONS);
        } catch (NameNotFoundException e) {
            // e.printStackTrace();
        }
        return ret;
    }
    
    public static ApplicationInfo getApplicationInfoSharedLibs (PackageManager pm, String name) {
        ApplicationInfo ret = null;
        try {
            ret = pm.getApplicationInfo(name, 1024);
        } catch (NameNotFoundException e) {
            // e.printStackTrace();
        }
        return ret;
    }
    
    
    public static PackageInfo getPackageInfoSigns (PackageManager pm, String name) {
        PackageInfo ret = null;
        try {
            ret = pm.getPackageInfo(name, PackageManager.GET_SIGNATURES);
        } catch (NameNotFoundException e) {
            // e.printStackTrace();
        }
        return ret;
    }

    public static Dialog getTaskMenuDialog(final MyAdminAppActivity ctx, final AdminDetailProcess dp) {
    	
    	return new AlertDialog.Builder(ctx).setTitle(dp.getTitle()).setItems(
                R.array.menu_task_operation, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case MENU_KILL: {
                                ctx.am.restartPackage(dp.getPackageName());
                                if (dp.getPackageName().equals(ctx.getPackageName())) return;
                                ctx.refresh();
                                return;
                            }
                            
                            case MENU_SHOW_ACTIVITIES: {
                            	                       
                            	String activitystr = "\n# The ACTIVITIES of ";
                            	activitystr += dp.getPackageName();
                            	activitystr += " App #\n\n";
                            	                            	
                            	dp.fetchPackageInfo();
                            	localpkginfo = dp.sharedpkginfo; 
                            	ActivityInfo[] activitylist = localpkginfo.activities;
                            	
                            	for (android.content.pm.ActivityInfo actv : activitylist) {
                            		activitystr += actv.name;
                            		activitystr += "\n";
                                }                     	

                            	ctx.printData(activitystr, ctx);
                
                            	return;
                            }
                            
                            
                            case MENU_SHOW_SHARED_LIBRARY_FILES: {
       	                       
                              	String libstr = "\n# The SHARED LIBS PATHS of ";
                              	libstr += dp.getPackageName();
                              	libstr += " App #\n\n";
                            	                      
                              	/*
                            	dp.fetchPackageInfoSharedLibs();
                            	localappinfo = dp.sharedappinfo;
                            	
                            	String[] libslist = localappinfo.sharedLibraryFiles ;
                            	
                            	if (libslist == null) {
                            		libstr = "\n No SHARED LIBS found for this App !";                     		
                            	}
                            	else {
                            		for (String lib : libslist) {
                            			libstr = libstr + lib;
                            			libstr = libstr + "\n";
                            		}
                            	}
                            	*/

                            	ctx.printData(libstr, ctx);
                
                            	return;
                            }
                            
                            case MENU_SHOW_PERMISSIONS: {
     	                       
                            	String permstr = "\n# PERMISSIONS of ";
                            	permstr += dp.getPackageName();
                            	permstr += " App #\n\n";
                            	                            	
                            	dp.fetchPackageInfoPerms();
                            	localpkginfo = dp.sharedpkginfo; 
                            	
                            	PermissionInfo[] permslist = localpkginfo.permissions;
                            	
                            	if (permslist == null) {
                            		permstr = "\n No PERMISSIONS found for this App !";                     		
                            	}
                            	else {
                            		for (android.content.pm.PermissionInfo perm : permslist) {
                            			permstr = permstr + perm.name;
                            			permstr = permstr + "\n";
                            		}
                            	}

                            	ctx.printData(permstr, ctx);
                
                            	return;
                            }                   
                       
                            
                            case MENU_SHOW_SIGNATURES: {
     	                       
                            	String signstr = "\n# The SIGNATURES of ";
                            	signstr += dp.getPackageName();
                            	signstr += " App #\n\n";
                            	                            	
                            	dp.fetchPackageInfoSigns();
                            	localpkginfo = dp.sharedpkginfo; 
                            	
                            	Signature[] signlist = localpkginfo.signatures;
                            	
                            	if (signlist == null) {
                            		signstr = "\n No SIGNATURES found for this App !";                     		
                            	}
                            	else {
                            		for (Signature sign : signlist) {
                            			signstr = signstr + sign.toString();
                            			signstr = signstr + "\n";
                            		}
                            	}

                            	ctx.printData(signstr, ctx);
                
                            	return;
                            }
                            
                            case MENU_UNINSTALL: {
                                Uri uri = Uri.fromParts("package", dp.getPackageName(), null);
                                Intent it = new Intent(Intent.ACTION_DELETE, uri);
                                try {
                                    ctx.startActivity(it);
                                } catch (Exception e) {
                                    Toast.makeText(ctx, e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                                return;
                            }
                            case MENU_DETAIL: {
                           
                            	Intent intent;
                            	                        	
                                if (android.os.Build.VERSION.SDK_INT >= 9) {
                                    
                                    Uri packageURI = Uri.parse("package:" + dp.getPackageName());
                                    intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS", packageURI);
                                    ctx.startActivity(intent);
                                } 
                                                                
                                return;
                            }
                        }

                   }
                }).create();
    }
}
