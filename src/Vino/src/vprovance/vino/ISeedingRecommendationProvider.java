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
interface ISeedingRecommendationProvider {
    public Date GetRecommendation(Field field);
}
