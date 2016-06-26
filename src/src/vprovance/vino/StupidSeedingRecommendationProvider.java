/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vprovance.vino;

import java.util.Date;

/**
 *
 * @author DexpUser
 */
public class StupidSeedingRecommendationProvider implements ISeedingRecommendationProvider{

    @Override
    public Date GetRecommendation(Field field) {
        return new Date(116,6,6);
    }    
}
