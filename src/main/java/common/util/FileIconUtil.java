package common.util;

import sun.awt.shell.ShellFolder;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;

public class FileIconUtil {
    public static Icon icon(File file) {
        return FileSystemView.getFileSystemView()
                .getSystemIcon(file);
    }

    public static Image iconBig(File file) {
        try {
            return ShellFolder.getShellFolder(file)
                    .getIcon(true);
        } catch (FileNotFoundException e) {
            return ExceptionUtil.throwT(e);
        }
    }
}
