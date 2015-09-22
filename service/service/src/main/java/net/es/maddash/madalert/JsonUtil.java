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
 *
 * @author carcassi
 */
public class JsonUtil {
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
