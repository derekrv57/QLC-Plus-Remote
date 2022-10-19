package com.derek.qlcplusremote;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;


public class ConnectActivity extends AppCompatActivity {
    private Spinner cmbIp;

    public static final String ip = "ip";
    String route = "";
    final List<String> ips = new ArrayList<String>();
    final List<String> hosts = new ArrayList<String>();
    boolean qlcFound = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        cmbIp = (Spinner) findViewById(R.id.cmbIp);
        checkIpStatus();

    }
    public void connect(View v){
        int index = cmbIp.getSelectedItemPosition();
        if (index == 0){
            showIpDialog(v);
        }
        else{
            String cntIp = ips.get(index);
            showQlcView(v, cntIp);
        }
    }
    void showQlcView(View v, String cntIp){
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra(ip , cntIp);
        startActivity(i);
    }
    void showIpDialog(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Host address");
            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_NUMBER);
            input.requestFocus();
            builder.setView(input);
            builder.setPositiveButton("Connect", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String val = input.getText().toString();
                    if (val.length()==0){
                        showIpDialog(v);
                    }
                    else{
                        showQlcView(v, val);
                    }
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();
    }
    void checkIpStatus() {
        try {
            ips.clear();
        hosts.clear();
        addIp(null, "Other");
        String baseIp = os.getIp();
        boolean flag = false;
        for (int i = baseIp.length(); i > 0; i--) {
            if (baseIp.substring(i - 1, i).equals(".")) {
                flag = true;
            }
            if (flag) {
                route = baseIp.substring(i - 1, i) + route;
            }
        }
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 255; i++) {
                    showIpSatus(route + String.valueOf(i));
                }
            }
        };
        Thread hilo = new Thread(runnable);
        hilo.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    void showIpSatus(String ip) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    if (!ip.equals("")) {
                        if (new remote().isAvatible(ip)) {
                            qlcFound = true;
                            String hostName = ip;
                            try {
                                InetAddress host = InetAddress.getByName(ip);
                                hostName = host.getHostName();
                            } catch (UnknownHostException e) {
                                e.printStackTrace();
                            }
                            addIp(ip, hostName);
                        }
                    }
                } catch (Exception e) {
                    //System.err.println(e);
                }
            }
        };
        Thread hilo = new Thread(runnable);
        hilo.start();
    }

    void addIp(String ip, String host){
        ips.add(ip);
        hosts.add(host);
        ArrayAdapter<String> adp1 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, hosts);
        adp1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cmbIp.setAdapter(adp1);
    }
}