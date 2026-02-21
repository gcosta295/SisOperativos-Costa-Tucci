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
newNode.setNext(null);
    newNode.setBefore(null);
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
        this.len++; // Increment queue size
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
        newNode.setNext(null);
    newNode.setBefore(null);
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
        this.len++; // Increment queue size
    }

    public void enqueueByPriority(PCB newNode) {
        newNode.setNext(null);
        newNode.setBefore(null);
        if (firstP == null) {
            firstP = newNode;
            lastP = newNode;
            newNode.setNext(null);
            newNode.setBefore(null);
        } else if (newNode.getPriority() >= firstP.getPriority()) {
            // El nuevo nodo va de primero
            firstP.setBefore(newNode);
            newNode.setNext(firstP);
            newNode.setBefore(null); // Buena práctica: asegurarnos de que no haya nada antes

            firstP = newNode; // ¡CORRECCIÓN CRÍTICA! Actualizamos la cabeza de la fila
        } else {
            PCB current = firstP;
            // Buscamos la posición correcta
            while (current.getNext() != null && current.getNext().getPriority() > newNode.getPriority()) {
                current = current.getNext();
            }

            // Insertamos el nuevo nodo
            newNode.setBefore(current);
            newNode.setNext(current.getNext());

            if (current.getNext() != null) {
                // Enlazamos el nodo siguiente de vuelta al nuevo nodo
                current.getNext().setBefore(newNode);
            } else {
                // Si lo insertamos al final, actualizamos el puntero lastP (Cola)
                lastP = newNode;
            }
            current.setNext(newNode);
        }

        len++; // ¡CORRECCIÓN! Ahora sí incrementa el tamaño en 1
    }

    // Método para encolar usando SOLO el puntero de I/O
    public void enqueueIO(PCB process) {
        process.setNextIO(null); // Limpiamos por si acaso

        if (this.firstP == null) {
            this.firstP = process;
            this.lastP = process;
        } else {
            this.lastP.setNextIO(process); // Enlazamos usando la segunda "cuerda"
            this.lastP = process;
        }
        // Incrementa tu contador de tamaño si tienes uno (ej. this.len++)
    }

    // Método para desencolar usando SOLO el puntero de I/O
    public PCB dequeueIO() {
        if (this.firstP == null) {
            return null;
        }

        PCB processToExtract = this.firstP;
        this.firstP = this.firstP.getNextIO(); // Avanzamos usando la segunda "cuerda"

        if (this.firstP == null) {
            this.lastP = null;
        }

        processToExtract.setNextIO(null); // Limpiamos el rastro
        // Disminuye tu contador de tamaño si tienes uno (ej. this.len--)

        return processToExtract;
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

    public InputOutput ioSercher(String name) {
        InputOutput temp = this.firstIO;
        while (temp != null) {
            if (temp.getName().equals(name)) {
                return temp;
            }
            temp = temp.getNext();
        }
        return null;
    }

public PCB extractById(int id) {
    PCB aux = this.firstP;
    while (aux != null) {
        if (aux.getId() == id) {
            // 1. Si es el ÚNICO elemento en la cola
            if (this.firstP == aux && this.lastP == aux) {
                this.firstP = null;
                this.lastP = null;
            } 
            // 2. Si es el PRIMER elemento (Head)
            else if (this.firstP == aux) {
                this.firstP = aux.getNext();
                if (this.firstP != null) this.firstP.setBefore(null);
            } 
            // 3. Si es el ÚLTIMO elemento (Tail)
            else if (this.lastP == aux) {
                this.lastP = aux.getBefore();
                if (this.lastP != null) this.lastP.setNext(null);
            } 
            // 4. Si está en el MEDIO
            else {
                aux.getBefore().setNext(aux.getNext());
                aux.getNext().setBefore(aux.getBefore());
            }
            
            // ¡VITAL! Desconectar al proceso extraído de la matrix
            aux.setNext(null);
            aux.setBefore(null);
            
            return aux; // Retornamos el proceso limpio
        }
        aux = aux.getNext();
    }
    return null; // No se encontró
}
}
