/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vprovance.vino;

import VProvance.Core.Database.Field;
import java.util.Date;

/**
 *
 * @author DexpUser
 */
public final class SeddingRecommendation {
    private final Field _field;
    private final Date _date;
    
    public SeddingRecommendation(Field field, Date date) {
        _field = field;
        _date = date;
    }
    
    public Field GetField() {
        return _field;
    }
    
    public Date GetDate() {
        return _date;
    }
}
