package com.meicompany.pi.grid;

import java.util.HashMap;

/**
 *
 * @author mpopescu
 */
public class Options {
    
    private HashMap<String,Object> list = new HashMap<>();

    /**
     * @return the list
     */
    public HashMap<String,Object> getList() {
        return list;
    }

    /**
     * @param list the list to set
     */
    public void setList(HashMap<String,Object> list) {
        this.list = list;
    }
}
