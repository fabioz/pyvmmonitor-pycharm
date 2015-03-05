package com.brainwy.pyvmmonitor;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComponentWithBrowseButton;
import com.intellij.openapi.ui.TextComponentAccessor;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.PanelWithAnchor;
import com.intellij.ui.RawCommandLineEditor;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.jetbrains.python.debugger.PyDebuggerOptionsProvider;
import com.jetbrains.python.run.*;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Form to be shown to the user in the editor. Similar to PythonRunConfigurationForm but with options related
 * to running PyVmMonitor.
 */
public class PyVmMonitorPythonRunConfigurationForm {

    private final boolean isGlobalSettingEditor;
    private JPanel myRootPanel;
    private JComponent anchor;
    private JLabel myInitialProfileModeLabel;
    private JComboBox myInitialProfileModeCombo;
    private JLabel myPyVmMonitorLocationLabel;
    private TextFieldWithBrowseButton myPyVmMonitorLocationTextField;

    public PyVmMonitorPythonRunConfigurationForm(boolean isGlobalSettingEditor) {
        this.isGlobalSettingEditor = isGlobalSettingEditor;
        myInitialProfileModeCombo.addItem("Deterministic (profile)");
        myInitialProfileModeCombo.addItem("Sampling (yappi)");
        myInitialProfileModeCombo.addItem("Don't start profiling");
        if (!isGlobalSettingEditor) {
            myInitialProfileModeCombo.addItem("Use Global Setting");
        }

        FileChooserDescriptor chooserDescriptor = new FileChooserDescriptor(true, false, false, false, false, false) {
            @Override
            public boolean isFileVisible(VirtualFile file, boolean showHiddenFiles) {
                return file.isDirectory() || file.getExtension() == null || Comparing.equal(file.getExtension(), "py");
            }
        };

        ComponentWithBrowseButton.BrowseFolderActionListener<JTextField> listenerPyVmMonitorLocation =
                new ComponentWithBrowseButton.BrowseFolderActionListener<JTextField>("Select pyvmmonitor-ui location", "", myPyVmMonitorLocationTextField, null,
                        chooserDescriptor, TextComponentAccessor.TEXT_FIELD_WHOLE_TEXT) {

                    @Override
                    protected void onFileChosen(@NotNull VirtualFile chosenFile) {
                        super.onFileChosen(chosenFile);
                        setPyVmMonitorLocation(chosenFile.getParent().getPath());
                    }
                };

        myPyVmMonitorLocationTextField.addActionListener(listenerPyVmMonitorLocation);
        if (!isGlobalSettingEditor) {
            myPyVmMonitorLocationLabel.setVisible(false);
            myPyVmMonitorLocationTextField.setVisible(false);
        }
    }

    public void setPyVmMonitorLocation(String location) {
        myPyVmMonitorLocationTextField.setText(location);
    }

    public String getPyVmMonitorLocation() {
        return myPyVmMonitorLocationTextField.getText().trim();
    }

    public String getInitialProfileMode() {
        int i = myInitialProfileModeCombo.getSelectedIndex();
        if (i == 0) {
            return PyVmMonitorRunExtension.PROFILE_MODE_LSPROF;
        }
        if (i == 1) {
            return PyVmMonitorRunExtension.PROFILE_MODE_YAPPI;
        }
        if (i == 3) {
            return PyVmMonitorRunExtension.PROFILE_MODE_INHERIT_GLOBAL;
        }
        return PyVmMonitorRunExtension.PROFILE_MODE_NONE;
    }

    public void setInitialProfileMode(String initialProfileMode) {
        int i = 2;
        if (initialProfileMode.equals(PyVmMonitorRunExtension.PROFILE_MODE_LSPROF)) {
            i = 0;
        } else if (initialProfileMode.equals(PyVmMonitorRunExtension.PROFILE_MODE_YAPPI)) {
            i = 1;
        } else if (initialProfileMode.equals(PyVmMonitorRunExtension.PROFILE_MODE_INHERIT_GLOBAL)) {
            i = 3;
        }
        myInitialProfileModeCombo.setSelectedIndex(i);
    }

    public JComponent getPanel() {
        return myRootPanel;
    }

}
