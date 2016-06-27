/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vprovance.vino;

import VProvance.Core.Database.UsefullBatch;
import VProvance.Core.Database.DBConnection;
import VProvance.Core.UI.BatchTableModel;
import VProvance.Core.UI.IBatchSource;
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
public class WinemakerBatchTableModel extends BatchTableModel  {
           
    public void addBatch(UsefullBatch batch) throws SQLException
    {
        DBConnection.instance().AddBatch(batch);    
        Refresh();    
    }
}
