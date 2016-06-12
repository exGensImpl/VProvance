/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vprovance.vino;

import java.math.BigInteger;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import org.eclipse.persistence.jpa.jpql.parser.DateTime;

/**
 *
 * @author DexpUser
 */
public class BatchTableModel implements TableModel {
    
    private Set<TableModelListener> _listeners = new HashSet<>();
    List<UsefullBatch> _batches;
    
    public BatchTableModel(List<UsefullBatch> batches)// throws Exception
    {
        //if (batches == null) throw new Exception();        
        _batches = batches;
    }

    @Override
    public int getRowCount() {
        return _batches.size();
    }

    @Override
    public int getColumnCount() {
        return 7;
    }

    @Override
    public String getColumnName(int columnIndex) {
        switch (columnIndex)
        {
            case 0 : return "Тип ресурса";
            case 1 : return "Кол-во";
            case 2 : return "Мера";
            case 3 : return "Описание";
            case 4 : return "Стоимость";
            case 5 : return "Дата производства";
            case 6 : return "Место храниния";
        }
        
        return "";
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex)
        {
            case 0 : return String.class;
            case 1 : return Double.class;
            case 2 : return String.class;
            case 3 : return String.class;
            case 4 : return BigInteger.class;
            case 5 : return DateTime.class;
            case 6 : return String.class;
        }
        
        return Object.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex)
        {
            case 0 : return _batches.get(rowIndex).getResourceType();
            case 1 : return _batches.get(rowIndex).getCount();
            case 2 : return _batches.get(rowIndex).getMeasure();
            case 3 : return _batches.get(rowIndex).getDescription();
            case 4 : return _batches.get(rowIndex).getCost();
            case 5 : return _batches.get(rowIndex).getProductionDate();
            case 6 : return _batches.get(rowIndex).getPlaceName();
        }
        
        return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addTableModelListener(TableModelListener l) {
        _listeners.add(l);
    }

    @Override
    public void removeTableModelListener(TableModelListener l) {
        _listeners.remove(l);
    }
    
    public void addBatch(UsefullBatch batch) throws SQLException
    {
        DBConnection.instance().AddBatch(batch);       
        
        _batches = DBConnection.instance().GetBatches();
        for (TableModelListener listener : _listeners){
            listener.tableChanged(new TableModelEvent(this));
        }    
    }
}
