package de.schwarzdennis.hvm.layout;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import javax.swing.JFileChooser;
import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;

public class VoicePackFolderSelect extends javax.swing.JFrame {

  private File path;

  public VoicePackFolderSelect(File path) {
    this.path = path;
    initComponents();
    super.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    super.addWindowListener(new WindowListener() {
      @Override
      public void windowOpened(WindowEvent e) {
      }

      @Override
      public void windowClosing(WindowEvent e) {
        Main.chooser.voicePackPathChosen(null);
      }

      @Override
      public void windowClosed(WindowEvent e) {
      }

      @Override
      public void windowIconified(WindowEvent e) {
      }

      @Override
      public void windowDeiconified(WindowEvent e) {
      }

      @Override
      public void windowActivated(WindowEvent e) {
      }

      @Override
      public void windowDeactivated(WindowEvent e) {
      }
    });
  }

  @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jFileChooser1 = new javax.swing.JFileChooser();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("de/schwarzdennis/hvm/bundles/other"); // NOI18N
        setTitle(bundle.getString("window_chooseFolder_title")); // NOI18N
        setAlwaysOnTop(true);
        setName("voicePackDirectoryChooser");

        jFileChooser1.setCurrentDirectory(getPath());
        jFileChooser1.setDialogTitle(bundle.getString("window_chooseFolder_title")); // NOI18N
        jFileChooser1.setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY);
        jFileChooser1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okClick(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jFileChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jFileChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        getAccessibleContext().setAccessibleName("");

        pack();
    }// </editor-fold>//GEN-END:initComponents

  public File getPath() {
    return path;
  }

    private void okClick(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okClick
      File chosenFile = null;
      if (JFileChooser.APPROVE_SELECTION.equals(evt.getActionCommand())) {
        chosenFile = jFileChooser1.getSelectedFile().getAbsoluteFile();
      }
      Main.chooser.voicePackPathChosen(chosenFile);
    }//GEN-LAST:event_okClick
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JFileChooser jFileChooser1;
    // End of variables declaration//GEN-END:variables
}
