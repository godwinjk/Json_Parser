package com.godwin.jsonparser.ui.tab;

import java.util.LinkedList;
import java.util.List;

public class TabIdCollection {
    private static final List<Integer> LIST= new LinkedList<Integer>();
    private static final int MAX_COUNT=11;

    public int getId(){
        for (int i =0;i<MAX_COUNT;i++){
            if(!LIST.contains(i)) return i;
        }
        return MAX_COUNT;
    }
    public void removeId(){

    }
}
