package com.infinario.android.infinariosdk;

/**
 * Created by rolandrogansky on 07/09/15.
 */
public class InfinarioSegment {
    private String name = "";

    public InfinarioSegment setName(String name) {
        this.name = name;
        return this;
    }

    public String getName(){
        return name;
    }
}
