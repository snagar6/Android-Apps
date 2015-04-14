package tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import android.util.Log;

public class RunScript {
    private String command;
    private String stdout;
    private String stderr;
    private int retvalue;

    public RunScript(String command) {
        this.command = command;
    }

    public static String runIt(String command) {
        return new RunScript(command).run();
    	
    	// SHREYAS - Checking for root access - cmdline ....
    	// return new RunScript(command).canRunRootCommands();
    }

    public String run() {
        String sRet = "";
        try {
            // Runtime rt = Runtime.getRuntime();
            // rt.runFinalizersOnExit(true);
            // Process m_process=rt.exec(this.CMD);
        
        	
            final Process m_process = Runtime.getRuntime().exec(this.command);
            // System.out.println("start");
            final StringBuilder sbread = new StringBuilder();
            Thread tout = new Thread(new Runnable() {
                public void run() {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(m_process
                            .getInputStream()), 8192);
                    String ls_1 = null;
                    try {
                        while ((ls_1 = bufferedReader.readLine()) != null) {
                            sbread.append(ls_1).append("\n");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            bufferedReader.close();
                        } catch (IOException e) {
                            // ignore
                        }
                    }
                }
            });
            tout.start();
            final StringBuilder sberr = new StringBuilder();
            Thread terr = new Thread(new Runnable() {
                public void run() {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(m_process
                            .getErrorStream()), 8192);
                    String ls_1 = null;
                    try {
                        while ((ls_1 = bufferedReader.readLine()) != null) {
                            sberr.append(ls_1).append("\n");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            bufferedReader.close();
                        } catch (IOException e) {
                            // ignore
                        }
                    }
                }
            });
            terr.start();
            // System.out.println("wait");
            this.retvalue = m_process.waitFor();
            while (tout.isAlive()) {
                Thread.sleep(50);
            }
            // System.out.println("output:\n"+sbread.toString());
            if (terr.isAlive()) terr.interrupt();
            // System.out.println("error:\n"+sberr.toString());
            this.stdout = sbread.toString();
            this.stderr = sberr.toString();
            sRet = this.stdout + this.stderr;
        } catch (java.io.IOException ee) {
            System.err.println("RunScript have a IO error :" + ee.getMessage());
            return null;
        } catch (InterruptedException ie) {
            System.err.println("RunScript have a interrupte error:" + ie.getMessage());
            return null;
        } catch (Exception ex) {
            System.err.print("RunScript have a error :" + ex.getMessage());
            return null;
        }
        return sRet;
    }
    
    
    // RUN ROOT Check ...
    
    public static String canRunRootCommands()
    {
      String retval = "nope -  no ROOT! #1";
      Process suProcess;
      
      try
      {
        suProcess = Runtime.getRuntime().exec("su");
        
        DataOutputStream os = 
            new DataOutputStream(suProcess.getOutputStream());
        DataInputStream osRes = 
            new DataInputStream(suProcess.getInputStream());
        
        if (null != os && null != osRes)
        {
          // Getting the id of the current user to check if this is root
          // os.writeBytes("id\n");
          // os.flush();
        	
          return new RunScript("id").run();
          
          /*

          String currUid = osRes.readLine();
          boolean exitSu = false;
          if (null == currUid)
          {
            retval = "nope -  no ROOT! #2";
            exitSu = false;
            // System.err.print("ROOT", "Can't get root access or denied by user");
          }
          else if (true == currUid.contains("uid=0"))
          {
            retval = "YUP-root!";
            exitSu = true;
            // System.err.print("ROOT", "Root access granted");
          }
          else
          {
            retval = "nope -  no ROOT! #3";
            exitSu = true;
            // Log.d("ROOT", "Root access rejected: " + currUid);
          }

          if (exitSu)
          {
            os.writeBytes("exit\n");
            os.flush();
          }
          */
        }        
      }
      
      catch (Exception e)
      {
        // Can't get root !
        // Probably broken pipe exception on trying to write to output
        // stream after su failed, meaning that the device is not rooted
        
        retval = "nope -  no ROOT! #4";
        // Log.d("ROOT", "Root access rejected [" +
             //  e.getClass().getName() + "] : " + e.getMessage());
      }

      return retval;
     
    }
    
    
    

    public String getCommand() {
        return command;
    }

    public String getStdout() {
        return stdout;
    }

    public String getStderr() {
        return stderr;
    }

    public int getRetvalue() {
        return retvalue;
    }

}
