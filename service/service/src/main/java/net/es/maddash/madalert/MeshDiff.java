/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.es.maddash.madalert;

import java.math.BigDecimal;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

/**
 * A mesh diff.
 *
 * @author carcassi
 */
public class MeshDiff {
    
    static Mesh calculateDiff(Mesh mesh1, Mesh mesh2) {
        if (!mesh1.getSites().equals(mesh2.getSites())) {
            throw new RuntimeException("Cannot diff two meshes with a different site list");
        }
        if (!mesh1.getStatusLabels().equals(mesh2.getStatusLabels())) {
            throw new RuntimeException("Cannot diff two meshes with a different status labels");
        }
        if (mesh1.isSplitCell() != mesh2.isSplitCell()) {
            throw new RuntimeException("Cannot diff two meshes if they aren't both split-cells");
        }
        JsonObjectBuilder diffMesh = Json.createObjectBuilder();
        diffMesh.add("name", "Diff between (" + mesh1.getName() + ") and (" + mesh2.getName() + ")");
        diffMesh.add("statusLabels", mesh1.toJson().getJsonArray("statusLabels"));
        diffMesh.add("lastUpdateTime", mesh1.toJson().getJsonNumber("lastUpdateTime"));
        diffMesh.add("columnNames", mesh1.toJson().getJsonArray("columnNames"));
        diffMesh.add("columnProps", mesh1.toJson().getJsonArray("columnProps"));
        diffMesh.add("checkNames", mesh1.toJson().getJsonArray("checkNames"));
        JsonArrayBuilder resultGrid = Json.createArrayBuilder();
        for (int row = 0; row < mesh1.getSites().size(); row++) {
            JsonArrayBuilder resultRow = Json.createArrayBuilder();
            for (int column = 0; column < mesh1.getSites().size(); column++) {
                if (row == column) {
                    resultRow.addNull();
                } else if (mesh1.statusFor(row, column, Mesh.CellHalf.INITIATED_BY_ROW)
                        == mesh2.statusFor(row, column, Mesh.CellHalf.INITIATED_BY_ROW)) {
                    resultRow.add(Json.createArrayBuilder().add(Json.createObjectBuilder().add("message", "No difference")
                                .add("status", mesh1.getStatusLabels().size() - 1)
                                .add("prevCheckTime", 0)
                                .add("uri", ""))
                            .add(Json.createObjectBuilder().add("message", "No difference")
                                .add("status", mesh1.getStatusLabels().size() - 1)
                                .add("prevCheckTime", 0)
                                .add("uri", "")));
                } else {
                    resultRow.add(Json.createArrayBuilder().add(Json.createObjectBuilder().add("message", "Difference found")
                                .add("status", mesh1.statusFor(row, column, Mesh.CellHalf.INITIATED_BY_ROW))
                                .add("prevCheckTime", 0)
                                .add("uri", ""))
                            .add(Json.createObjectBuilder().add("message", "Difference found")
                                .add("status", mesh2.statusFor(row, column, Mesh.CellHalf.INITIATED_BY_ROW))
                                .add("prevCheckTime", 0)
                                .add("uri", "")));
                }
            }
            resultGrid.add(resultRow);
        }
        diffMesh.add("grid", resultGrid);
        diffMesh.add("rows", mesh1.toJson().getJsonArray("rows"));
        
        return Mesh.from(diffMesh.build());
    }
    
    public static Mesh diff(Mesh mesh1, Mesh mesh2) {
        return calculateDiff(mesh1, mesh2);
    }
}
