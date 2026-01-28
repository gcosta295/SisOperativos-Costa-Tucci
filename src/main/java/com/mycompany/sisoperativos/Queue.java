/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sisoperativos;

/**
 *
 * @author astv06
 */
public class Queue {
    private String name;
    private PCB first;
    private PCB last;
    private int len;
    private int capacity;

    public String getName() {
        return name;
    }

    public PCB getFirst() {
        return first;
    }

    public PCB getLast() {
        return last;
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

    public void setFirst(PCB first) {
        this.first = first;
    }

    public void setLast(PCB last) {
        this.last = last;
    }

    public void setLen(int len) {
        this.len = len;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
    
    public Queue(){
        this.name=null;
        this.first=null;
        this.last=null;
        this.len=0;
        this.capacity=0;
    }
}
