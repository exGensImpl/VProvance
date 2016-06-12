/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vprovance.vino;

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
    
    @Override
    public void start(Stage st){};
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        AuthDialog ad = new AuthDialog(null, true);     
        
        ad.addWindowListener (new WindowAdapter () {
            @Override
            public void windowClosing (WindowEvent e) {
                if(ad.isValidAuthParams)
                {
                    VinodelMain v = new VinodelMain();
                    v.setTitle("vProvance vinodel main frame");
                    v.setVisible(true);
                }
            }
        });
           
        ad.setVisible(true);      
    }     
}
