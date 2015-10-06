/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.es.maddash.madalert;

import java.util.Arrays;
import java.util.List;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.mockito.InOrder;
import static org.mockito.Mockito.*;

/**
 *
 * @author carcassi
 */
public class TestSetTest {

    @Test
    public void allSites() {
        Mesh mesh = mock(Mesh.class);
        when(mesh.getSites()).thenReturn(Arrays.asList("A", "B", "C", "D"));
        when(mesh.isSplitCell()).thenReturn(true);
        for (int row = 0; row < 4; row++) {
            for (int column = 0; column < 4; column++) {
                when(mesh.statusFor(row, column, Mesh.CellHalf.INITIATED_BY_ROW))
                        .thenReturn((row + column) % 4);
                when(mesh.statusFor(row, column, Mesh.CellHalf.INITIATED_BY_COLUMN))
                        .thenReturn((row + column) % 4);
            }
        }
        
        StatusMatcher.Instance instance = mock(StatusMatcher.Instance.class);
        when(instance.isMatched()).thenReturn(Boolean.TRUE);
        StatusMatcher statusMatcher = mock(StatusMatcher.class);
        when(statusMatcher.prepareInstance(mesh)).thenReturn(instance);
        
        TestSet testSet = Madalert.forAllSites();
        
        testSet.match(mesh, statusMatcher);
        InOrder order = inOrder(instance);
        order.verify(instance).match(1, 0, Mesh.CellHalf.INITIATED_BY_ROW, 1);
        order.verify(instance).match(1, 0, Mesh.CellHalf.INITIATED_BY_COLUMN, 1);
        order.verify(instance).match(2, 0, Mesh.CellHalf.INITIATED_BY_ROW, 2);
        order.verify(instance).match(2, 0, Mesh.CellHalf.INITIATED_BY_COLUMN, 2);
        order.verify(instance).match(3, 0, Mesh.CellHalf.INITIATED_BY_ROW, 3);
        order.verify(instance).match(3, 0, Mesh.CellHalf.INITIATED_BY_COLUMN, 3);
        order.verify(instance).match(0, 1, Mesh.CellHalf.INITIATED_BY_ROW, 1);
        order.verify(instance).match(0, 1, Mesh.CellHalf.INITIATED_BY_COLUMN, 1);
        order.verify(instance).match(2, 1, Mesh.CellHalf.INITIATED_BY_ROW, 3);
        order.verify(instance).match(2, 1, Mesh.CellHalf.INITIATED_BY_COLUMN, 3);
        order.verify(instance).match(3, 1, Mesh.CellHalf.INITIATED_BY_ROW, 0);
        order.verify(instance).match(3, 1, Mesh.CellHalf.INITIATED_BY_COLUMN, 0);
        order.verify(instance).match(0, 2, Mesh.CellHalf.INITIATED_BY_ROW, 2);
        order.verify(instance).match(0, 2, Mesh.CellHalf.INITIATED_BY_COLUMN, 2);
        order.verify(instance).match(1, 2, Mesh.CellHalf.INITIATED_BY_ROW, 3);
        order.verify(instance).match(1, 2, Mesh.CellHalf.INITIATED_BY_COLUMN, 3);
        order.verify(instance).match(3, 2, Mesh.CellHalf.INITIATED_BY_ROW, 1);
        order.verify(instance).match(3, 2, Mesh.CellHalf.INITIATED_BY_COLUMN, 1);
        order.verify(instance).match(0, 3, Mesh.CellHalf.INITIATED_BY_ROW, 3);
        order.verify(instance).match(0, 3, Mesh.CellHalf.INITIATED_BY_COLUMN, 3);
        order.verify(instance).match(1, 3, Mesh.CellHalf.INITIATED_BY_ROW, 0);
        order.verify(instance).match(1, 3, Mesh.CellHalf.INITIATED_BY_COLUMN, 0);
        order.verify(instance).match(2, 3, Mesh.CellHalf.INITIATED_BY_ROW, 1);
        order.verify(instance).match(2, 3, Mesh.CellHalf.INITIATED_BY_COLUMN, 1);
    }

}
