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
public class Scheduling {

    private Queue readyQueue;
    private Queue blockedQueue;
    private Queue ioQueue;
    private Queue finishedQueue;
    private String politic;
    private int System_Quantum;
    private PCB currentProcess;
    private Dashboard gui;

    public Queue getIoQueue() {
        return ioQueue;
    }

    public void setIoQueue(Queue ioQueue) {
        this.ioQueue = ioQueue;
    }

    public void setSystem_Quantum(int System_Quantum) {
        this.System_Quantum = System_Quantum;

    }

    public int getSystem_Quantum() {
        return System_Quantum;
    }

    public Scheduling(Dashboard gui) {
        this.readyQueue = new Queue();
        this.ioQueue = new Queue();
        this.politic = null;
        this.blockedQueue = new Queue();
        this.finishedQueue = new Queue();
        this.currentProcess = null;
        this.System_Quantum = 10;
        this.gui = gui;
    }

    public Queue getFinishedQueue() {
        return this.finishedQueue;
    }

    public void createScheduling(Queue oldQueue, String politic) {
        this.readyQueue = oldQueue;
        this.politic = politic;
    }

    public void Organize() {
        if (this.readyQueue.getLen() == 0 && this.currentProcess == null) {
            return;
        }
        Queue newQueue = new Queue();
        PCB aux = readyQueue.dequeue();
        while (aux != null) {
            if ("EDF".equals(this.politic)) {
                newQueue.enqueueByDeadline(aux);
            } else if ("SRT".equals(this.politic)) {
                newQueue.enqueueByRemainingTime(aux);
            } else if ("Priority".equals(this.politic)) {
                newQueue.enqueueByPriority(aux);
            } else {
                // Para RR y FIFO usamos FIFO simple
                newQueue.enqueueFIFO(aux);
            }
            aux = readyQueue.dequeue();
        }
        // IMPORTANTE: Reemplazar la cola vieja con la nueva ya ordenada
        this.readyQueue = newQueue;
    }

    public void runExecutionCycle() {
        // 1. Cargar proceso si el CPU está vacío
        // 1. Reducir Deadline de los que están en la cola (El tiempo no perdona)
        PCB temp = readyQueue.peek();
        // Suponiendo que tienes un método para recorrer la cola o 
        // puedes restar el tiempo a todos los procesos en espera.
        updateQueueDeadlines();
        if (currentProcess == null) {
            currentProcess = readyQueue.dequeue();
            if (currentProcess != null) {
                currentProcess.setQuantum(0);
            }
        }

        if (currentProcess != null) {
            // Ejecución estándar
            currentProcess.setDurationR(currentProcess.getDurationR() - 1);
            currentProcess.setQuantum(currentProcess.getQuantum() + 1);
            currentProcess.setDeadlineR(currentProcess.getDeadlineR() - 1);

            gui.log("Executing ID: " + currentProcess.getId() + " [Rem: " + currentProcess.getDurationR() + "]" + " [Deadline: " + currentProcess.getDeadlineR() + "]");

            // 2. ¿Terminó?
            if (currentProcess.getDurationR() <= 0) {
                System.out.println("Proceso " + currentProcess.getId() + " finalizado con ÉXITO.");
                currentProcess.setState("Exit");
                finishedQueue.enqueueFIFO(currentProcess);
                currentProcess = null;
            } // 3. Revisamos si se le acabó el deadline estando en el CPU
            else if (currentProcess.getDeadlineR() <= 0) {
                System.out.println("¡ALERTA! Proceso " + currentProcess.getId() + " KILLED EN CPU (Deadline Vencido).");
                currentProcess.setState("Aborted");
                finishedQueue.enqueueFIFO(currentProcess);
                currentProcess = null; // Lo sacamos del CPU a la fuerza
            }

            // 3. LÓGICA DE PREEMPCIÓN (REALISMO ESTRICTO)
            PCB nextInQueue = readyQueue.getFirstP(); // Solo miramos el primero sin sacarlo

            if (nextInQueue != null) {
                boolean expulsar = false;

                if ("RR".equals(this.politic) && currentProcess.getQuantum() >= 4) {
                    System.out.println("!!! QUANTUM EXPIRED !!!");
                    expulsar = true;
                } else if ("SRT".equals(this.politic) && nextInQueue.getDurationR() < currentProcess.getDurationR()) {
                    System.out.println("!!! PREEMPTION (SRT) !!! ID " + nextInQueue.getId() + " es más corto.");
                    expulsar = true;
                } else if ("EDF".equals(this.politic) && nextInQueue.getDeadlineR() < currentProcess.getDeadlineR()) {
                    System.out.println("!!! PREEMPTION (EDF) !!! ID " + nextInQueue.getId() + " es más urgente.");
                    expulsar = true;
                } else if ("Priority".equals(this.politic) && nextInQueue.getPriority() > currentProcess.getPriority()) {
                    System.out.println("!!! PREEMPTION (PRIORITY) !!! ID " + nextInQueue.getId() + " tiene más prioridad.");
                    expulsar = true;
                } else if ("Priority".equals(this.politic)) {
                    // Si el que espera (nextInQueue) tiene un número de prioridad MÁS ALTO 
                    // que el que está corriendo, lo expulsamos.
                    if (nextInQueue.getPriority() > currentProcess.getPriority()) {
                        System.out.println("!!! PREEMPCIÓN POR PRIORIDAD !!!");
                        System.out.println("ID " + nextInQueue.getId() + " (Prio: " + nextInQueue.getPriority()
                                + ") expulsa a ID " + currentProcess.getId() + " (Prio: " + currentProcess.getPriority() + ")");

                        expulsar = true;
                    }
                }

                if (expulsar) {
                    currentProcess.setQuantum(0);
                    readyQueue.enqueueFIFO(currentProcess); // El actual vuelve a la cola
                    currentProcess = null; // El CPU queda libre para el "retador" en el ciclo siguiente
                }
            }
        }
    }

    public Queue getReadyQueue() {
        return this.readyQueue;
    }

    public String getPolitic() {
        return this.politic;
    }

    public void setReadyQueue(Queue readyQueue) {
        this.readyQueue = readyQueue;
    }

    public void setPolitic(String nueva) {
        this.politic = nueva;
        this.Organize();
        // No hace falta expulsar aquí manualmente, 
        // porque el runExecutionCycle lo hará solo en el siguiente ciclo 
        // al comparar los procesos con la nueva regla.
    }

    private void updateQueueDeadlines() {
        // Necesitas un método en tu clase Queue que permita 
        // restar 1 al DeadlineR de todos los PCB sin sacarlos de la fila.
        readyQueue.decrementAllDeadlines();
    }

    // Método para simular que el proceso actual pide E/S
    public void blockCurrentProcess() {
        if (currentProcess != null) {
            currentProcess.setState("Blocked");
            blockedQueue.enqueueFIFO(currentProcess); // Se va a la "sala de espera"
            currentProcess = null; // El CPU queda libre
            System.out.println("!!! PROCESO BLOQUEADO POR E/S !!!");
        }
    }

    public void unblockProcess(int id) {
        // Buscamos el proceso en la cola de bloqueados y lo pasamos a listos
        PCB p = blockedQueue.extractById(id);
        if (p != null) {
            p.setState("Ready");
            readyQueue.enqueueFIFO(p); // Vuelve a competir por el CPU
            System.out.println("ID " + id + " ha terminado su E/S y vuelve a Ready.");

            // Si la política es Preemptiva (EDF/SRT), deberíamos organizar
            this.Organize();
        }
    }

    public Queue getBlockedQueue() {
        return blockedQueue;
    }

    public PCB getCurrentProcess() {
        return currentProcess;
    }

    public Object getQueue() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
// Método para envejecer procesos y purgar los vencidos

    public void checkAndPurgeDeadlines() {
        // 1. Revisamos si hay procesos en la cola de listos
        if (readyQueue != null && readyQueue.peek() != null) {

            Queue sobrevivientes = new Queue(); // Cola temporal

            // Sacamos los procesos uno por uno (asumo que tienes un método dequeue() o sacar())
            // Si tu método para sacar se llama diferente, cámbialo aquí:
            PCB aux = readyQueue.dequeue();

            while (aux != null) {
                // Restamos 1 al tiempo límite por el ciclo que acaba de pasar
                aux.setDeadlineR(aux.getDeadlineR() - 1);

                // ¿Se le acabó el tiempo antes de terminar su ráfaga?
                if (aux.getDeadlineR() <= 0 && aux.getDurationR() > 0) {
                    System.out.println("¡ALERTA! Proceso " + aux.getId() + " TERMINADO ANÓMALAMENTE (Deadline Vencido).");
                    aux.setState("Aborted"); // Lo marcamos como abortado

                    // Lo mandamos al cementerio de finalizados
                    if (finishedQueue != null) {
                        finishedQueue.enqueueFIFO(aux);
                    }
                } else {
                    // Si sobrevive, lo metemos a la cola temporal
                    sobrevivientes.enqueueFIFO(aux);
                }

                // Sacamos el siguiente
                aux = readyQueue.dequeue();
            }

            // Reemplazamos la cola vieja con los que sobrevivieron
            this.readyQueue = sobrevivientes;
        }
    }
}
