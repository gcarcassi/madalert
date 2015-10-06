/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.es.maddash.madalert;

import java.util.AbstractList;
import java.util.List;
import javax.json.JsonArray;

/**
 * Minor utility methods to marshal/unmarshal JSON.
 *
 * @author carcassi
 */
class JsonUtil {
    
    /**
     * Adapts a JSON array to a Java List.
     * 
     * @param array a non-null JSON array
     * @return the content as a List
     */
    public static List<String> toListString(JsonArray array) {
        return new AbstractList<String>() {

            @Override
            public String get(int index) {
                return array.getString(index);
            }

            @Override
            public int size() {
                return array.size();
            }
        };
    }    
}
