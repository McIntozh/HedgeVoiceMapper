package de.schwarzdennis.hvm.logic;

import de.schwarzdennis.hvm.layout.Main;
import de.schwarzdennis.hvm.layout.SoundFileSelect;
import de.schwarzdennis.hvm.layout.VoicePackFolderSelect;
import de.schwarzdennis.hvm.layout.VoicePackSelect;
import java.io.File;
import javax.swing.JTextField;

public class Chooser {

  private SoundFileSelect fileSelect = new SoundFileSelect();
  private VoicePackFolderSelect voicePackFolderSelect;
  private VoicePackSelect voicePackSelect;
  private JTextField targetField;
  private Main main;

  public Chooser(Main main) {
    this.main = main;
  }

  public void chooseSoundFile(JTextField targetField) {
    this.targetField = targetField;
    fileSelect.setVisible(true);
    targetField.getTopLevelAncestor().setEnabled(false);
  }

  public void soundFileChosen(File chosenFile) {
    fileSelect.setVisible(false);
    targetField.getTopLevelAncestor().setEnabled(true);
    if (chosenFile != null) {
      targetField.setText(chosenFile.getAbsolutePath());
    }
    main.toFront();
  }

  public void chooseVoicePackPath(File voicePackPath, JTextField targetField) {
    this.targetField = targetField;
    voicePackFolderSelect = new VoicePackFolderSelect(voicePackPath);
    voicePackFolderSelect.setVisible(true);
    targetField.getTopLevelAncestor().setEnabled(false);
  }

  public void voicePackPathChosen(File chosenFile) {
    voicePackFolderSelect.setVisible(false);
    targetField.getTopLevelAncestor().setEnabled(true);
    if (chosenFile != null) {
      targetField.setText(chosenFile.getAbsolutePath());
      Main.voicePackPath = chosenFile;
    }
    main.toFront();
  }

  public void chooseVoicePack(File voicePackPath, JTextField targetField) {
    this.targetField = targetField;
    voicePackSelect = new VoicePackSelect(voicePackPath);
    voicePackSelect.setVisible(true);
    targetField.getTopLevelAncestor().setEnabled(false);
  }

  public void voicePackChosen(File chosenFile) {
    voicePackSelect.setVisible(false);
    targetField.getTopLevelAncestor().setEnabled(true);
    if (chosenFile != null) {
      if (!Main.voicePackPath.getName().equals(chosenFile.getName())) {
        targetField.setText(chosenFile.getName());
        main.load();
      }
    }
    main.toFront();
  }
}
