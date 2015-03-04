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
 * This will provide us with an editor to show to the user. Mostly, this is just a wrapper for the form...
 */
public class PyVmMonitorPythonRunConfigurationEditor extends SettingsEditor<AbstractPythonRunConfiguration> {

    private PyVmMonitorPythonRunConfigurationForm myForm;

    public PyVmMonitorPythonRunConfigurationEditor(final AbstractPythonRunConfiguration configuration) {
        myForm = new PyVmMonitorPythonRunConfigurationForm(configuration);
    }

    protected void resetEditorFrom(final AbstractPythonRunConfiguration config) {

        Object location = config.getUserData(Key.create(PyVmMonitorRunExtension.PY_VM_MONITOR_LOCATION));
        if (location == null) {
            location = "";
        }
        myForm.setPyVmMonitorLocation(location.toString());

        Object initialProfileMode = config.getUserData(Key.create(PyVmMonitorRunExtension.INITIAL_PROFILE_MODE));
        if (initialProfileMode == null) {
            initialProfileMode = "";
        }
        if (!PyVmMonitorRunExtension.isValidInitialProfileMode(initialProfileMode.toString())) {
            initialProfileMode = PyVmMonitorRunExtension.PROFILE_MODE_LSPROF;
        }

        myForm.setInitialProfileMode(initialProfileMode.toString());
    }

    protected void applyEditorTo(final AbstractPythonRunConfiguration config) throws ConfigurationException {
        config.putUserData(Key.create(PyVmMonitorRunExtension.PY_VM_MONITOR_LOCATION), myForm.getPyVmMonitorLocation());
        config.putUserData(Key.create(PyVmMonitorRunExtension.INITIAL_PROFILE_MODE), myForm.getInitialProfileMode());
    }

    @NotNull
    protected JComponent createEditor() {
        return myForm.getPanel();
    }

    protected void disposeEditor() {
        myForm = null;
    }
}
