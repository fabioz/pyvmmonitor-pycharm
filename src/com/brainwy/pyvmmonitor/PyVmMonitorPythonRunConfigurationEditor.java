package com.brainwy.pyvmmonitor;

import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.util.Key;
import com.jetbrains.python.run.AbstractPythonRunConfiguration;
import com.jetbrains.python.run.PythonRunConfiguration;
import com.jetbrains.python.run.PythonRunConfigurationForm;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * This will provide us with an editor to show to the user (for a launch configuration).
 * Mostly, this is just a wrapper for the form...
 */
public class PyVmMonitorPythonRunConfigurationEditor extends SettingsEditor<AbstractPythonRunConfiguration> {

    private PyVmMonitorPythonRunConfigurationForm myForm;

    public PyVmMonitorPythonRunConfigurationEditor(final AbstractPythonRunConfiguration configuration) {
        myForm = new PyVmMonitorPythonRunConfigurationForm(false);
    }

    protected void resetEditorFrom(final AbstractPythonRunConfiguration config) {
        Object initialProfileMode = config.getUserData(PyVmMonitorRunExtension.KEY_INITIAL_PROFILE_MODE);
        if (initialProfileMode == null) {
            initialProfileMode = "";
        }
        if (!PyVmMonitorRunExtension.isValidInitialProfileMode(initialProfileMode.toString(), true)) {
            initialProfileMode = PyVmMonitorRunExtension.PROFILE_MODE_INHERIT_GLOBAL;
        }

        myForm.setInitialProfileMode(initialProfileMode.toString());
    }

    protected void applyEditorTo(final AbstractPythonRunConfiguration config) throws ConfigurationException {
        String initialProfileMode = myForm.getInitialProfileMode();
        config.putUserData(PyVmMonitorRunExtension.KEY_INITIAL_PROFILE_MODE, initialProfileMode);
    }

    @NotNull
    protected JComponent createEditor() {
        return myForm.getPanel();
    }

    protected void disposeEditor() {
        myForm = null;
    }
}
