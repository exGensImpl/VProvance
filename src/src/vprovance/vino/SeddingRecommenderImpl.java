/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vprovance.vino;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

/**
 *
 * @author DexpUser
 */
class SeddingRecommenderImpl implements ISeddingRecommender {
    
    private final DBConnection _connection;
    private final ISeedingRecommendationProvider _provider;
    
    public SeddingRecommenderImpl(DBConnection connection, ISeedingRecommendationProvider provider){
        _connection = connection;
        _provider = provider;
    }
    
    @Override
    public Stream<SeddingRecommendation> GetRecommendations() {
        return _connection
                .GetFields()
                .stream()
                .filter((field) -> (field.getSeedingBy() == null))
                .map((field) -> new SeddingRecommendation(field, _provider.GetRecommendation(field)));
    }
}
