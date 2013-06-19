package com.howtojboss.gridlogz.services.model;

import java.util.List;

/**
 *
 * @author Shane K Johnson
 */
public class NestedResult {
    
    private String key;
    
    private List<Result> values;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public List<Result> getValues() {
        return values;
    }

    public void setValues(List<Result> values) {
        this.values = values;
    }
}
