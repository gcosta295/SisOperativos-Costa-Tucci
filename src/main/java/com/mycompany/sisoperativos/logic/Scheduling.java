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
    private int ramSize;
    private int successFinish;
    public final Object lock = new Object();

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
        this.ramSize = 256;
        this.successFinish = 0;

    }

    public Queue getFinishedQueue() {
        return this.finishedQueue;
    }

    public int getSuccessFinish() {
        return successFinish;
    }

    public void createScheduling(Queue oldQueue, String politic) {
        this.readyQueue = oldQueue;
        this.politic = politic;
    }

    public void Organize() {
        synchronized (this.lock) {
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
    }

    public void runExecutionCycle() {
        synchronized (this.lock) {
            // 1. Cargar proceso si el CPU está vacío
            if (currentProcess == null) {
                currentProcess = readyQueue.dequeue();
                if (currentProcess != null) {
                    currentProcess.setQuantum(0);
                }
            }

            // 2. Ejecutar un ciclo del proceso en el CPU
            if (currentProcess != null) {
                currentProcess.setDurationR(currentProcess.getDurationR() - 1);
                currentProcess.setQuantum(currentProcess.getQuantum() + 1);
                currentProcess.setDeadlineR(currentProcess.getDeadlineR() - 1);

                // ¿Le toca ir a E/S?
                // ¿Le toca ir a E/S?
                if (currentProcess.getInputOutput() != null) {
                    InputOutput io = this.ioQueue.ioSercher(currentProcess.getInputOutput());

                    // Si el momento de ir a E/S coincide con el ciclo actual
                    if (io != null && (currentProcess.getDurationHope() - currentProcess.getDurationR() == io.getCounter())) {
                        gui.log("!!! PROCESO " + currentProcess.getId() + " ENVIADO A E/S !!!");

                        // 1. Cambiamos su estado oficial a Bloqueado
                        currentProcess.setState("Blocked");

                        // 2. Lo mandamos al dispositivo de I/O (ioChecker ahora se encarga de meterlo a BlockedQueue)
                        io.ioChecker(currentProcess, this.blockedQueue);

                        // 3. Liberamos el CPU 
                        currentProcess = null;
                    }
                }
            }

            // 3. ACTUALIZAR DISPOSITIVOS DE I/O 
            InputOutput tempIO = this.ioQueue.getFirstIO();
            while (tempIO != null) {
                PCB pcbEnIO = tempIO.getPcbProcess();

                if (pcbEnIO != null) {
                    // 1. Le restamos 1 a TU timer del dispositivo
                    tempIO.setTimer(tempIO.getTimer() - 1);

                    // 2. ¿El timer llegó a 0? (Significa que ya cumplió su totalTime)
                    // 2. ¿El timer llegó a 0? (Significa que ya cumplió su totalTime)
                    if (tempIO.getTimer() <= 0) {

                        // Lo sacamos de la tabla general de bloqueados
                        this.blockedQueue.extractById(pcbEnIO.getId());

                        // ¡NUEVO! Limpiamos su puntero por si acaso se trae a otros procesos pegados
                        pcbEnIO.setNext(null);

                        // Limpiamos la variable porque ya no necesita I/O
                        pcbEnIO.setInputOutput(null);

                        // ¡NUEVO! Le cambiamos el estado para que la interfaz lo pueda dibujar
                        pcbEnIO.setState("Ready");

                        gui.log("Proceso " + pcbEnIO.getId() + " terminó su I/O en " + tempIO.getName());

                        // Lo devolvemos a la cola Ready (agregué FIFO como salvavidas por si acaso)
                        if ("FCFS".equals(politic) || "FIFO".equals(politic) || "RR".equals(politic)) {
                            this.readyQueue.enqueueFIFO(pcbEnIO);
                        } else if ("SRT".equals(politic)) {
                            this.readyQueue.enqueueByRemainingTime(pcbEnIO);
                        } else if ("Priority".equals(politic)) {
                            this.readyQueue.enqueueByPriority(pcbEnIO);
                        } else if ("EDF".equals(politic)) {
                            this.readyQueue.enqueueByDeadline(pcbEnIO);
                        } else {
                            // Salvavidas: Si la política viene nula o con otro nombre, que no se pierda el proceso
                            this.readyQueue.enqueueFIFO(pcbEnIO);
                        }

                        // 3. Si había alguien haciendo fila específica para ESTE dispositivo, lo metemos
                        // CAMBIA EL dequeue() normal por dequeueIO()
                        PCB nextInIO = tempIO.getIOQueue().dequeueIO();
                        tempIO.setPcbProcess(nextInIO);

                        // ¡Y le reiniciamos el timer a este nuevo proceso para que empiece su cuenta regresiva!
                        if (nextInIO != null) {
                            tempIO.setTimer(tempIO.getTotalTime());
                        }
                    }
                }
                tempIO = tempIO.getNext(); // Pasamos al siguiente dispositivo
            }

            // 4. Revisar finalización, muerte o expulsión 
            // (Solo entra aquí si el proceso NO se fue a E/S este ciclo)
            if (currentProcess != null) {
                // ¿Terminó con éxito?
                if (currentProcess.getDurationR() <= 0) {
                    gui.log("Proceso " + currentProcess.getId() + " finalizado con ÉXITO.");
                    currentProcess.setState("Exit");
                    if (finishedQueue != null) {
                        successFinish += 1;
                        finishedQueue.enqueueFIFO(currentProcess);
                    }
                    currentProcess = null; // Liberamos el CPU
                } // ¿Se le acabó el deadline estando en el CPU?
                else if (currentProcess.getDeadlineR() <= 0) {
                    gui.log("¡ALERTA! Proceso " + currentProcess.getId() + " TERMINADO ANÓMALAMENTE (Deadline Vencido en CPU).");
                    currentProcess.setState("Aborted");
                    if (finishedQueue != null) {
                        finishedQueue.enqueueFIFO(currentProcess);
                    }
                    currentProcess = null; // Liberamos el CPU
                } // LÓGICA DE PREEMPCIÓN
                else {
                    PCB nextInQueue = readyQueue.peek();

                    if (nextInQueue != null) {
                        boolean expulsar = false;

                        if ("RR".equals(this.politic) && currentProcess.getQuantum() >= this.System_Quantum) {
                            gui.log("!!! QUANTUM EXPIRED !!! ID " + currentProcess.getId() + " vuelve a la cola.");
                            expulsar = true;
                        } else if ("SRT".equals(this.politic) && nextInQueue.getDurationR() < currentProcess.getDurationR()) {
                            gui.log("!!! PREEMPTION (SRT) !!! ID " + nextInQueue.getId() + " es más corto.");
                            expulsar = true;
                        } else if ("EDF".equals(this.politic) && nextInQueue.getDeadlineR() < currentProcess.getDeadlineR()) {
                            gui.log("!!! PREEMPTION (EDF) !!! ID " + nextInQueue.getId() + " es más urgente.");
                            expulsar = true;
                        } else if ("Priority".equals(this.politic) && nextInQueue.getPriority() > currentProcess.getPriority()) {
                            gui.log("!!! PREEMPTION (PRIORITY) !!! ID " + nextInQueue.getId() + " tiene más prioridad.");
                            expulsar = true;
                        }

                        if (expulsar) {
                            currentProcess.setQuantum(0);

                            // ¡LIMPIEZA OBLIGATORIA! 
                            currentProcess.setNext(null);

                            readyQueue.enqueueFIFO(currentProcess);
                            currentProcess = null;
                        }
                    }
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

// Método para envejecer procesos y purgar los vencidos SIN romper la cola
    public void checkAndPurgeDeadlines(Queue queue) {
        if (queue == null || queue.peek() == null) {
            return;
        }

        // Sincronizamos para que la interfaz no lea la cola mientras la modificamos
        synchronized (this.lock) {
            PCB aux = queue.peek(); // Miramos el primero, NO lo sacamos con dequeue

            while (aux != null) {
                // Guardamos el siguiente antes de evaluar, por si tenemos que eliminar a 'aux'
                PCB siguiente = aux.getNext();

                // 1. Restamos 1 al deadline
                aux.setDeadlineR(aux.getDeadlineR() - 1);

                // 2. ¿Se le acabó el tiempo?
                if (aux.getDeadlineR() <= 0 && aux.getDurationR() > 0) {
                    gui.log("¡ALERTA! Proceso " + aux.getId() + " TERMINADO ANÓMALAMENTE (Deadline Vencido en espera).");
                    aux.setState("Aborted");

                    // Usamos tu método para extraer SOLO al que se le acabó el tiempo
                    queue.extractById(aux.getId());

                    // ¡EL SALVAVIDAS! Le borramos la memoria de quién estaba detrás de él
                    aux.setNext(null);

                    // Lo mandamos al cementerio
                    if (finishedQueue != null) {
                        finishedQueue.enqueueFIFO(aux);
                    }
                }

                // 3. Pasamos al siguiente
                aux = siguiente;
            }
        }

    }
}
