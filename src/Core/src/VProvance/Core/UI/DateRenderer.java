/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VProvance.Core.UI;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author DexpUser
 */
public class DateRenderer extends DefaultTableCellRenderer {

    public DateRenderer() {
        super();
        setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    }

    @Override
    public void setValue(Object value) {
        if ((value != null) && (value instanceof Date)) {
            Date dateValue = (Date) value;
            DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
           
            value = formatter.format(dateValue);
        }
        super.setValue(value);
    }
}
