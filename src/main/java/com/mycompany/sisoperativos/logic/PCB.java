/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sisoperativos.logic;

/**
 *
 * @author astv06
 */
public class PCB {
    private int id;
    private String user;
    private String state;
    private int priority;
    private PCB before;
    private PCB next;
    private int quantum;
    private int durationHope;
    private int deadlineR;
    private int durationR;
    private String inputOutput;
    private int size;
    private PCB nextIO;

    public int getId() {
        return id;
    }

    public String getUser() {
        return user;
    }

    public String getState() {
        return state;
    }

    public int getPriority() {
        return priority;
    }

    public int getQuantum() {
        return quantum;
    }

    public int getDeadlineR() {
        return deadlineR;
    }

    public int getDurationR() {
        return durationR;
    }

    public PCB getBefore() {
        return before;
    }

    public PCB getNext() {
        return next;
    }

    public int getSize() {
        return size;
    }

    public String getInputOutput() {
        return inputOutput;
    }

    public int getDurationHope() {
        return durationHope;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setQuantum(int quantum) {
        this.quantum = quantum;
    }

    public void setDeadlineR(int deadlineR) {
        this.deadlineR = deadlineR;
    }

    public void setDurationR(int durationR) {
        this.durationR = durationR;
    }

    public void setBefore(PCB before) {
        this.before = before;
    }

    public void setNext(PCB next) {
        this.next = next;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setInputOutput(String inputOutput) {
        this.inputOutput = inputOutput;
    }

    public void setDurationHope(int durationHope) {
        this.durationHope = durationHope;
    }
    
    public PCB getNextIO() {
        return nextIO;
    }

    public void setNextIO(PCB nextIO) {
        this.nextIO = nextIO;
    }
         
    public PCB() {
        long time = System.nanoTime();
        this.id= (int) ((time % 10000000)+1);   
        this.user="Satellite";
        this.priority=0;
        this.before=null;
        this.next=null;
        this.nextIO=null;
        this.quantum=0;
        this.durationHope=0;
        this.deadlineR=0;
        this.durationR=0;
        this.size=0;
        this.inputOutput=null;
    }
    
    
}
