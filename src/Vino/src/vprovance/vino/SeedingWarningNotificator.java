/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vprovance.vino;

import VProvance.Core.Database.DBConnection;
import VProvance.Core.Notifications.IWarningNotificator;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author DexpUser
 */
public class SeedingWarningNotificator implements IWarningNotificator {

    private final ISeddingRecommender _recommender;
    
    public SeedingWarningNotificator(ISeddingRecommender recommender) {
        _recommender = recommender;
    }
    
    @Override
    public List<String> GetNotification() {
        ArrayList<String> res = new ArrayList<>();
        
        if(_recommender.GetRecommendations().anyMatch(r -> r.GetDate().getTime() - new Date().getTime() < 3*24*60*60*1000))
            res.add("Время рекомендуемого посева на одном из поле менее 3х дней");
                    
        return res;
    }
    
}
