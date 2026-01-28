/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sisoperativos;

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

    public String getUser() {
        return user;
    }

    public String getState() {
        return state;
    }

    public int getPriority() {
        return priority;
    }

    public PCB getBefore() {
        return before;
    }

    public PCB getNext() {
        return next;
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

    public void setBefore(PCB before) {
        this.before = before;
    }

    public void setNext(PCB next) {
        this.next = next;
    }
        
    public PCB() {
        long time = System.currentTimeMillis();
        this.id= (int) (time % 10000000);   
        this.user="Satellite";
        this.priority= (int) ((time % 14)+1);
        this.before=null;
        this.next=null;
        Process process = new Process(this);
    }
}
