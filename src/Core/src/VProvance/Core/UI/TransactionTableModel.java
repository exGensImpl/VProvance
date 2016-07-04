/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VProvance.Core.UI;

import VProvance.Core.Database.DBConnection;
import VProvance.Core.Database.Transaction;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 *
 * @author DexpUser
 */
public class TransactionTableModel  implements TableModel {
    
    private final Set<TableModelListener> _listeners = new HashSet<>();
    DBConnection _source = DBConnection.instance();
    List<Transaction> _trans = DBConnection.instance().GetTransactions();
    
    @Override
    public int getRowCount() {
        return _trans.size();
    }

    @Override
    public int getColumnCount() {
        return 9;
    }

    @Override
    public String getColumnName(int columnIndex) {
        switch (columnIndex)
        {
            case 0 : return "Время";
            case 1 : return "Тип";
            case 2 : return "Продукт";
            case 3 : return "Количество";
            case 4 : return "Единица измерения";
            case 5 : return "Отправитель";
            case 6 : return "Адресат";
            case 7 : return "Подтверждена";
            case 8 : return "Время подтверждения";
        }
        
        return "";
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex)
        {
            case 0 : return Date.class;
            case 1 : return String.class;
            case 2 : return String.class;
            case 3 : return Double.class;
            case 4 : return String.class;
            case 5 : return String.class;
            case 6 : return String.class;
            case 7 : return Boolean.class;
            case 8 : return Date.class;
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
            case 0 : return _trans.get(rowIndex).getTime();
            case 1 : return _trans.get(rowIndex).getAction();
            case 2 : return _trans.get(rowIndex).getResource();
            case 3 : return _trans.get(rowIndex).getCount();
            case 4 : return _trans.get(rowIndex).getMeasure();
            case 5 : return _trans.get(rowIndex).getSubject();
            case 6 : return _trans.get(rowIndex).getObject();
            case 7 : return _trans.get(rowIndex).getAccepted();
            case 8 : return _trans.get(rowIndex).getAcceptedTime();
        }
        
        return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addTableModelListener(TableModelListener l) {
        _listeners.add(l);
    }

    @Override
    public void removeTableModelListener(TableModelListener l) {
        _listeners.remove(l);
    }
    
    public void Refresh()
    {
        _trans = _source.GetTransactions();
        
        for (TableModelListener listener : _listeners){
            listener.tableChanged(new TableModelEvent(this));
        }    
    }
}
    
