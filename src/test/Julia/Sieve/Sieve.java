//  Eratosthenes Sieve prime number benchmark in Java
package test.Julia.Sieve;

import java.awt.*;

public class Sieve extends java.applet.Applet implements Runnable {

  Thread sieveThread;
  String results1, results2;

  public void start() {
     sieveThread = new Thread(this);
     sieveThread.start();
     results1 = "Running Sieve benchmark.";
     results2 = "This will take about 10 seconds.";
    }

  public void stop() {
     sieveThread.stop();
     sieveThread = null;
  }

  public void run() {
     try {sieveThread.sleep(1000);}
     catch (InterruptedException e) { }
     sieveThread.setPriority(Thread.MAX_PRIORITY);
     runSieve();
     repaint();
  }

  void runSieve() {
     int SIZE = 8190;
     boolean flags[] = new boolean[SIZE+1];
     int i, prime, k, iter, count;
     int iterations = 0;
     double seconds = 0.0;
     int score = 0;
     long startTime, elapsedTime;

     startTime = System.currentTimeMillis();
     while (true) {
        count=0;
        for(i=0; i<=SIZE; i++) flags[i]=true;
        for (i=0; i<=SIZE; i++) {
           if(flags[i]) {
              prime=i+i+3;
              for(k=i+prime; k<=SIZE; k+=prime)
                 flags[k]=false;
              count++;
           }
        }
        iterations++;
        elapsedTime = System.currentTimeMillis() - startTime;
        if (elapsedTime >= 10000) break;
     }
     seconds = elapsedTime / 1000.0;
     score = (int) Math.round(iterations / seconds);
     results1 = iterations + " iterations in " + seconds + " seconds";
     if (count != 1899)
        results2 = "Error: count <> 1899";
     else
        results2 = "Sieve score = " + score;
  }

  public void paint(Graphics g) {
     g.setColor(Color.blue);
     g.drawString(results1, 20, 20);
     g.drawString(results2, 20, 35);
  }

}