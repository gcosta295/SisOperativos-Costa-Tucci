/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sisoperativos.logic;

/**
 *
 * @author gabri
 */
public class Clock implements Runnable {
//Se agrega el "Runnable" a la clase del reloj para que sea capaz de poder correr en un hilo nuevo

    private int contadorCiclos = 0; //Siempre el reloj arranca en 0
    private int duracionCicloMs; // Tiempo en milisegundos que dura cada ciclo
    private boolean encendido = true;
    private final Scheduling scheduler;

    public Clock(int duracionInicial, Scheduling scheduler) {
        this.duracionCicloMs = duracionInicial;
        this.scheduler = scheduler;
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
        try {
            while (encendido) {
                contadorCiclos++;
                System.out.println("[CLOCK] Cycle: " + contadorCiclos);

                // IMPORTANT: Tell the scheduler to process one cycle
                if (scheduler != null) {
                    scheduler.runExecutionCycleRR();
                }

                Thread.sleep(duracionCicloMs);
            }
        } catch (InterruptedException e) {
            System.out.println("Simulation clock interrupted.");
        }
    }
}
