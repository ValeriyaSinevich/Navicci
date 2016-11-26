package sample;

import com.sun.org.apache.regexp.internal.RE;

import org.omg.CORBA.Request;

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

    final class Requester implements Callable<List<Gyro> > {
        public List<Gyro> call() {
            //some logic of retrieving
            return new LinkedList<Gyro>();
        }
    };

    public void run() {
        try {
//            System.out.println("request\n");
            Callable req = new Requester();
            Main.getInstance().addResult(req.call());
        }  catch (Exception e) {
            Main.getInstance().addResult(new LinkedList<Gyro>()); // Assuming I want to know that an invocation failed
        }
    }

};


