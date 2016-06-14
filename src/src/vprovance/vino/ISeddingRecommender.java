/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vprovance.vino;

import java.util.List;
import java.util.stream.Stream;

/**
 *
 * @author DexpUser
 */
public interface ISeddingRecommender {

    /**
     *
     * @return
     */
    public Stream<SeddingRecommendation> GetRecommendations();
}
