/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VProvance.Core.Database;

/**
 *
 * @author DexpUser
 */
public class DataUnreachableException extends Exception
{
    DataUnreachableReason _reason;
    
    public DataUnreachableException(DataUnreachableReason reason) {
        _reason = reason;
    }
    
    public DataUnreachableReason GetReason() {
        return _reason;
    }
}
