package com.example.autoupdate;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import com.example.autoupdate.BuildConfig;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
public class MainActivity extends Activity 
{

    ProgressDialog dialog;

    class PInfo {
        private String appname = "";
        private String pname = "";
        private String versionName = "";
        private int versionCode = 0;
        //private Drawable icon;
        /*private void prettyPrint() {
            //Log.v(appname + "\t" + pname + "\t" + versionName + "\t" + versionCode);
        }*/
    }
    public int VersionCode;
    public String VersionName="";
    public String ApkName ;
    public String AppName ;
    public String BuildVersionPath="";
    public String urlpath ;
    public String PackageName;
    public String InstallAppPackageName;
    public String Text="";

    TextView tvApkStatus;
    Button btnCheckUpdates;
    TextView tvInstallVersion;
    String temp;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy);
        //Text= "Old".toString();
        Text= "New".toString();


        ApkName = "AutoUpdate.apk";//"Test1.apk";// //"DownLoadOnSDcard_01.apk"; //      
        AppName = "AutoUpdate";//"Test1"; //

        BuildVersionPath = "http://88.150.160.88/Version.txt".toString();
        PackageName = "com.example.autoupdate".toString(); //"package:com.Test1".toString();

        urlpath = "http://88.150.160.88/"+ Text.toString()+"_Apk/" + ApkName.toString();


        tvApkStatus =(TextView)findViewById(R.id.tvApkStatus);
        tvApkStatus.setText(Text+" Apk Download.".toString());


        tvInstallVersion = (TextView)findViewById(R.id.tvInstallVersion);
        String temp = getInstallPackageVersionInfo(AppName.toString());
        tvInstallVersion.setText("" +temp.toString());

        btnCheckUpdates =(Button)findViewById(R.id.btnCheckUpdates);
        btnCheckUpdates.setOnClickListener(new OnClickListener() 
        {       
            @Override
            public void onClick(View arg0) 
            {
                callAscyntask();

            }
        });

    }// On Create END.



    void callAscyntask(){

        new AsyncTask<Void,Void,Void>(){

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog = new ProgressDialog(MainActivity.this);
                dialog.setMessage("Loding");
             dialog.show();
            }

            @Override
            protected Void doInBackground(Void... voids) {

                temp =GetVersionFromServer(BuildVersionPath);
                 return null;

            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

               // Toast.makeText(MainActivity.this,"on Post Call -"+temp,Toast.LENGTH_SHORT).show();

                dialog.dismiss();

                if(checkInstalledApp(AppName.toString()) == true)
                {
                    Toast.makeText(getApplicationContext(), "Application Found " + AppName.toString(), Toast.LENGTH_SHORT).show();


                }else{
                    Toast.makeText(getApplicationContext(), "Application Not Found. "+ AppName.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();


    }

    private Boolean checkInstalledApp(String appName){
        return getPackages(appName);    
    }

    // Get Information about Only Specific application which is Install on Device.
    public String getInstallPackageVersionInfo(String appName) 
    {
        String InstallVersion = "";     
        ArrayList<PInfo> apps = getInstalledApps(false); /* false = no system packages */
        final int max = apps.size();
        for (int i=0; i<max; i++) 
        {
            //apps.get(i).prettyPrint();        
            if(apps.get(i).appname.toString().equals(appName.toString()))
            {
                InstallVersion = "Install Version Code: "+ apps.get(i).versionCode+
                    " Version Name: "+ apps.get(i).versionName.toString();
                break;
            }
        }

        return InstallVersion.toString();
    }
    private Boolean getPackages(String appName) 
    {
        Boolean isInstalled = false;
        ArrayList<PInfo> apps = getInstalledApps(false); /* false = no system packages */
        final int max = apps.size();
        for (int i=0; i<max; i++) 
        {
            //apps.get(i).prettyPrint();

            if(apps.get(i).appname.toString().equals(appName.toString()))
            {
                /*if(apps.get(i).versionName.toString().contains(VersionName.toString()) == true &&
                        VersionCode == apps.get(i).versionCode)
                {
                    isInstalled = true;
                    Toast.makeText(getApplicationContext(),
                            "Code Match", Toast.LENGTH_SHORT).show(); 
                    openMyDialog();
                }*/
                if(VersionCode <= apps.get(i).versionCode)
                {
                    isInstalled = true;

                    /*Toast.makeText(getApplicationContext(),
                            "Install Code is Less.!", Toast.LENGTH_SHORT).show();*/

                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() 
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which)
                            {
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                                //SelfInstall01Activity.this.finish(); Close The App.


                                callDownloadAscyntask();


                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked

                                break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("New Upadate Available..").setPositiveButton("Yes Proceed", dialogClickListener)
                        .setNegativeButton("No.", dialogClickListener).show();

                }    
                if(VersionCode > apps.get(i).versionCode)
                {
                    isInstalled = true;
                    /*Toast.makeText(getApplicationContext(),
                            "Install Code is better.!", Toast.LENGTH_SHORT).show();*/

                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() 
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which)
                            {
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                                //SelfInstall01Activity.this.finish(); Close The App.

                                callDownloadAscyntask();

                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked

                                break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("NO need to Install.").setPositiveButton("Install Forcely", dialogClickListener)
                        .setNegativeButton("Cancel.", dialogClickListener).show();              
                }
            }
        }

        return isInstalled;
    }


    void callDownloadAscyntask(){

        new AsyncTask<Void,Void,Void>(){

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog = new ProgressDialog(MainActivity.this);
                dialog.setMessage("Downloading new file");
                dialog.show();
            }

            @Override
            protected Void doInBackground(Void... voids) {
                DownloadOnSDcard();

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                dialog.dismiss();


                Toast.makeText(MainActivity.this,"on Post Call "+temp,Toast.LENGTH_SHORT).show();
                InstallApplication();
                UnInstallApplication(PackageName.toString());
            }
        }.execute();
    }


    private ArrayList<PInfo> getInstalledApps(boolean getSysPackages) 
    {       
        ArrayList<PInfo> res = new ArrayList<PInfo>();        
        List<PackageInfo> packs = getPackageManager().getInstalledPackages(0);

        for(int i=0;i<packs.size();i++) 
        {
            PackageInfo p = packs.get(i);
            if ((!getSysPackages) && (p.versionName == null)) {
                continue ;
            }
            PInfo newInfo = new PInfo();
            newInfo.appname = p.applicationInfo.loadLabel(getPackageManager()).toString();
            newInfo.pname = p.packageName;
            newInfo.versionName = p.versionName;
            newInfo.versionCode = p.versionCode;
            //newInfo.icon = p.applicationInfo.loadIcon(getPackageManager());
            res.add(newInfo);
        }
        return res; 
    }


    public void UnInstallApplication(String packageName)// Specific package Name Uninstall.
    {
        //Uri packageURI = Uri.parse("package:com.CheckInstallApp");

        try {
            Uri packageURI = Uri.parse(packageName.toString());
            Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
            startActivity(uninstallIntent);
        }catch (Exception e){
            Log.e("exception",e.toString());
        }
    }
    public void InstallApplication()
    {   
        Uri packageURI = Uri.parse(PackageName.toString());
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, packageURI);

//      Intent intent = new Intent(android.content.Intent.ACTION_VIEW);

        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //intent.setFlags(Intent.ACTION_PACKAGE_REPLACED);

        //intent.setAction(Settings. ACTION_APPLICATION_SETTINGS);

        intent.setDataAndType
        (Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/download/"  + ApkName.toString())), 
        "application/vnd.android.package-archive");

        // Not open this Below Line Bcuz...
        ////intent.setClass(this, Project02Activity.class); // This Line Call Activity Recursively its dangerous.

        startActivity(intent);  
    }



    public String GetVersionFromServer(String BuildVersionPath)
    {
        //this is the file you want to download from the remote server          
        //path ="http://10.0.2.2:82/Version.txt";
        //this is the name of the local file you will create
        // version.txt contain Version Code = 2; \n Version name = 2.1;             
        URL u;
        String res="";
        try {
            u = new URL(BuildVersionPath.toString());

            HttpURLConnection c = (HttpURLConnection) u.openConnection();           
            c.setRequestMethod("GET");
            c.setDoOutput(true);
            c.connect();

            //Toast.makeText(getApplicationContext(), "HttpURLConnection Complete.!", Toast.LENGTH_SHORT).show();  

            InputStream in = c.getInputStream();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024]; //that stops the reading after 1024 chars..
            //in.read(buffer); //  Read from Buffer.
            //baos.write(buffer); // Write Into Buffer.

            int len1 = 0;
            while ( (len1 = in.read(buffer)) != -1 ) 
            {               
                baos.write(buffer,0, len1); // Write Into ByteArrayOutputStream Buffer.
            }

            String temp = "";     
            String s = baos.toString();// baos.toString(); contain Version Code = 2; \n Version name = 2.1;
            res = s;
           /* for (int i = 0; i < s.length(); i++)
            {               
                i = s.indexOf("=") + 1; 
                while (s.charAt(i) == ' ') // Skip Spaces
                {
                    i++; // Move to Next.
                }
                while (s.charAt(i) != ';'&& (s.charAt(i) >= '0' && s.charAt(i) <= '9' || s.charAt(i) == '.'))
                {
                    temp = temp.toString().concat(Character.toString(s.charAt(i))) ;
                    i++;
                }
                //
                s = s.substring(i); // Move to Next to Process.!
                temp = temp + " "; // Separate w.r.t Space Version Code and Version Name.
            }
            String[] fields = temp.split(" ");// Make Array for Version Code and Version Name.

            VersionCode = Integer.parseInt(fields[0].toString());// .ToString() Return String Value.
            VersionName = fields[1].toString();
*/
            baos.close();


        }
        catch (MalformedURLException e) {
            Toast.makeText(getApplicationContext(), "Error." + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (IOException e) {           
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Error." + e.getMessage(), Toast.LENGTH_SHORT).show();
        }


        return res;
            //return true;
    }// Method End.

    // Download On My Mobile SDCard or Emulator.
    public void DownloadOnSDcard()
    {
        try{
            //URL url = new URL(urlpath.toString()); // Your given URL.

            URL url = new URL("http://88.150.160.88/AutoUpdate.apk"); // Your given URL.

            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("GET");
            c.setDoOutput(true);
            c.connect(); // Connection Complete here.!

            //Toast.makeText(getApplicationContext(), "HttpURLConnection complete.", Toast.LENGTH_SHORT).show();

            String PATH = Environment.getExternalStorageDirectory() + "/download/";
            File file = new File(PATH); // PATH = /mnt/sdcard/download/
            if (!file.exists()) {
                file.mkdirs();
            }
            File outputFile = new File(file, ApkName.toString());           
            FileOutputStream fos = new FileOutputStream(outputFile);

            //      Toast.makeText(getApplicationContext(), "SD Card Path: " + outputFile.toString(), Toast.LENGTH_SHORT).show();

            InputStream is = c.getInputStream(); // Get from Server and Catch In Input Stream Object.

            byte[] buffer = new byte[1024];
            int len1 = 0;
            while ((len1 = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len1); // Write In FileOutputStream.
            }
            fos.close();
            is.close();//till here, it works fine - .apk is download to my sdcard in download file.
            // So plz Check in DDMS tab and Select your Emualtor.

            //Toast.makeText(getApplicationContext(), "Download Complete on SD Card.!", Toast.LENGTH_SHORT).show();
            //download the APK to sdcard then fire the Intent.
        } 
        catch (IOException e) 
        {
            Log.e("Error - ",e.toString());
          /*  Toast.makeText(getApplicationContext(), "Error! " +
                    e.toString(), Toast.LENGTH_LONG).show();*/
        }           
    }
}