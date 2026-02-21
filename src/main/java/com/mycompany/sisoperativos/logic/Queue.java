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

    //Elimina el primer elemento
    public PCB dequeue() {
        if (this.firstP == null) {
            return null;
        }
        PCB target = this.firstP;
        this.firstP = this.firstP.getNext(); // El segundo pasa a ser primero
        if (this.firstP != null) {
            this.firstP.setBefore(null);
        } else {
            this.lastP = null;
        }
        target.setNext(null);
        target.setBefore(null);
        this.len--;
        return target;
    }

    //Encola segun su deadline
    public void enqueueByDeadline(PCB newNode) {
        newNode.setNext(null);
        newNode.setBefore(null);
        if (firstP == null) {
            firstP = newNode;
            lastP = newNode;
            newNode.setNext(null);
            newNode.setBefore(null);
        }
        else if (newNode.getDeadlineR() < firstP.getDeadlineR()) {
            newNode.setNext(firstP);
            firstP.setBefore(newNode);
            firstP = newNode;
            newNode.setBefore(null);
        }
        else {
            PCB current = firstP;
            while (current.getNext() != null && current.getNext().getDeadlineR() <= newNode.getDeadlineR()) {
                current = current.getNext();
            }
            newNode.setNext(current.getNext());
            newNode.setBefore(current);
            if (current.getNext() != null) {
                current.getNext().setBefore(newNode);
            } else {
                lastP = newNode;
            }
            current.setNext(newNode);
        }
        this.len++;
    }

    //Encola segun politica FIFO
    public void enqueueFIFO(PCB newNode) {
        newNode.setNext(null);
        newNode.setBefore(null);
        if (this.firstP == null) {
            this.firstP = newNode;
            this.lastP = newNode;
        }
        else {
            this.lastP.setNext(newNode);
            newNode.setBefore(this.lastP);
            this.lastP = newNode;
        }
        this.len++;
    }

    //Encola por tiempo restante
    public void enqueueByRemainingTime(PCB newNode) { 
        newNode.setNext(null);
    newNode.setBefore(null);
        if (firstP == null) {
            firstP = newNode;
            lastP = newNode;
            newNode.setNext(null);
            newNode.setBefore(null);
        } 
        else if (newNode.getDurationR() < firstP.getDurationR()) {
            newNode.setNext(firstP);
            firstP.setBefore(newNode);
            firstP = newNode;
            newNode.setBefore(null);
        }
        else {
            PCB current = firstP;
            while (current.getNext() != null && current.getNext().getDurationR() <= newNode.getDurationR()) {
                current = current.getNext();
            }
            newNode.setNext(current.getNext());
            newNode.setBefore(current);
            if (current.getNext() != null) {
                current.getNext().setBefore(newNode);
            } else {
                lastP = newNode;
            }
            current.setNext(newNode);
        }
        this.len++;
    }

    //Encola segun su prioridad
    public void enqueueByPriority(PCB newNode) {
        newNode.setNext(null);
        newNode.setBefore(null);
        if (firstP == null) {
            firstP = newNode;
            lastP = newNode;
            newNode.setNext(null);
            newNode.setBefore(null);
        } else if (newNode.getPriority() >= firstP.getPriority()) {
            firstP.setBefore(newNode);
            newNode.setNext(firstP);
            newNode.setBefore(null); 
            firstP = newNode; 
        } else {
            PCB current = firstP;
            while (current.getNext() != null && current.getNext().getPriority() > newNode.getPriority()) {
                current = current.getNext();
            }
            newNode.setBefore(current);
            newNode.setNext(current.getNext());
            if (current.getNext() != null) {
                current.getNext().setBefore(newNode);
            } else {
                lastP = newNode;
            }
            current.setNext(newNode);
        }
        len++; 
    }

    // Encola dispositivos IO
    public void enqueueIO(PCB process) {
        process.setNextIO(null); 
        if (this.firstP == null) {
            this.firstP = process;
            this.lastP = process;
        } else {
            this.lastP.setNextIO(process); 
            this.lastP = process;
        }
    }

    // Desencolar dispositivos IO
    public PCB dequeueIO() {
        if (this.firstP == null) {
            return null;
        }
        PCB processToExtract = this.firstP;
        this.firstP = this.firstP.getNextIO(); 
        if (this.firstP == null) {
            this.lastP = null;
        }
        processToExtract.setNextIO(null);
        return processToExtract;
    }

    public void decrementAllDeadlines() {
        PCB aux = this.firstP;
        while (aux != null) {
            aux.setDeadlineR(aux.getDeadlineR() - 1);
            if (aux.getDeadlineR() == 0) {
            }
            aux = aux.getNext();
        }
    }

    public PCB peek() {
        if (this.firstP == null) {
            return null;
        }
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

    //Encuentra un PCB en una cola con su ID
    public PCB extractById(int id) {
        PCB aux = this.firstP;
        while (aux != null) {
            if (aux.getId() == id) {
                if (this.firstP == aux && this.lastP == aux) {
                    this.firstP = null;
                    this.lastP = null;
                } 
                else if (this.firstP == aux) {
                    this.firstP = aux.getNext();
                    if (this.firstP != null) this.firstP.setBefore(null);
                } 
                else if (this.lastP == aux) {
                    this.lastP = aux.getBefore();
                    if (this.lastP != null) this.lastP.setNext(null);
                } 
                else {
                    aux.getBefore().setNext(aux.getNext());
                    aux.getNext().setBefore(aux.getBefore());
                }
                aux.setNext(null);
                aux.setBefore(null);
                return aux;
            }
            aux = aux.getNext();
        }
        return null; 
    }
}
