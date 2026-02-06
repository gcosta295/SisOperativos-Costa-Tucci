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
        // 1. Check if the queue is empty using the class attribute
        if (this.firstP == null) {
            return null;
        }

        // 2. Reference the node to be removed (the current head)
        PCB target = this.firstP;

        // 3. Move the class head pointer to the next node
        this.firstP = this.firstP.getNext();

        // 4. Update the new head's 'before' pointer
        if (this.firstP != null) {
            // If there is a next node, it becomes the new head
            this.firstP.setBefore(null);
        } else {
            // If the queue is now empty, we must also reset the lastP (Tail)
            this.lastP = null;
        }

        // 5. Isolate the target node for safety (Clear its links)
        target.setNext(null);
        target.setBefore(null);

        // 6. Update queue length
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

    public void executeRoundRobin(Queue readyQueue, int systemQuantum) {
        // 1. Get the first process in line
        PCB currentP = readyQueue.dequeue();

        if (currentP != null) {
            // Set the process state to Running
            currentP.setState("Running");

            // We calculate how much time to run: the minimum between the system quantum and what the process needs
            int timeToExecute = Math.min(systemQuantum, currentP.getDurationR());

            System.out.println("Executing Process ID: " + currentP.getId() + " for " + timeToExecute + " cycles.");

            // 2. Subtract the execution time from the remaining duration
            currentP.setDurationR(currentP.getDurationR() - timeToExecute);

            // 3. Check if the process finished or needs to go back to the queue
            if (currentP.getDurationR() > 0) {
                // Quantum expired but process is not finished
                currentP.setState("Ready");
                System.out.println("Quantum expired. Moving Process " + currentP.getId() + " to the back of the queue.");
                readyQueue.enqueueFIFO(currentP); // Goes to the back of the line
            } else {
                // Process finished its work
                currentP.setState("Terminated");
                System.out.println("Process " + currentP.getId() + " has finished execution.");
            }
        }
    }
}
