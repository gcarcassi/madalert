/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.es.maddash.madalert;

import java.util.List;
import javax.json.JsonObject;

/**
 *
 * @author carcassi
 */
public class Mesh {
    
    private final JsonObject jObj;

    private Mesh(JsonObject jObj) {
        this.jObj = jObj;
    }
    
    public List<String> getSites() {
        return JsonUtil.toListString(jObj.getJsonArray("columnNames"));
    }
    
    public static Mesh from(JsonObject jObj) {
        return new Mesh(jObj);
    }
}
