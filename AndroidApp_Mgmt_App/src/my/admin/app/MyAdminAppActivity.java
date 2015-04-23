package my.admin.app;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import my.admin.app.AdminDetailProcess;
import my.admin.app.AdminMiscUtil;
import my.admin.app.AdminPackageInfo;
import my.admin.app.AdminProcessInfo;
import my.admin.app.R;
import my.admin.app.MyAdminAppActivity;
import my.admin.app.AdminTaskListAdapters.ProcessListAdapter;
import my.admin.app.AdminTaskListAdapters.TasksListAdapter;
import my.admin.app.AdminDetailProcess;
import my.admin.app.AdminTaskListAdapters.ProcessListAdapter;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ScrollView;
import android.widget.AdapterView.OnItemClickListener;
import tools.RunScript;

public class MyAdminAppActivity extends Activity {
	
    public static final boolean DEBUG = true;
    ActivityManager am = null;
    private PackageManager pm;
    private static final int STAT_TASK = 0;
    private static final int STAT_CMDS = 1;
    private static final int STAT_RSVD = 2;
    public int currentStat = STAT_TASK;    

    private ProcessListAdapter adapter;
    private BroadcastReceiver loadFinish = new LoadFinishReceiver();    

    private ArrayList<AdminDetailProcess> listdp;
    private AdminProcessInfo pinfo = null;
    private AdminPackageInfo packageinfo = null;
    private List<RunningTaskInfo> tasklist = null;
    protected static final String ACTION_LOAD_FINISH = "my.admin.app.ACTION_LOAD_FINISH";

 	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);       
        
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        
        setContentView(R.layout.main);
        am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        pm = this.getApplicationContext().getPackageManager();     
        
        findViewById(R.id.cmnds_button3).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
            	
            	String cmdStr = null;
            	
            	EditText editText = (EditText)findViewById(R.id.editText1);
            	cmdStr = editText.getText().toString();
            	
            	if (cmdStr == null)
            		MyAdminAppActivity.this.printData("No Command Found !! \n", MyAdminAppActivity.this);
            	else {
            		// Running the Command and Dumping the Output as textView
            		MyAdminAppActivity.this.runCommandAndDump(cmdStr, MyAdminAppActivity.this);
            	}
            }

        });
        
      
        findViewById(R.id.tasks_button1).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                currentStat = STAT_TASK;
                refresh();                
            }

        });        

        packageinfo = new AdminPackageInfo(this);
                
    }
    
    
    ListView getListView() {
    	
    	return (ListView) this.findViewById(R.id.listbody);
    }
    
    
    public void refresh() {
    	
        setProgressBarIndeterminateVisibility(true);
        if (currentStat == STAT_TASK) {

            Thread t = new Thread(new Runnable() {
        
                @Override
                public void run() {
                  
                	pinfo = new AdminProcessInfo();
                    getRunningProcess();
                    
                    Intent in = new Intent(ACTION_LOAD_FINISH);
                    MyAdminAppActivity.this.sendBroadcast(in);
                  
                }

            });
            t.start();        	
        }        
        
       
    }
    
    // SHREYAS
    
    public void printData(String Data, final Context ctx) {
    	
    	 TextView tv = new TextView(ctx);
         tv.setText(Data);
         MyAdminAppActivity.this.setContentView(tv); 
    	
    }
    
    public void runCommandAndDump(String cmdStr, final Context ctx) {
    	
    	final TextView tv = new TextView(ctx);
    	final ScrollView sv = new ScrollView(ctx);
    	
    	String output = RunScript.runIt(cmdStr);    	
        tv.setText(output);        
        
        sv.post(new Runnable() {
            public void run() {
                sv.fullScroll(tv.getHeight());
            }

        }); 
        
        MyAdminAppActivity.this.setContentView(tv);     
    	
    	
    }    
    
    
    public AdminProcessInfo getProcessInfo() {
       return pinfo;
    }

    public AdminPackageInfo getPackageInfo() {
        return packageinfo;
    }
       
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // System.out.println("change");
    }

    @Override
    protected void onResume() {
        super.onResume();
      
        IntentFilter filter = new IntentFilter(ACTION_LOAD_FINISH);
        this.registerReceiver(loadFinish, filter);
        packageinfo = new AdminPackageInfo(this);
    
        refresh();
    }

    @Override
    public void onBackPressed(){
    	super.onBackPressed();
    	
        Intent a = new Intent(this, MyAdminAppActivity.class);
        a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(a);
         
    }
    
    @SuppressWarnings("unchecked")
    public void getRunningProcess() {    	
    	
        List<RunningAppProcessInfo> list2 = am.getRunningAppProcesses();
        
        listdp = new ArrayList<AdminDetailProcess>();
        for (RunningAppProcessInfo ti : list2) {
           
            if (ti.processName.equals("system") || ti.processName.equals("com.android.phone")) {
                continue;
            }
            AdminDetailProcess dp = new AdminDetailProcess(this, ti);
            dp.fetchApplicationInfo(packageinfo);
            dp.fetchPackageInfo();
            dp.fetchPsRow(pinfo);
            
            if (dp.isGoodProcess()) {
                listdp.add(dp);
               
            }
        }
        Collections.sort(listdp);
        adapter = new ProcessListAdapter(this, listdp);
        
    }

    public List<RunningTaskInfo> _getRunningTask() {

        List<RunningTaskInfo> list = am.getRunningTasks(100);
        
        for (RunningTaskInfo ti : list) {
            System.out.println(ti.baseActivity.getPackageName() + "/" + ti.baseActivity.getClassName() + "/"
                    + ti.id);
        }
        
        ListAdapter adapter = new TasksListAdapter(this, list);
        getListView().setAdapter(adapter);
        
        List<RunningAppProcessInfo> list2 = am.getRunningAppProcesses();
        
        for (RunningAppProcessInfo ti : list2) {
            System.out.println(ti.processName + "/" + ti.pid + "/" + ti.lru + "/" + ti.importance + "/"
                    + Arrays.toString(ti.pkgList) + "\n\n");
        }
        
        List<RunningServiceInfo> list3 = am.getRunningServices(100);
        
        for (RunningServiceInfo ti : list3) {
            System.out.println(ti.service.getPackageName() + "/" + ti.service.getClassName());
        }
        
        MemoryInfo outInfo = new MemoryInfo();
        am.getMemoryInfo(outInfo);
        
        System.out.println(outInfo.availMem);
        
        return list;
    }

     private class LoadFinishReceiver extends BroadcastReceiver {    	
    	
    		
        @Override
        public void onReceive(final Context ctx, Intent intent) {
        	MyAdminAppActivity.this.setProgressBarIndeterminateVisibility(false);
            MyAdminAppActivity.this.getListView().setAdapter(adapter);
            MyAdminAppActivity.this.getListView().setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                	
                	if (currentStat == STAT_TASK) {
                        AdminDetailProcess dp = listdp.get(arg2);
                        AdminMiscUtil.getTaskMenuDialog(MyAdminAppActivity.this, dp).show();
                    }
                }
                
            });
        }
       
    }

    
    @Override
    protected void onPause() {
        super.onPause();
        this.unregisterReceiver(loadFinish);
      }


    
}



