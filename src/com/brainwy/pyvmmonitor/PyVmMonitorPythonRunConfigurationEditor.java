package com.brainwy.pyvmmonitor;

import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.jetbrains.python.run.PythonRunConfiguration;
import com.jetbrains.python.run.PythonRunConfigurationForm;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * This will provide us with an editor to show to the user
 */
public class PyVmMonitorPythonRunConfigurationEditor extends SettingsEditor<PyVmMonitorPythonRunConfiguration> {

    private PyVmMonitorPythonRunConfigurationForm myForm;

    public PyVmMonitorPythonRunConfigurationEditor(final PyVmMonitorPythonRunConfiguration configuration) {
        myForm = new PyVmMonitorPythonRunConfigurationForm(configuration);
    }

    protected void resetEditorFrom(final PyVmMonitorPythonRunConfiguration config) {
        PythonRunConfiguration.copyParams(config, myForm);
    }

    protected void applyEditorTo(final PyVmMonitorPythonRunConfiguration config) throws ConfigurationException {
        PythonRunConfiguration.copyParams(myForm, config);
    }

    @NotNull
    protected JComponent createEditor() {
        return myForm.getPanel();
    }

    protected void disposeEditor() {
        myForm = null;
    }
}
