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
    
    public static enum CellHalf {INITIATED_BY_ROW(0), INITIATED_BY_COLUMN(1);

        private int index;

        private CellHalf(int index) {
            this.index = index;
        }

        public int getIndex() {
            return index;
        }
        
    };
    
    private final JsonObject jObj;

    private Mesh(JsonObject jObj) {
        this.jObj = jObj;
    }
    
    public List<String> getSites() {
        return JsonUtil.toListString(jObj.getJsonArray("columnNames"));
    }
    
    public int statusFor(int row, int column, CellHalf half) {
        return jObj.getJsonArray("grid").getJsonArray(row).getJsonArray(column).getJsonObject(half.getIndex()).getInt("status");
    }
    
    public static Mesh from(JsonObject jObj) {
        return new Mesh(jObj);
    }
}
