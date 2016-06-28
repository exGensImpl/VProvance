/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vprovance.admin;

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
public class VProvanceAdmin extends Application {
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        AuthDialog ad = new AuthDialog("Администратор", null, true);     
        
        ad.addWindowListener (new WindowAdapter () {
            @Override
            public void windowClosing (WindowEvent e) {
                if(ad.isValidAuthParams())
                {
                    AdminMain s = new AdminMain();
                    s.setTitle("Модуль администратора");
                    s.setVisible(true);
                }
            }
        });
           
        ad.setVisible(true);   
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
