/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VProvance.Core.Notificactions;

import VProvance.Core.Database.DBConnection;
import java.util.List;

/**
 *
 * @author DexpUser
 */
public interface IWarningNotificator {
    public List<String> GetNotification(DBConnection db);
}
