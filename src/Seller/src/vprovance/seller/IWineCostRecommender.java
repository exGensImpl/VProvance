/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vprovance.seller;

import java.util.List;

/**
 *
 * @author DexpUser
 */
public interface IWineCostRecommender {
    
    public List<WineCostRecommendation> GetRecommendation(List<WineCost> currentCosts);
}
