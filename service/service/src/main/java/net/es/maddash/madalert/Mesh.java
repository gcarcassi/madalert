/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.es.maddash.madalert;

import java.util.List;
import javax.json.JsonObject;

/**
 * A mesh.
 * <p>
 * The actual data of the mash is represented internally by one of the JSON
 * specifications. This class provides JSON to Java bindings and other
 * view-like functionality.
 *
 * @author carcassi
 */
public class Mesh {
    
    // XXX: given that not all meshes have split cells, we may have to
    // rethink this
    public static enum CellHalf {INITIATED_BY_ROW(0), INITIATED_BY_COLUMN(1);

        private final int index;

        private CellHalf(int index) {
            this.index = index;
        }

        public int getIndex() {
            return index;
        }
        
    };
    
    private final JsonObject jObj;
    private final String meshLocation;

    private Mesh(JsonObject jObj, String meshLocation) {
        this.jObj = jObj;
        this.meshLocation = meshLocation;
    }
    
    public boolean isSplitCell() {
        return jObj.getJsonArray("grid").getJsonArray(0).getJsonArray(1).size() != 1;
    }
    
    public List<String> getSites() {
        return JsonUtil.toListString(jObj.getJsonArray("columnNames"));
    }

    public String getLocation() {
        return meshLocation;
    }
    
    public String getName() {
        return jObj.getString("name");
    }
    
    public int nSeverityLevels() {
        // TODO: get it from the mesh
        return 4;
    }
    
    public int statusFor(int row, int column, CellHalf half) {
        return jObj.getJsonArray("grid").getJsonArray(row).getJsonArray(column).getJsonObject(half.getIndex()).getInt("status");
    }
    
    public static Mesh from(JsonObject jObj) {
        return new Mesh(jObj, null);
    }
    
    public static Mesh from(JsonObject jObj, String meshLocation) {
        return new Mesh(jObj, meshLocation);
    }
}
