/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vprovance.vino;

import java.util.List;
import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataListener;

/**
 *
 * @author DexpUser
 */
public final class UserRolesComboBoxModel implements ComboBoxModel<String> {
    
    private final List<String> _userRoles = DBConnection.instance().GetUserRoles();
    private String _selectedRole = _userRoles.get(0);
    
    @Override
    public void setSelectedItem(Object anItem) {
        _selectedRole = (String)anItem;
    }

    @Override
    public Object getSelectedItem() {
        return _selectedRole;
    }

    @Override
    public int getSize() {
        return _userRoles.size();
    }

    @Override
    public String getElementAt(int index) {
        return _userRoles.get(index);
    }

    @Override
    public void addListDataListener(ListDataListener l) {
        }

    @Override
    public void removeListDataListener(ListDataListener l) {
        }
    
}
