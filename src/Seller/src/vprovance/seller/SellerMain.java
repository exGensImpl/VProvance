/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vprovance.seller;

import VProvance.Core.Database.DBConnection;
import VProvance.Core.UI.*;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 *
 * @author DexpUser
 */
public class SellerMain extends javax.swing.JFrame {

    /**
     * Creates new form SellerMain
     */
    public SellerMain() {
        initComponents();
        ArrivedBatchTable.removeColumn(ArrivedBatchTable.getColumnModel().getColumn(0));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        ArrivedBatchTable = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        AllBatchTable = new javax.swing.JTable();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        EditUserMI = new javax.swing.JMenuItem();
        AcceptMI = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        ArrivedBatchTable.setModel(new BatchTableModel(new ArrivedBatchSource()));
        jScrollPane1.setViewportView(ArrivedBatchTable);

        jTabbedPane1.addTab("Ожидающие подтверждения", jScrollPane1);

        AllBatchTable.setModel(new BatchTableModel(new DefaultBatchSource()));
        jScrollPane2.setViewportView(AllBatchTable);

        jTabbedPane1.addTab("Партии в магазине", jScrollPane2);

        jMenu1.setText("Файл");

        jMenuItem1.setText("Выход");
        jMenu1.add(jMenuItem1);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Редактирование");

        EditUserMI.setText("Редактирование профиля");
        EditUserMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EditUserMIActionPerformed(evt);
            }
        });
        jMenu2.add(EditUserMI);
        EditUserMI.getAccessibleContext().setAccessibleDescription("");

        AcceptMI.setText("Подтвердить получение партии");
        AcceptMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AcceptMIActionPerformed(evt);
            }
        });
        jMenu2.add(AcceptMI);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 297, Short.MAX_VALUE)
        );

        jTabbedPane1.getAccessibleContext().setAccessibleName("BatchTab");
        jTabbedPane1.getAccessibleContext().setAccessibleDescription("");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void EditUserMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EditUserMIActionPerformed
        EditUserDataDialog ad = new EditUserDataDialog(null, true); 
        ad.setVisible(true);
    }//GEN-LAST:event_EditUserMIActionPerformed

    private void AcceptMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AcceptMIActionPerformed
        int row = ArrivedBatchTable.getSelectedRow();
        if (row == -1) return;
        
        Object id = ArrivedBatchTable.getModel().getValueAt(row, 0);
        
        try {
            DBConnection.instance().AcceptBatch((long)id);
            ((BatchTableModel)ArrivedBatchTable.getModel()).Refresh();
            ((BatchTableModel)AllBatchTable.getModel()).Refresh();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_AcceptMIActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(SellerMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SellerMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SellerMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SellerMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new SellerMain().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem AcceptMI;
    private javax.swing.JTable AllBatchTable;
    private javax.swing.JTable ArrivedBatchTable;
    private javax.swing.JMenuItem EditUserMI;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    // End of variables declaration//GEN-END:variables
}
