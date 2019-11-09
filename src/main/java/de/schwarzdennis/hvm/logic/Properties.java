package de.schwarzdennis.hvm.logic;

import java.io.File;

public class Properties {

  public File getDefaultVoicePath() {
    java.util.Properties sysprops = System.getProperties();
    String fileSeperator = sysprops.getProperty("file.separator");
    String prefix = sysprops.getProperty("user.home");

    if ("Linux".equalsIgnoreCase(sysprops.getProperty("os.name"))) {
      if (prefix != null) {
        return new File(prefix + fileSeperator + ".hedgewars" + fileSeperator + "Data" + fileSeperator + "Sounds" + fileSeperator + "voices");
      }
    }
    if ("Windows".equalsIgnoreCase(sysprops.getProperty("os.name"))) {
      if (prefix != null) {
        return new File(prefix + fileSeperator + "Hedgewars" + fileSeperator + "Data" + fileSeperator + "Sounds" + fileSeperator + "voices");
      }
    }
    if ("MacOS".equalsIgnoreCase(sysprops.getProperty("os.name"))) {
      if (prefix != null) {
        return new File(prefix + fileSeperator + "Library/Application Support/Hedgewars" + fileSeperator + "Data" + fileSeperator + "Sounds" + fileSeperator + "voices");
      }
    }

    return null;
  }
}
