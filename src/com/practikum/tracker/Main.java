package com.practikum.tracker;

import com.practikum.tracker.model.Task;

import java.util.Collection;

public class Main {
    public static void main(String[] args) {
        ;
    }

    public static String printHistory(Collection<Task> history) {
        String out = "";
        for (Task t : history){
            out += t.getId() + " ";
        }
        return out;
    }
}
