package net.glidr.urdht_test;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
    private String str = "Android UrDHT Main Activity";
    DataBase db = (DataBase)getApplication();


    private boolean netflag = true;
    boolean started = false;
    boolean flag1 = false;
    boolean flag2 = false;
    boolean flag3 = false;

    private String build = "00001";

    TextView txtLocIP;
    TextView txtPubIP;
    TextView txtLocalBuild;
    TextView txtRemoteBuild;

    Button btnStart;
    Button btnStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtLocIP = (TextView)findViewById(R.id.txtLocIP);
        txtPubIP = (TextView)findViewById(R.id.txtPubIP);
        txtLocalBuild = (TextView)findViewById(R.id.txtLocalBuild);

        txtLocalBuild.setText(build);

        txtRemoteBuild = (TextView)findViewById(R.id.txtRemoteBuild);

        btnStart = (Button)findViewById(R.id.btnStart);
        btnStop = (Button)findViewById(R.id.btnStop);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(str, "Start Pressed");

                new LongTaskLoc(txtLocIP, txtPubIP, db.gmi).execute();
                new LongTaskPub(txtLocIP, txtPubIP, db.gmi).execute();
                new LongHash(db.hash, db.gmi).execute();

                if(started) return;
                started = true;
                Intent i = new Intent(MainActivity.this, UrDHTService.class);

                MainActivity.this.startService(i);

                if(!netflag) {
                    txtLocIP.setText("No Network");
                    txtPubIP.setText("Disconnected - STOPPED");
                    txtRemoteBuild.setText("-NA-");
                    MainActivity.this.stopService(i);
                    flag1 = false;
                    flag2 = false;
                    flag3 = false;
                }
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(str, "Stop Pressed");
                txtLocIP.setText("Disconnected");
                txtPubIP.setText("Disconnected");
                txtRemoteBuild.setText("-NA-");
                Intent i = new Intent(MainActivity.this, UrDHTService.class);
                MainActivity.this.stopService(i);
                flag1 = false;
                flag2 = false;
                flag3 = false;
                started = false;
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class LongTaskLoc extends AsyncTask<Void, Void, Void> {
        TextView loc;
        GetMyIpAddress ip;
        private LongTaskLoc(TextView loc, TextView pub, GetMyIpAddress g) {
            this.loc = loc;
            this.ip = g;
        }

        @Override
        protected Void doInBackground(Void... urls){
            if( this.ip.isOnline() ) {
                this.ip.updateIpAddress();
            } else {
                Log.d("AsyncTask", "No Network!");
                this.ip.inetAddr = "No Network";
            }
            flag1 = true;
            return null;
        }

        @Override
        protected void onPostExecute(Void s) {
            this.loc.setText(this.ip.inetAddr);
        }
    }

    private class LongTaskPub extends AsyncTask<Void, Void, Void> {
        TextView pub;
        GetMyIpAddress ip;
        private LongTaskPub(TextView loc, TextView pub, GetMyIpAddress g) {
            this.pub = pub;
            this.ip = g;
        }

        @Override
        protected Void doInBackground(Void... urls){
            if( this.ip.isOnline() ) {
                this.ip.getGateway();
            } else {
                Log.d("AsyncTask", "No Network!");
                this.ip.publicIP = "No Network";
            }
            flag2 = true;
            return null;
        }

        @Override
        protected void onPostExecute(Void s) {
            this.pub.setText(this.ip.publicIP);
        }
    }

    private class LongHash extends AsyncTask<Void, Void, Void> {
        HashFunction hash;
        GetMyIpAddress ip;
        private LongHash(HashFunction h, GetMyIpAddress g) {
            this.hash = h;
            this.ip = g;
        }

        @Override
        protected Void doInBackground(Void... urls){
            if( this.ip.isOnline() ) {
                this.hash.genHash("http://"+this.ip.publicIP +":"+this.ip.bindPort);
                //this.hash.genHash("http://45.79.205.125:8002/");
            } else {
                Log.d("AsyncTask", "No Network!");
            }
            flag3 = true;
            return null;
        }

        @Override
        protected void onPostExecute(Void s) {
        }
    }
}
