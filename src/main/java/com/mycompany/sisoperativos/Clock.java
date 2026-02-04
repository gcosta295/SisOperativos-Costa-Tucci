/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sisoperativos;

/**
 *
 * @author gabri
 */
public class Clock implements Runnable {
//Se agrega el "Runnable" a la clase del reloj para que sea capaz de poder correr en un hilo nuevo
    private int contadorCiclos = 0; //Siempre el reloj arranca en 0
    private int duracionCicloMs; // Tiempo en milisegundos que dura cada ciclo
    private boolean encendido = true;

    public Clock(int duracionInicial) {
        this.duracionCicloMs = duracionInicial;
    }

    // Método para cambiar la velocidad del reloj en tiempo real
    public void setDuracionCiclo(int nuevaDuracion) {
        this.duracionCicloMs = nuevaDuracion;
    }

    public int getContadorCiclos() {
        return contadorCiclos;
    }

    public void detener() {
        this.encendido = false;
    }

    @Override
    public void run() {
        try {
            while (encendido) {
                // Lógica del ciclo
                contadorCiclos++;
                System.out.println("[RELOJ] Ciclo actual: " + contadorCiclos);
                
                // Aquí podrías avisar a tu Planificador que un ciclo pasó
                
                // Pausa según la duración configurada
                Thread.sleep(duracionCicloMs);
            }
        } catch (InterruptedException e) {
            System.out.println("Reloj de simulación interrumpido.");
        }
    }
    }