/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sisoperativos.logic;

/**
 *
 * @author gabri
 */
public class Scheduling {

    private Queue queue;
    private String politic;
    private int System_Quantum;
    private PCB currentProcess;

    public void setSystem_Quantum(int System_Quantum) {
        this.System_Quantum = System_Quantum;
    }

    public int getSystem_Quantum() {
        return System_Quantum;
    }

    public Scheduling() {
        this.queue = new Queue();
        this.politic = null;
        this.currentProcess = null;
        this.System_Quantum = 10;
    }

    public void createScheduling(Queue oldQueue, String politic) {
        this.queue = oldQueue;
        this.politic = politic;
    }

    public void Organize() {
        if (this.queue.getLen() == 0 && this.currentProcess == null) {
            return;
        }
        Queue newQueue = new Queue();
        PCB aux = queue.dequeue();
        while (aux != null) {
            if ("EDF".equals(this.politic)) {
                newQueue.enqueueByDeadline(aux);
            } else if ("SRT".equals(this.politic)) {
                newQueue.enqueueByRemainingTime(aux);
            } else if ("Priority".equals(this.politic)) {
                newQueue.enqueueByPriority(aux);
            } else {
                // Para RR y FCFS usamos FIFO simple
                newQueue.enqueueFIFO(aux);
            }
            aux = queue.dequeue();
        }
        // IMPORTANTE: Reemplazar la cola vieja con la nueva ya ordenada
        this.queue = newQueue;
    }

    public void runExecutionCycle() {
        // 1. Cargar proceso si el CPU está vacío
        // 1. Reducir Deadline de los que están en la cola (El tiempo no perdona)
        PCB temp = queue.peek();
        // Suponiendo que tienes un método para recorrer la cola o 
        // puedes restar el tiempo a todos los procesos en espera.
        updateQueueDeadlines();
        if (currentProcess == null) {
            currentProcess = queue.dequeue();
            if (currentProcess != null) {
                currentProcess.setQuantum(0);
            }
        }

        if (currentProcess != null) {
            // Ejecución estándar
            currentProcess.setDurationR(currentProcess.getDurationR() - 1);
            currentProcess.setQuantum(currentProcess.getQuantum() + 1);
            currentProcess.setDeadlineR(currentProcess.getDeadlineR() - 1);

            System.out.println("Executing ID: " + currentProcess.getId() + " [Rem: " + currentProcess.getDurationR() + "]" + " [Deadline: " + currentProcess.getDeadlineR() + "]");

            // 2. ¿Terminó?
            if (currentProcess.getDurationR() <= 0) {
                System.out.println("Process " + currentProcess.getId() + " FINISHED.");
                currentProcess = null;
                return; // Salimos para que el siguiente entre en el próximo ciclo
            }
            if (currentProcess.getDeadlineR() < 0) {
                System.out.println("!!! ALERTA: ID " + currentProcess.getId() + " ha superado su DEADLINE (Misión Crítica en riesgo) !!!");
            }

            // 3. LÓGICA DE PREEMPCIÓN (REALISMO ESTRICTO)
            PCB nextInQueue = queue.getFirstP(); // Solo miramos el primero sin sacarlo

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
                    queue.enqueueFIFO(currentProcess); // El actual vuelve a la cola
                    currentProcess = null; // El CPU queda libre para el "retador" en el ciclo siguiente
                }
            }
        }
    }

    public Queue getQueue() {
        return this.queue;
    }

    public String getPolitic() {
        return this.politic;
    }

    public void setQueue(Queue queue) {
        this.queue = queue;
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
        queue.decrementAllDeadlines();
    }
}
