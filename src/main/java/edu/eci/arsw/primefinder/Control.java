/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.primefinder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 */
public class Control extends Thread {

    private final static int NTHREADS = 3;
    private final static int MAXVALUE = 30000000;
    private final static int TMILISECONDS = 5000;

    private final int NDATA = MAXVALUE / NTHREADS;

    private PrimeFinderThread pft[];

    private Control() {
        super();
        this.pft = new PrimeFinderThread[NTHREADS];

        int i;
        for (i = 0; i < NTHREADS - 1; i++) {
            PrimeFinderThread elem = new PrimeFinderThread(i * NDATA, (i + 1) * NDATA);
            pft[i] = elem;
        }
        pft[i] = new PrimeFinderThread(i * NDATA, MAXVALUE + 1);
    }

    public static Control newControl() {
        return new Control();
    }

    @Override
    public void run() {
        for (int i = 0; i < NTHREADS; i++) {
            pft[i].start();
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int pauseCount = 0;
        while (anyThreadAlive()) {
            try {
                Thread.sleep(TMILISECONDS);
                if (!anyThreadAlive()) {
                    break;
                }
                pauseAllThreads();
                showPrimeCount();
                waitForEnter(br);
                resumeAllThreads();

            } catch (InterruptedException e) {
                System.out.println("Se interrumpió el hilo de control");
                break;
            }
        }

        waitForThreadsCompletion();
        showFinalResults();

        try {
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void pauseAllThreads() {
        for (int i = 0; i < NTHREADS; i++) {
            pft[i].pauseThread();
        }
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void resumeAllThreads() {
        for (int i = 0; i < NTHREADS; i++) {
            pft[i].resumeThread();
        }
    }

    private void showPrimeCount() {
        int total = 0;
        for (int i = 0; i < NTHREADS; i++) {
            int count = pft[i].getPrimesCount();
            total += count;
            System.out.println("Hilo " + (i + 1) + ": " + count + " primos");
        }
        System.out.println("Los números primos encontrados fueron  " + total);
    }

    private void waitForEnter(BufferedReader br) {
        System.out.print("\n Oprime enter para continuar");
        try {
            br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean anyThreadAlive() {
        for (int i = 0; i < NTHREADS; i++) {
            if (pft[i].isAlive()) {
                return true;
            }
        }
        return false;
    }

    private void waitForThreadsCompletion() {
        for (int i = 0; i < NTHREADS; i++) {
            try {
                pft[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void showFinalResults() {
        int total = 0;
        System.out.println("\nEl resultado es");
        for (int i = 0; i < NTHREADS; i++) {
            int count = pft[i].getPrimesCount();
            total += count;
            System.out.println("Hilo " + (i + 1) + ": " + count + " primos");
        }
        System.out.println("El total de números primos encontrados fue " + total);
    }
}