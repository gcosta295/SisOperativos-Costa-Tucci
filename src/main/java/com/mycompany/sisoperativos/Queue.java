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
    
    public Queue(){
        this.name=null;
        this.firstP=null;
        this.lastP=null;
        this.firstIO=null;
        this.lastIO=null;
        this.len=0;
        this.capacity=0;
    }
}
