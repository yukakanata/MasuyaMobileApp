package com.yusuffahrudin.masuyamobileapp.util;

/**
 * Created by yusuf fahrudin on 16-02-2017.
 */

public class Server {
    private static String path;
    private String URL;
    private String URL_IMAGE;

    public Server(String path) {
        Server.path = path;
    }

    public String URL (){
        if  (path.equalsIgnoreCase("JKT")){
            URL = "http://103.227.145.42/cobaApp2/masuyamobile"+path+"/";
        } else if (path.equalsIgnoreCase("TES")){
            URL = "http://masuyasby.ddns.net:81/cobaApp2/masuyates/";
        } else {
            URL = "http://masuyasby.ddns.net:81/cobaApp2/masuyamobile"+path+"/";
        }
        //final String URL = "http://103.227.145.42/cobaApp2/masuyamobile"+path+"/";
        //final String URL = "http://masuyasby.ddns.net:81/cobaApp2/masuyates/";
        return URL;
    }

    public String URL_IMAGE (){
        if  (path.equalsIgnoreCase("JKT")){
            URL_IMAGE = "http://103.227.145.42/cobaApp2/Images/katalog/";
        } else if (path.equalsIgnoreCase("TES")){
            URL = "http://masuyasby.ddns.net:81/cobaApp2/Images/katalog/";
        } else {
            URL_IMAGE = "http://masuyasby.ddns.net:81/cobaApp2/Images/katalog/";
        }
        //final String URL_IMAGE = "http://103.227.145.42/cobaApp2/Images/katalog/";
        return URL_IMAGE;
    }

    public String URL_APK (){
        if  (path.equalsIgnoreCase("JKT")){
            URL = "http://masuyasby.ddns.net:81/cobaApp2/apk/masuya.apk";
        } else {
            URL = "http://masuyasby.ddns.net:81/cobaApp2/apk/masuya.apk";
        }
        //final String URL = "http://masuyasby.ddns.net:81/cobaApp2/apk/masuya.apk";
        return URL;
    }
}