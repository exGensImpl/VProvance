/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vprovance.vino;

import VProvance.Core.Database.DBConnection;
import VProvance.Core.Database.Field;
import java.awt.event.WindowEvent;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JOptionPane;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import org.eclipse.persistence.jpa.jpql.parser.DateTime;

/**
 *
 * @author DexpUser
 */
public class FieldsTableModel  implements TableModel {
    
    private Set<TableModelListener> _listeners = new HashSet<>();
    List<Field> _fields = DBConnection.instance().GetFields();
    
    @Override
    public int getRowCount() {
        return _fields.size();
    }

    @Override
    public int getColumnCount() {
        return 4;
    }

    @Override
    public String getColumnName(int columnIndex) {
        switch (columnIndex)
        {
            case 0 : return "Название";
            case 1 : return "Площадь";
            case 2 : return "Кол-во осадков";
            case 3 : return "Тип почв";
        }
        
        return "";
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex)
        {
            case 0 : return String.class;
            case 1 : return Double.class;
            case 2 : return Double.class;
            case 3 : return String.class;
        }
        
        return Object.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex)
        {
            case 0 : return _fields.get(rowIndex).getDescription();
            case 1 : return _fields.get(rowIndex).getSquare();
            case 2 : return _fields.get(rowIndex).getPrecipitation();
            case 3 : return _fields.get(rowIndex).getGroundType();
        }
        
        return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        switch (columnIndex)
        {
            case 0 : _fields.get(rowIndex).setDescription((String)aValue); break;
            case 1 : _fields.get(rowIndex).setSquare((Double)aValue); break;
            case 2 : _fields.get(rowIndex).setPrecipitation((Double)aValue); break;
            case 3 : _fields.get(rowIndex).setGroundType((String)aValue); break;
        }
        
        try {
            DBConnection.instance().UpdateField((String)getValueAt(rowIndex, 0), _fields.get(rowIndex));       
            
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Override
    public void addTableModelListener(TableModelListener l) {
        _listeners.add(l);
    }

    @Override
    public void removeTableModelListener(TableModelListener l) {
        _listeners.remove(l);
    }
}
    
