/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sisoperativos.logic;

import com.mycompany.sisoperativos.gui.Dashboard;

/**
 *
 * @author gabri
 */
public class Clock implements Runnable {
//Se agrega el "Runnable" a la clase del reloj para que sea capaz de poder correr en un hilo nuevo

    private int contadorCiclos = 0; //Siempre el reloj arranca en 0
    private volatile int duracionCicloMs; // Tiempo en milisegundos que dura cada ciclo
    private boolean encendido = true;
    private final Scheduling scheduler;
    private Dashboard gui;

    public Clock(int duracionInicial, Scheduling scheduler, Dashboard gui) {
        this.duracionCicloMs = duracionInicial;
        this.scheduler = scheduler;
        this.gui = gui;
    }

    // Método para cambiar la velocidad del reloj en tiempo real
    public void setDuracionCiclo(int nuevaDuracion) {
        this.duracionCicloMs = nuevaDuracion;
    }

    public int getContadorCiclos() {
        return contadorCiclos;
    }

    public int getDuracionCicloMs() {
        return duracionCicloMs;
    }

    public void detener() {
        this.encendido = false;
    }

    public void setContadorCiclos(int contadorCiclos) {
        this.contadorCiclos = contadorCiclos;
    }

    public void setDuracionCicloMs(int duracionCicloMs) {
        this.duracionCicloMs = duracionCicloMs;
    }

    public void setEncendido(boolean encendido) {
        this.encendido = encendido;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(duracionCicloMs);
                contadorCiclos++;

                // 1. Imprimir en consola para saber si el reloj sigue vivo
                System.out.println(">>> Reloj Tick: " + contadorCiclos);

                scheduler.runExecutionCycle();
                scheduler.checkAndPurgeDeadlines();
                if (contadorCiclos % 8 == 0){
                    Process process = new Process();
                    process.getPCB().setId(2);
                    process.periodicProcess(process.getPCB(), scheduler.getReadyQueue(), scheduler.getPolitic());
                }if (contadorCiclos % 12 == 0){
                    Process process = new Process();
                    process.getPCB().setId(3);
                    process.periodicProcess(process.getPCB(), scheduler.getReadyQueue(), scheduler.getPolitic());
                }if (contadorCiclos % 18 == 0){
                    Process process = new Process();
                    process.getPCB().setId(5);
                    process.periodicProcess(process.getPCB(), scheduler.getReadyQueue(), scheduler.getPolitic());
                }if (contadorCiclos % 40 == 0){
                    Process process = new Process();
                    process.getPCB().setId(7);
                    process.periodicProcess(process.getPCB(), scheduler.getReadyQueue(), scheduler.getPolitic());
                }if (contadorCiclos % 80 == 0){
                    Process process = new Process();
                    process.getPCB().setId(11);
                    process.periodicProcess(process.getPCB(), scheduler.getReadyQueue(), scheduler.getPolitic());
                }if (contadorCiclos % 200 == 0){
                    Process process = new Process();
                    process.getPCB().setId(13);
                    process.periodicProcess(process.getPCB(), scheduler.getReadyQueue(), scheduler.getPolitic());
                }
                //2. Comprobar si la ventana existe antes de actualizar
                if (gui != null) {
                    gui.updateStatus(contadorCiclos, scheduler.getCurrentProcess());
                    gui.updateReadyQueue(scheduler.getReadyQueue());
                    gui.updateBlockedQueue(scheduler.getBlockedQueue());
                    gui.updateFinishedQueue(scheduler.getFinishedQueue());
                } else {
                    System.out.println("!!! ERROR: El reloj no tiene conexión con la ventana (gui es null)");
                }

            } catch (Exception e) {
                // ¡AQUÍ ESTÁ LA MAGIA! 
                // Cambiamos InterruptedException por Exception general para atrapar CUALQUIER error.
                System.err.println("¡CRASH EN EL RELOJ! Motivo:");
                e.printStackTrace();
                break; // Detenemos el ciclo roto
            }
        }
    }
}
