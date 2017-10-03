package de.schwarzdennis.hvm.logic;

import java.io.File;
import java.util.ResourceBundle;
import javax.swing.filechooser.FileFilter;

public class OggFileFilter extends FileFilter {

    @Override
    public boolean accept(File f) {
        return (f!=null && (f.isDirectory() || f.getName().endsWith(".ogg")));
    }

    @Override
    public String getDescription() {
        return ResourceBundle.getBundle("de/schwarzdennis/hvm/bundles/other").getString("fileFilterOGG");
    }
}
