package sample;

import com.sun.org.apache.regexp.internal.RE;
import com.sun.org.apache.xerces.internal.util.URI;

import org.omg.CORBA.Request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Created by valeriyasin on 11/26/16.
 */

final class RunnableRequester implements Runnable {

    private static String Url = "http://46.101.182.16/scooters_data/";

    final class Requester implements Callable<List<Gyro> > {
        public List<Gyro> call() {
            try {
                System.out.println("req\n");
                URL obj = new URL(Url);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                con.setRequestMethod("GET");
                int responseCode = con.getResponseCode();
                System.out.println(responseCode);
                if (responseCode == HttpURLConnection.HTTP_OK) { // success
                    BufferedReader in = new BufferedReader(new InputStreamReader(
                            con.getInputStream()));
                    String inputLine;
                    StringBuffer response = new StringBuffer();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    System.out.println(response);
                    in.close();
                }
                return new LinkedList<Gyro>();
            } catch(IOException ex) {
                ex.printStackTrace();
            }
            return null;
        }
    };

    public void run() {
        try {
            System.out.println("request\n");
            Callable req = new Requester();
            Main.getInstance().addResult(req.call());
        }  catch (Exception e) {
            Main.getInstance().addResult(new LinkedList<Gyro>()); // Assuming I want to know that an invocation failed
        }
    }

};


