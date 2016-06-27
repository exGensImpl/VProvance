/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vprovance.seller;

/**
 *
 * @author DexpUser
 */
public class WineCost {
    
    private final String _type;
    private final double _cost;
    
    public WineCost(String type, double cost) {
        _type = type;
        _cost = cost;
    }
    
    public String getType() {
        return _type;
    }
    
    public double getCost() {
        return _cost;
    }
}
