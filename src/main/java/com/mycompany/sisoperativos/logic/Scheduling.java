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
    private Queue oldQueue;
    private Queue newQueue;
    private String politic;
    
    public Scheduling(){
        this.newQueue= null;
        this.oldQueue= null;
        this.politic= null;
    }
    
    public void createScheduling(Queue oldQueue, String politic){
        this.oldQueue=oldQueue;
        this.politic=politic;
        this.newQueue=new Queue();
    }
    
    public Queue Organize(Queue oldQueue, Queue newQueue){
        if ("SRT".equals(this.politic)) { //Shortes Remaining Time
            for (int i = 0; i < oldQueue.getLen(); i++) {
                PCB pcb= oldQueue.getFirst();           
                }
            } else {
                
            }        for (int i = 0; i < oldQueue.getLen(); i++) {
            PCB pcb= oldQueue.getFirst();
            
        }
        return null;

    }
    public Queue getOldQueue() {
        return oldQueue;
    }

    public Queue getNewQueue() {
        return newQueue;
    }

    public String getPolitic() {
        return politic;
    }

    public void setOldQueue(Queue oldQueue) {
        this.oldQueue = oldQueue;
    }

    public void setNewQueue(Queue newQueue) {
        this.newQueue = newQueue;
    }

    public void setPolitic(String politic) {
        this.politic = politic;
    }
    
}
