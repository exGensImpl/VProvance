/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vprovance.seller;

import VProvance.Core.UI.AuthDialog;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 *
 * @author DexpUser
 */
public class VProvanceSeller extends Application {
    
    boolean isAuthorized;
    
    public void start(Stage st){};
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        AuthDialog ad = new AuthDialog("Продавец", null, true);     
        
        ad.addWindowListener (new WindowAdapter () {
            @Override
            public void windowClosing (WindowEvent e) {
                if(ad.isValidAuthParams())
                {
                    SellerMain s = new SellerMain();
                    s.setTitle("Модуль продавца");
                    s.setVisible(true);
                }
            }
        });
           
        ad.setVisible(true);      
    }
}
