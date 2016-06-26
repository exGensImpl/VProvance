/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vprovance.vino;

import VProvance.Core.Database.DBConnection;
import VProvance.Core.UI.AuthDialog;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 *
 * @author DexpUser
 */
public class Main extends Application {
    
    boolean isAuthorized;
    
    public void start(Stage st){};
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        AuthDialog ad = new AuthDialog("Винодел", null, true);     
        
        ad.addWindowListener (new WindowAdapter () {
            @Override
            public void windowClosing (WindowEvent e) {
                if(ad.isValidAuthParams())
                {
                    VinodelMain v = new VinodelMain(
                            new SeddingRecommenderImpl(
                                    DBConnection.instance(),
                                    new StupidSeedingRecommendationProvider())
                    );
                    v.setTitle("Модуль винодела");
                    v.setVisible(true);
                }
            }
        });
           
        ad.setVisible(true);      
    }     
}
