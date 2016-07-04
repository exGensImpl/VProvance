/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vprovance.stockman;

import VProvance.Core.UI.AuthDialog;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 *
 * @author DexpUser
 */
public class VProvanceStockman extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        AuthDialog ad = new AuthDialog("Кладовщик", null, true);     
        
        ad.addWindowListener (new WindowAdapter () {
            @Override
            public void windowClosing (WindowEvent e) {
                if(ad.isValidAuthParams())
                {
                    StockmanMain s = new StockmanMain();
                    s.setTitle("Модуль кладовщика");
                    s.setVisible(true);
                }
            }
        });
           
        ad.setVisible(true);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
