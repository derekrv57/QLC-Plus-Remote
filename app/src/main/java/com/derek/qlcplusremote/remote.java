package com.derek.qlcplusremote;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class remote {
    public boolean isAvatible(String host){
        URL url;
        try {
            url = new URL("http://"+host+":9999");
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println(inputLine);
                if (inputLine.contains("QLC")) {
                    return true;
                }
            }
            in.close();
        } catch (MalformedURLException ex) {
            return false;
        } catch (IOException ex) {
            return false;
        }
        return false;
    }
}
