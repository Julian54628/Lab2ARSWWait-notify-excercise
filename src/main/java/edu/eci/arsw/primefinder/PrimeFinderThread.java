package edu.eci.arsw.primefinder;

import java.util.LinkedList;
import java.util.List;

public class PrimeFinderThread extends Thread {

    int a, b;
    private List<Integer> primes;

    private boolean pause = false;
    private boolean run = true;

    public PrimeFinderThread(int a, int b) {
        super();
        this.primes = new LinkedList<>();
        this.a = a;
        this.b = b;
    }

    @Override
    public void run() {
        for (int i = a; i < b && run; i++) {
            synchronized (this) {
                while (pause && run) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        System.out.println("Hilo interrumpido");
                    }
                }
            }
            if (isPrime(i)) {
                primes.add(i);
                System.out.println(i);
            }
        }
    }

    public synchronized void pauseThread() {
        pause = true;
    }

    public synchronized void resumeThread() {
        pause = false;
        notify();
    }

    public synchronized void stopThread() {
        run = false;
        pause = false;
        notify();
    }

    boolean isPrime(int n) {
        boolean answ;
        if (n > 2) {
            answ = n % 2 != 0;
            for (int i = 3; answ && i * i <= n; i += 2) {
                answ = n % i != 0;
            }
        } else {
            answ = n == 2;
        }
        return answ;
    }

    public List<Integer> getPrimes() {
        return primes;
    }

    public int getPrimesCount() {
        return primes.size();
    }
}