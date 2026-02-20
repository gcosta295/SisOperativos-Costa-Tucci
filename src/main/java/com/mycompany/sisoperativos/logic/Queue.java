/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sisoperativos.logic;

/**
 *
 * @author astv06
 */
public class Queue {

    private String name;
    private PCB firstP;
    private PCB lastP;
    private InputOutput firstIO;
    private InputOutput lastIO;
    private int len;
    private int capacity;

    public String getName() {
        return name;
    }

    public PCB getFirstP() {
        return firstP;
    }

    public PCB getLastP() {
        return lastP;
    }

    public InputOutput getFirstIO() {
        return firstIO;
    }

    public InputOutput getLastIO() {
        return lastIO;
    }

    public int getLen() {
        return len;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFirstP(PCB firstP) {
        this.firstP = firstP;
    }

    public void setLastP(PCB lastP) {
        this.lastP = lastP;
    }

    public void setFirstIO(InputOutput firstIO) {
        this.firstIO = firstIO;
    }

    public void setLastIO(InputOutput lastIO) {
        this.lastIO = lastIO;
    }

    public void setLen(int len) {
        this.len = len;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public Queue() {
        this.name = null;
        this.firstP = null;
        this.lastP = null;
        this.firstIO = null;
        this.lastIO = null;
        this.len = 0;
        this.capacity = 0;
    }

    public PCB dequeue() {
        if (this.firstP == null) {
            return null;
        }

        PCB target = this.firstP; // El que va a salir
        this.firstP = this.firstP.getNext(); // El segundo pasa a ser primero

        if (this.firstP != null) {
            this.firstP.setBefore(null); // El nuevo primero no tiene a nadie antes
        } else {
            this.lastP = null; // Si no hay nadie más, la cola está vacía
        }

        // LIMPIEZA CRÍTICA: Desconectar el proceso extraído de la cadena
        target.setNext(null);
        target.setBefore(null);

        this.len--;
        return target;
    }

    public void enqueueByDeadline(PCB newNode) { //Earliest Deadline First METHOD WHEN INSERTED
        // Case 1: The queue is empty

        if (firstP == null) {
            firstP = newNode;
            lastP = newNode;
            newNode.setNext(null);
            newNode.setBefore(null);
        } // Case 2: The new process has the shortest deadline (New Head)
        else if (newNode.getDeadlineR() < firstP.getDeadlineR()) {
            newNode.setNext(firstP);
            firstP.setBefore(newNode);
            firstP = newNode;
            newNode.setBefore(null);
        } // Case 3: Find the correct position in the middle or at the end
        else {
            PCB current = firstP;

            // Traverse the list until finding the correct spot
            while (current.getNext() != null && current.getNext().getDeadlineR() <= newNode.getDeadlineR()) {
                current = current.getNext();
            }

            // Insert newNode after 'current'
            newNode.setNext(current.getNext());
            newNode.setBefore(current);

            if (current.getNext() != null) {
                // Link the following node back to the new node
                current.getNext().setBefore(newNode);
            } else {
                // If inserted at the end, update the Tail pointer
                lastP = newNode;
            }
            current.setNext(newNode);
        }
        len = +1; // Increment queue size
    }

    public void enqueueFIFO(PCB newNode) {
        // 1. Safety check: ensure the new node doesn't carry old links
        newNode.setNext(null);
        newNode.setBefore(null);

        // 2. Case 1: The queue is empty
        if (this.firstP == null) {
            this.firstP = newNode;
            this.lastP = newNode;
        } // 3. Case 2: The queue already has elements
        else {
            // Link the current tail to the new node
            this.lastP.setNext(newNode);

            // Link the new node back to the current tail
            newNode.setBefore(this.lastP);

            // Move the tail pointer to the new node
            this.lastP = newNode;
        }

        // 4. Update queue size
        this.len++;
    }

    public void enqueueByRemainingTime(PCB newNode) { //Shortest time remaining (the same as EDF but with duration)
        // Case 1: The queue is empty

        if (firstP == null) {
            firstP = newNode;
            lastP = newNode;
            newNode.setNext(null);
            newNode.setBefore(null);
        } // Case 2: The new process has the shortest deadline (New Head)
        else if (newNode.getDurationR() < firstP.getDurationR()) {
            newNode.setNext(firstP);
            firstP.setBefore(newNode);
            firstP = newNode;
            newNode.setBefore(null);
        } // Case 3: Find the correct position in the middle or at the end
        else {
            PCB current = firstP;

            // Traverse the list until finding the correct spot
            while (current.getNext() != null && current.getNext().getDurationR() <= newNode.getDurationR()) {
                current = current.getNext();
            }

            // Insert newNode after 'current'
            newNode.setNext(current.getNext());
            newNode.setBefore(current);

            if (current.getNext() != null) {
                // Link the following node back to the new node
                current.getNext().setBefore(newNode);
            } else {
                // If inserted at the end, update the Tail pointer
                lastP = newNode;
            }
            current.setNext(newNode);
        }
        len = +1; // Increment queue size
    }

    public void enqueueByPriority(PCB newNode) { //Shortest time remaining (the same as EDF but with duration)
        // Case 1: The queue is empty

        if (firstP == null) {
            firstP = newNode;
            lastP = newNode;
            newNode.setNext(null);
            newNode.setBefore(null);
        } // Case 2: The new process has the shortest deadline (New Head)
        else if (newNode.getPriority() <= firstP.getPriority()) {
            firstP.setNext(newNode);
            newNode.setBefore(firstP);
        } // Case 3: Find the correct position in the middle or at the end
        else {
            PCB current = firstP;

            // Traverse the list until finding the correct spot
            while (current.getNext() != null && current.getNext().getPriority() < newNode.getPriority()) {
                current = current.getNext();
            }

            // Insert newNode after 'current'
            newNode.setNext(current.getNext());
            newNode.setBefore(current);

            if (current.getNext() != null) {
                // Link the following node back to the new node
                current.getNext().setBefore(newNode);
            } else {
                // If inserted at the end, update the Tail pointer
                lastP = newNode;
            }
            current.setNext(newNode);
        }
        len = +1; // Increment queue size
    }

    public void decrementAllDeadlines() {
        PCB aux = this.firstP;
        while (aux != null) {
            aux.setDeadlineR(aux.getDeadlineR() - 1);
            if (aux.getDeadlineR() == 0) {
                // Opcional: imprimir que un proceso en espera está por fallar
            }
            aux = aux.getNext();
        }
    }

    public PCB peek() {
        // Si la cola está vacía, devuelve null
        if (this.firstP == null) {
            return null;
        }
        // Retorna el objeto PCB que está en la cabeza de la lista
        return this.firstP;
    }

    public void enqueueIO(InputOutput io) {
        if (this.firstIO == null) {
            this.firstIO = io;
            this.lastIO = io;
        } else {
            this.lastIO.setNext(io);
            this.lastIO = io;
        }
    }
    
    public InputOutput serchByName(String ioName){
        InputOutput checker = this.firstIO;
        boolean flag = true;
        while (flag){
            if (checker.getName() == ioName){
                flag=false;
            }else{
                checker=checker.getNext();
            }
        }
        return checker;
    }

    public PCB extractById(int id) {
        if (this.firstP == null) {
            return null; // Cola vacía
        }
        PCB current = this.firstP;

        // 1. Buscar el proceso con el ID solicitado
        while (current != null) {
            if (current.getId() == id) {
                // ¡LO ENCONTRAMOS! Ahora a desconectarlo

                // Caso A: Es el primero de la cola
                if (current == this.firstP) {
                    this.firstP = current.getNext();
                    if (this.firstP != null) {
                        this.firstP.setBefore(null);
                    } else {
                        this.lastP = null; // La cola quedó vacía
                    }
                } // Caso B: Es el último de la cola
                else if (current == this.lastP) {
                    this.lastP = current.getBefore();
                    if (this.lastP != null) {
                        this.lastP.setNext(null);
                    }
                } // Caso C: Está en medio de dos procesos
                else {
                    current.getBefore().setNext(current.getNext());
                    current.getNext().setBefore(current.getBefore());
                }

                // Limpiar los punteros del nodo extraído por seguridad
                current.setNext(null);
                current.setBefore(null);
                this.len--; // Reducir el tamaño de la cola
                return current;
            }
            current = current.getNext();
        }

        return null; // No se encontró el proceso
    }
    
    
}
