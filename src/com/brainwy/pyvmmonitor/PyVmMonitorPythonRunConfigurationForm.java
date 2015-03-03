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

/**
 * Form to be shown to the user in the editor. Similar to PythonRunConfigurationForm but with options related
 * to running PyVmMonitor.
 */
public class PyVmMonitorPythonRunConfigurationForm implements PythonRunConfigurationParams, PanelWithAnchor {

    private JPanel myRootPanel;
    private TextFieldWithBrowseButton myScriptTextField;
    private RawCommandLineEditor myScriptParametersTextField;
    private JPanel myCommonOptionsPlaceholder;
    private JBLabel myScriptParametersLabel;
    private final AbstractPyCommonOptionsForm myCommonOptionsForm;
    private JComponent anchor;
    private final Project myProject;
    private JBCheckBox myShowCommandLineCheckbox;

    public PyVmMonitorPythonRunConfigurationForm(PythonRunConfiguration configuration) {
        myCommonOptionsForm = PyCommonOptionsFormFactory.getInstance().createForm(configuration.getCommonOptionsFormData());
        myCommonOptionsPlaceholder.add(myCommonOptionsForm.getMainPanel(), BorderLayout.CENTER);

        myProject = configuration.getProject();

        FileChooserDescriptor chooserDescriptor = new FileChooserDescriptor(true, false, false, false, false, false) {
            @Override
            public boolean isFileVisible(VirtualFile file, boolean showHiddenFiles) {
                return file.isDirectory() || file.getExtension() == null || Comparing.equal(file.getExtension(), "py");
            }
        };

        ComponentWithBrowseButton.BrowseFolderActionListener<JTextField> listener =
                new ComponentWithBrowseButton.BrowseFolderActionListener<JTextField>("Select Script", "", myScriptTextField, myProject,
                        chooserDescriptor, TextComponentAccessor.TEXT_FIELD_WHOLE_TEXT) {

                    @Override
                    protected void onFileChosen(@NotNull VirtualFile chosenFile) {
                        super.onFileChosen(chosenFile);
                        myCommonOptionsForm.setWorkingDirectory(chosenFile.getParent().getPath());
                    }
                };

        myScriptTextField.addActionListener(listener);

        setAnchor(myCommonOptionsForm.getAnchor());
    }

    public JComponent getPanel() {
        return myRootPanel;
    }

    @Override
    public AbstractPythonRunConfigurationParams getBaseParams() {
        return myCommonOptionsForm;
    }

    @Override
    public String getScriptName() {
        return FileUtil.toSystemIndependentName(myScriptTextField.getText().trim());
    }

    @Override
    public void setScriptName(String scriptName) {
        myScriptTextField.setText(scriptName == null ? "" : FileUtil.toSystemDependentName(scriptName));
    }

    @Override
    public String getScriptParameters() {
        return myScriptParametersTextField.getText().trim();
    }

    @Override
    public void setScriptParameters(String scriptParameters) {
        myScriptParametersTextField.setText(scriptParameters);
    }

    @Override
    public boolean showCommandLineAfterwards() {
        return myShowCommandLineCheckbox.isSelected();
    }

    @Override
    public void setShowCommandLineAfterwards(boolean showCommandLineAfterwards) {
        myShowCommandLineCheckbox.setSelected(showCommandLineAfterwards);
    }

    @Override
    public JComponent getAnchor() {
        return anchor;
    }

    public boolean isMultiprocessMode() {
        return PyDebuggerOptionsProvider.getInstance(myProject).isAttachToSubprocess();
    }

    public void setMultiprocessMode(boolean multiprocess) {
    }

    @Override
    public void setAnchor(JComponent anchor) {
        this.anchor = anchor;
        myScriptParametersLabel.setAnchor(anchor);
        myCommonOptionsForm.setAnchor(anchor);
    }
}
