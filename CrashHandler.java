package com.mdbiomedical.app.vion.vian_health.view;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.Thread.UncaughtExceptionHandler;

import com.mdbiomedical.app.vion.vian_health.MainActivity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

/**
 * @author Stay
 *      save error log
 */
public class CrashHandler implements UncaughtExceptionHandler {  
    /**ONLY OPEN DEBUG when need Error Log.
     *  
     * 
     * */ 
    public static final boolean DEBUG = true;  
    /**  default UncaughtException*/ 
    private Thread.UncaughtExceptionHandler mDefaultHandler;  
    /** CrashHandler  */ 
    private static CrashHandler INSTANCE;  
 
   private Context mContext;  
    /** make sure only one CrashHandler */ 
    private CrashHandler() {}  
    /** get instance*/ 
    public static CrashHandler getInstance() {  
        if (INSTANCE == null) {  
            INSTANCE = new CrashHandler();  
        }  
        return INSTANCE;  
    }  
    
    /** 
     * initial UncaughtException
     * get getDefaultUncaughtExceptionHandler
     * set  CrashHandler as default
     *  
     * @param ctx 
     */ 
    public void init(Context ctx) {  
        mContext = ctx;  
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();  
        Thread.setDefaultUncaughtExceptionHandler(this);  
    }  
    
    /** 
     * when UncaughtException happen,into  handleException
     */ 
    @Override 
    public void uncaughtException(Thread thread, Throwable ex) { 
    	Log.e("CrashHandler", ex.getMessage(), ex);

    	
        if (!handleException(ex) && mDefaultHandler != null) {  
            //handleException not work, do it as system
            mDefaultHandler.uncaughtException(thread, ex);  
        } else {  //if handleException do the job,close app
            try {  
                Thread.sleep(3000);  
            } catch (InterruptedException e) {  
            }  
            AlarmManager alarm = (AlarmManager) mContext.getSystemService(Activity.ALARM_SERVICE);
            PendingIntent pi = PendingIntent.getActivity(mContext, 12345, new Intent(mContext,
                                MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK),
                                PendingIntent.FLAG_ONE_SHOT);
            alarm.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 0, pi);
            android.os.Process.killProcess(android.os.Process.myPid());  
            System.exit(10);  
        }  
    }  
    
    /** 
     * save error report
     * 
     * 
     * @return 
     * true means already take that errorï¿???
     * false means threw that error to system.
     * true wont show system error, false does.
     */ 
    private boolean handleException(final Throwable ex) {  
        if (ex == null) {  
            return false;  
        }  
        //final String msg = ex.getLocalizedMessage();  
        final StackTraceElement[] stack = ex.getStackTrace();
        final String message = ex.getMessage();
        new Thread() {  
            @Override 
            public void run() {  
                Looper.prepare();  
                Toast.makeText(mContext, "Prepare to restart", Toast.LENGTH_LONG).show();  
//                String fileName = "crash-" + System.currentTimeMillis()  + ".log";  
//                File file = new File(Environment.getExternalStorageDirectory(), fileName);
//                try {
//                    FileOutputStream fos = new FileOutputStream(file,true);
//                    fos.write(message.getBytes());
//                    for (int i = 0; i < stack.length; i++) {
//                        fos.write(stack[i].toString().getBytes());
//                    }
//                    fos.flush();
//                    fos.close();
//                } catch (Exception e) {
//                }
                Looper.loop();  
            }  
    
        }.start();  
        
        return false;  
    }  
    

}