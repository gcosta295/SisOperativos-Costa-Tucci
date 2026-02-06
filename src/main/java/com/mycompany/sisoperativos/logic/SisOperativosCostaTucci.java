/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.mycompany.sisoperativos.logic;

/**
 *
 * @author gabri & astv06
 */
public class SisOperativosCostaTucci {


    public static void main(String[] args) {
        // 1. Inicialización del motor de simulación
        Scheduling scheduler = new Scheduling();
        
        // Obtenemos la referencia directa de la cola del scheduler
        Queue readyQueue = scheduler.getQueue();

        System.out.println("=== SISTEMA OPERATIVO: SIMULADOR DE PLANIFICACIÓN ===");
        System.out.println("Configurando procesos de prueba...\n");

        // 2. CREACIÓN DE PROCESOS (Usando tus métodos SET)
        
        // Proceso 101: El "Lento" (Largo, poca prioridad, deadline lejano)
        PCB p101 = new PCB();
        p101.setId(101);
        p101.setDurationR(12);  // 12 ciclos
        p101.setPriority(1);    // Prioridad baja
        p101.setDeadlineR(100); // Muy poco urgente
        readyQueue.enqueueFIFO(p101);

        // Proceso 202: El "Urgente" (Deadline muy cercano)
        PCB p202 = new PCB();
        p202.setId(202);
        p202.setDurationR(5);
        p202.setPriority(3);
        p202.setDeadlineR(10);  // ¡Debe terminar pronto!
        readyQueue.enqueueFIFO(p202);

        // Proceso 303: El "VIP" (Alta prioridad)
        PCB p303 = new PCB();
        p303.setId(303);
        p303.setDurationR(4);
        p303.setPriority(10);   // Prioridad máxima
        p303.setDeadlineR(50);
        readyQueue.enqueueFIFO(p303);

        // 3. INICIO DE LA SIMULACIÓN
        // Empezamos con FCFS para que el Proceso 101 tome el CPU primero
        scheduler.setPolitic("FCFS");
        
        Clock simClock = new Clock(1000, scheduler); // 1 segundo por ciclo para poder leer
        Thread hiloReloj = new Thread(simClock);
        hiloReloj.start();

        try {
            // FCFS: El 101 debería correr sin interrupciones
            Thread.sleep(4500); 

            // --- PRUEBA SRT ---
            System.out.println("\n>>> EVENTO: Cambiando a SRT (Shortest Remaining Time) <<<");
            scheduler.setPolitic("SRT");
            scheduler.Organize(); 
            // El 202 o 303 deberían expulsar al 101 porque tienen menos tiempo restante
            Thread.sleep(5000);

            // --- PRUEBA PRIORIDAD ---
            System.out.println("\n>>> EVENTO: Cambiando a PRIORIDAD ESTÁTICA PREEMPTIVA <<<");
            scheduler.setPolitic("Priority");
            scheduler.Organize();
            // El 303 (Prio 10) debería tomar el control inmediatamente
            Thread.sleep(5000);

            // --- PRUEBA EDF ---
            System.out.println("\n>>> EVENTO: Cambiando a EDF (Earliest Deadline First) <<<");
            scheduler.setPolitic("EDF");
            scheduler.Organize();
            // El 202 debería saltar al frente por su Deadline de 10
            Thread.sleep(5000);

            // --- PRUEBA ROUND ROBIN ---
            System.out.println("\n>>> EVENTO: Cambiando a ROUND ROBIN (Quantum: 4) <<<");
            scheduler.setPolitic("RR");
            scheduler.Organize();
            // Aquí verás las expulsiones por Quantum cada 4 ciclos

        } catch (InterruptedException e) {
            System.err.println("Error en el hilo principal: " + e.getMessage());
        }
    }
}