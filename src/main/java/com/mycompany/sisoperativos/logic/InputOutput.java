/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sisoperativos.logic;

/**
 *
 * @author astv06
 */
public class InputOutput {
    private String name;
    private int totalTime;
    private int responseTime;
    private int counter;
    private boolean inUse;
    private Queue IOQueue;
    private PCB pcbProcess;
    private InputOutput next;

    public String getName() {
        return name;
    }

    public int getTotalTime() {
        return totalTime;
    }

    public int getTesponseTime() {
        return responseTime;
    }

    public int getCounter() {
        return counter;
    }

    public boolean isInUse() {
        return inUse;
    }

    public PCB getPcbProcess() {
        return pcbProcess;
    }

    public InputOutput getNext() {
        return next;
    }

    public Queue getIOQueue() {
        return IOQueue;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTotalTime(int totalTime) {
        this.totalTime = totalTime;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public void setInUse(boolean inUse) {
        this.inUse = inUse;
    }

    public void setResponseTime(int responseTime) {
        this.responseTime = responseTime;
    }

    public void setPcbProcess(PCB pcbProcess) {
        this.pcbProcess = pcbProcess;
    }

    public void setNext(InputOutput next) {
        this.next = next;
    }

    public void setIOQueue(Queue IOQueue) {
        this.IOQueue = IOQueue;
    }

    public InputOutput(){
        this.counter=0;
        this.pcbProcess=null;
        this.inUse=false;
        this.name=null;
        this.totalTime=0;
        this.responseTime=0;
        this.IOQueue=new Queue();
    }
    
    public void initializationIO(int index){
        if(index==0){
            this.counter=2;
            this.name="Receptor";
            this.totalTime=5;
            this.responseTime=7;
        }
        if(index==1){
            this.counter=4;
            this.name="GNSS";
            this.totalTime=4;
            this.responseTime=8    ;
        }
        if(index==2){
            this.counter=6;
            this.name="Thermometer";
            this.totalTime=2;
            this.responseTime=8;
        }
        if(index==3){
            this.counter=8;
            this.name="SD Memory";
            this.totalTime=6;
            this.responseTime=14;
        }
        if(index==4){
            this.counter=1;
            this.name="Sensor";
            this.totalTime=8;
            this.responseTime=9;
        }
        if(index==5){
            this.counter=6;
            this.name="Sysadmin";
            this.totalTime=6;
            this.responseTime=12;
        }
        if(index==6){
            this.counter=6;
            this.name="Radiation Detector";
            this.totalTime=10;
            this.responseTime=16;
        }
    } 
    
    public void ioChecker(PCB pcb){
        if (this.pcbProcess==null){
            this.pcbProcess=pcb;
        }else{
            this.IOQueue.enqueueFIFO(pcb);
        }
    }
    
    
}
