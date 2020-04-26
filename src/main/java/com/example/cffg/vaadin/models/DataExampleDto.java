package com.example.cffg.vaadin.models;

import java.util.LinkedList;
import java.util.List;

public class DataExampleDto {
    private List<LinkedList<String>> dataExample = new LinkedList<>();

    public void addExample(LinkedList<String> example) {
        dataExample.add(example);
    }

}
