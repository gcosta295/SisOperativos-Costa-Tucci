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
    private Queue suspendedReadyQueue;
    private Queue suspendedBlockedQueue;
    private String politic;
    private int System_Quantum;
    private PCB currentProcess;
    private Dashboard gui;
    private int ramSize;
    private int successFinish;
    public final Object lock = new Object();
    private int totalTicks;
    private int busyTicks;
    private double sumaTiempoEspera = 0;

    public Queue getIoQueue() {
        return ioQueue;
    }

    public Queue getSuspendedReadyQueue() {
        return this.suspendedReadyQueue;
    }

    public Queue getSuspendedBlockedQueue() {
        return this.suspendedBlockedQueue;
    }

    public int getSystem_Quantum() {
        return System_Quantum;
    }

    public void setIoQueue(Queue ioQueue) {
        this.ioQueue = ioQueue;
    }

    public void setSystem_Quantum(int System_Quantum) {
        this.System_Quantum = System_Quantum;

    }

    public Scheduling(Dashboard gui) {
        this.suspendedReadyQueue = new Queue();
        this.suspendedBlockedQueue = new Queue();
        this.readyQueue = new Queue();
        this.ioQueue = new Queue();
        this.politic = null;
        this.blockedQueue = new Queue();
        this.finishedQueue = new Queue();
        this.currentProcess = null;
        this.System_Quantum = 5;
        this.gui = gui;
        this.ramSize = 256;
        this.successFinish = 0;
        this.busyTicks = 0;
        this.totalTicks = 0;
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
            //Chequeo de que existan procesos a ejecutar y ninguno ejecutandose
            if (this.readyQueue.getLen() == 0 && this.currentProcess == null) {
                return;
            }
            //Se crea una cola temporal y elemento a elemento se agrega
            //segun su politica de ordenamiento 
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
                    newQueue.enqueueFIFO(aux);
                }
                aux = readyQueue.dequeue();
            }
            //Se reasigna la cola de listos con la nueva cola organizada
            this.readyQueue = newQueue;
        }
    }

    public void runExecutionCycle() {
        synchronized (this.lock) {
            this.totalTicks++;
            //Chequeo del modo del CPU (true=kernel; false=user)
            boolean hizoSwap = manageRAM();
            if (hizoSwap) {
            } else {
                //De no tener un proceso el CPU cargar el primero en cola
                if (currentProcess == null) {
                    currentProcess = readyQueue.dequeue();
                    if (currentProcess != null) {
                        currentProcess.setQuantum(0);
                    }
                }
                //1 cliclo del programa en ejecucion
                if (currentProcess != null) {
                    this.busyTicks++;
                    currentProcess.setDurationR(currentProcess.getDurationR() - 1);
                    currentProcess.setQuantum(currentProcess.getQuantum() + 1);
                    currentProcess.setDeadlineR(currentProcess.getDeadlineR() - 1);
                    //Chequeo de si el programa tiene necesidad de un IO device
                    if (currentProcess.getInputOutput() != null) {
                        InputOutput io = this.ioQueue.ioSercher(currentProcess.getInputOutput());
                        if (io != null && (currentProcess.getDurationHope() - currentProcess.getDurationR() == io.getCounter())) {
                            gui.log("!!! PROCESO " + currentProcess.getId() + " ENVIADO A E/S !!!");
                            currentProcess.setState("Blocked");
                            io.ioChecker(currentProcess, this.blockedQueue);
                            currentProcess = null;
                        }
                    }
                }
                if (currentProcess != null) {
                    //comprobacion de procesos fantasmas o abortados
                    if (currentProcess.getDurationR() <= 0) {
                        currentProcess.setState("Exit");
                        int tiempoEnSistema = this.totalTicks - currentProcess.getArrivalTime();
                        int espera = tiempoEnSistema - currentProcess.getDurationHope();
                        sumaTiempoEspera += (espera < 0) ? 0 : espera;
                        if (finishedQueue != null) {
                            successFinish += 1;
                            finishedQueue.enqueueFIFO(currentProcess);
                        }
                        currentProcess = null;
                    } else if (currentProcess.getDeadlineR() <= 0) {
                        currentProcess.setState("Aborted");
                        if (finishedQueue != null) {
                            finishedQueue.enqueueFIFO(currentProcess);
                        }
                        currentProcess = null;
                    } else {
                        //CODIGO DE PREEMPTIONS
                        System.out.println("DEBUG MODO: " + this.politic + " | ID: " + currentProcess.getId() + " | Quantum: " + currentProcess.getQuantum() + "/" + this.System_Quantum);
                        boolean expulsar = false;
                        //Verificacion para Round Robin
                        if ("RR".equals(this.politic) && currentProcess.getQuantum() >= this.System_Quantum) {
                            gui.log("Round Robin: Proceso " + currentProcess.getId() + " agotó su Quantum.");
                            expulsar = true;    
                        } //Verificacion para SRT, EDF o Prioridad
                        else if ("SRT".equals(this.politic) || "EDF".equals(this.politic) || "Priority".equals(this.politic)) {
                            PCB candidato = this.readyQueue.getFirstP();
                            if (candidato != null) {
                                if ("SRT".equals(this.politic) && candidato.getDurationR() < currentProcess.getDurationR()) {
                                    gui.log("SRT Preemption: Proceso " + candidato.getId() + " tiene menor tiempo restante.");
                                    expulsar = true;
                                } else if ("EDF".equals(this.politic) && candidato.getDeadlineR() < currentProcess.getDeadlineR()) {
                                    gui.log("EDF Preemption: Proceso " + candidato.getId() + " tiene un deadline más crítico.");
                                    expulsar = true;
                                } else if ("Priority".equals(this.politic) && candidato.getPriority() > currentProcess.getPriority()) {
                                    gui.log("Priority Preemption: Proceso " + candidato.getId() + " tiene mayor prioridad.");
                                    expulsar = true;
                                }
                            }
                        }
                        // comprobacion de expulsion por cualquier politica
                        if (expulsar) {
                            currentProcess.setState("Ready");
                            this.readyQueue.enqueueFIFO(currentProcess);
                            if ("SRT".equals(this.politic) || "EDF".equals(this.politic) || "Priority".equals(this.politic)) {
                                this.Organize();
                            }
                            currentProcess = null;
                        }
                    }
                }
            }
            InputOutput tempIO = this.ioQueue.getFirstIO();
            while (tempIO != null) {
                tempIO = tempIO.getNext();
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

    //Tambien organiza la cola de listos
    public void setPolitic(String nueva) {
        this.politic = nueva;
        this.Organize();
    }

    private void updateQueueDeadlines() {
        readyQueue.decrementAllDeadlines();
    }

    public void blockCurrentProcess() {
        if (currentProcess != null) {
            currentProcess.setState("Blocked");
            blockedQueue.enqueueFIFO(currentProcess); 
            currentProcess = null; 
            System.out.println("!!! PROCESO BLOQUEADO POR E/S !!!");
        }
    }

    public void unblockProcess(int id) {
        PCB p = blockedQueue.extractById(id);
        if (p != null) {
            p.setState("Ready");
            readyQueue.enqueueFIFO(p); 
            System.out.println("ID " + id + " ha terminado su E/S y vuelve a Ready.");
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
        throw new UnsupportedOperationException("Not supported yet."); 
    }

// Método para envejecer procesos y purgar los vencidos SIN romper la cola
    public void checkAndPurgeDeadlines(Queue queue) {
        if (queue == null || queue.peek() == null) {
            return;
        }
        // Sincronizamos para que la interfaz no lea la cola mientras la modificamos
        synchronized (this.lock) {
            Queue temporalesVivos = new Queue();
            PCB aux = queue.dequeue();
            while (aux != null) {
                aux.setDeadlineR(aux.getDeadlineR() - 1);
                //Chequeo de que el proceso aun tiene tiempo
                if (aux.getDeadlineR() <= 0 && aux.getDurationR() > 0) {
                    gui.log("¡ALERTA! Proceso " + aux.getId() + " TERMINADO ANÓMALAMENTE (Deadline Vencido en espera).");
                    aux.setState("Aborted");
                    System.out.println("¡ALERTA! Proceso " + aux.getId() + " TERMINADO ANÓMALAMENTE (Deadline Vencido en espera).");
                    aux.setNext(null);
                    aux.setBefore(null);
                    if (finishedQueue != null) {
                        finishedQueue.enqueueFIFO(aux);
                    }
                } else {
                    aux.setNext(null);
                    aux.setBefore(null);
                    temporalesVivos.enqueueFIFO(aux);
                }
                aux = queue.dequeue();
            }
            PCB sobreviviente = temporalesVivos.dequeue();
            while (sobreviviente != null) {
                queue.enqueueFIFO(sobreviviente);
                sobreviviente = temporalesVivos.dequeue();
            }
        }
    }

    public boolean manageRAM() {
        synchronized (this.lock) {
            int ramUsada = calcularRamUsada();
            boolean huboIntervencion = false;
            if (ramUsada > this.ramSize) {
                gui.setCpuModo("Modo Supervisor");
                PCB victima = extraerVictimaMayorDeadline();
                if (victima != null && victima.getSize() > 0) {
                    huboIntervencion = true;
                    String estadoAntiguo = victima.getState();
                    victima.setNext(null);
                    victima.setBefore(null);
                    if (estadoAntiguo != null && estadoAntiguo.equalsIgnoreCase("Blocked")) {
                        victima.setState("SuspendedBlocked");
                        this.suspendedBlockedQueue.enqueueFIFO(victima);
                        gui.log("SWAP OUT: Proceso " + victima.getId() + " (Bloq) expulsado a Swap.");
                    } else {
                        victima.setState("SuspendedReady");
                        this.suspendedReadyQueue.enqueueFIFO(victima);
                        gui.log("SWAP OUT: Proceso " + victima.getId() + " (Listo) expulsado a Swap.");
                    }
                    ramUsada -= victima.getSize();
                }
            }
            if (!huboIntervencion && ramUsada < this.ramSize) {
                int espacioLibre = this.ramSize - ramUsada;
                PCB aDespertar = extraerSuspendidoMenorDeadline(espacioLibre);
                if (aDespertar != null && aDespertar.getSize() > 0) {
                    huboIntervencion = true;
                    gui.setCpuModo("Modo Supervisor");
                    String estadoAntiguo = aDespertar.getState();
                    aDespertar.setNext(null);
                    aDespertar.setBefore(null);
                    if (estadoAntiguo != null && estadoAntiguo.equalsIgnoreCase("SuspendedBlocked")) {
                        aDespertar.setState("Blocked");
                        this.blockedQueue.enqueueFIFO(aDespertar);
                        gui.log("SWAP IN: Proceso " + aDespertar.getId() + " regresó a Bloqueados.");
                    } else {
                        aDespertar.setState("Ready");
                        this.readyQueue.enqueueFIFO(aDespertar);
                        gui.log("SWAP IN: Proceso " + aDespertar.getId() + " regresó a Listos.");

                        if ("SRT".equals(this.politic) || "EDF".equals(this.politic) || "Priority".equals(this.politic)) {
                            this.Organize();
                        }
                    }
                }
            }
            if (huboIntervencion) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            return huboIntervencion;
        }
    }

    private int calcularRamUsada() {
        int total = 0;
        if (this.currentProcess != null) {
            total += this.currentProcess.getSize();
        }
        // Sumamos los de Ready
        PCB aux = this.readyQueue.peek();
        while (aux != null) {
            total += aux.getSize();
            aux = aux.getNext();
        }
        // Sumamos los de Blocked
        aux = this.blockedQueue.peek();
        while (aux != null) {
            total += aux.getSize();
            aux = aux.getNext();
        }
        return total;
    }

    private PCB extraerVictimaMayorDeadline() {
        PCB peorReady = buscarPeorDeadline(this.readyQueue);
        PCB peorBlocked = buscarPeorDeadline(this.blockedQueue);
        if (peorReady == null && peorBlocked == null) {
            return null;
        }
        PCB victimaElegida;
        boolean estabaEnReady = false;
        if (peorReady == null) {
            victimaElegida = peorBlocked;
        } else if (peorBlocked == null) {
            victimaElegida = peorReady;
            estabaEnReady = true;
        } else {
            if (peorBlocked.getDeadlineR() >= peorReady.getDeadlineR()) {
                victimaElegida = peorBlocked;
            } else {
                victimaElegida = peorReady;
                estabaEnReady = true;
            }
        }
        // Lo sacamos de la cola correcta usando la bandera.
        PCB extraido;
        if (estabaEnReady) {
            extraido = this.readyQueue.extractById(victimaElegida.getId());
        } else {
            extraido = this.blockedQueue.extractById(victimaElegida.getId());
        }
        if (extraido == null) {
            System.out.println("ERROR CRÍTICO: extractById devolvió null para el ID " + victimaElegida.getId());
        }
        return extraido;
    }

    private PCB buscarPeorDeadline(Queue q) {
        PCB aux = q.peek();
        if (aux == null) {
            return null;
        }
        PCB peor = aux;
        int maxDeadline = aux.getDeadlineR();
        // Empezamos a revisar desde el segundo proceso
        aux = aux.getNext();
        while (aux != null) {
            if (aux.getDeadlineR() > maxDeadline) {
                maxDeadline = aux.getDeadlineR();
                peor = aux;
            }
            aux = aux.getNext();
        }
        return peor;
    }

    private PCB extraerSuspendidoMenorDeadline(int maxTamanoPermitido) {
        PCB mejorSR = buscarMejorSuspendido(this.suspendedReadyQueue, maxTamanoPermitido);
        PCB mejorSB = buscarMejorSuspendido(this.suspendedBlockedQueue, maxTamanoPermitido);
        if (mejorSR == null && mejorSB == null) {
            return null;
        }
        PCB elegido;
        boolean estabaEnSR = false;
        if (mejorSR == null) {
            elegido = mejorSB;
        } else if (mejorSB == null) {
            elegido = mejorSR;
            estabaEnSR = true;
        } else {
            if (mejorSR.getDeadlineR() <= mejorSB.getDeadlineR()) {
                elegido = mejorSR;
                estabaEnSR = true;
            } else {
                elegido = mejorSB;
            }
        }
        if (estabaEnSR) {
            return this.suspendedReadyQueue.extractById(elegido.getId());
        } else {
            return this.suspendedBlockedQueue.extractById(elegido.getId());
        }
    }
    
    private PCB buscarMejorSuspendido(Queue q, int maxTamanoPermitido) {
        PCB aux = q.peek();
        PCB mejor = null;
        int minDeadline = Integer.MAX_VALUE;
        while (aux != null) {
            // Solo lo consideramos si cabe en la RAM (aux.getSize() <= maxTamanoPermitido)
            if (aux.getSize() <= maxTamanoPermitido && aux.getDeadlineR() < minDeadline) {
                minDeadline = aux.getDeadlineR();
                mejor = aux;
            }
            aux = aux.getNext();
        }
        return mejor;
    }

    public void ageAllQueues() {
        //Envejecemos a los que están en RAM y SWAP
        checkAndPurgeDeadlines(this.readyQueue);
        checkAndPurgeDeadlines(this.blockedQueue);
        checkAndPurgeDeadlines(this.suspendedReadyQueue);
        checkAndPurgeDeadlines(this.suspendedBlockedQueue);
    }

    public double getCpuUtilization() {
        if (totalTicks == 0) {
            return 0.0;
        }
        return ((double) this.busyTicks / this.totalTicks) * 100.0;
    }

    public double getThroughput() {
        if (totalTicks == 0) {
            return 0;
        }
        return (double) successFinish / totalTicks; // Fórmula: Procesos finalizados / Tiempo total transcurrido
    }

    public double getAvgWaitingTime() {
        // Usamos successFinish para no diluir el promedio con los procesos abortados
        if (this.successFinish == 0) {
            return 0.0;
        }
        return this.sumaTiempoEspera / this.successFinish;
    }
}
