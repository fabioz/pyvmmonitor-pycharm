package com.brainwy.pyvmmonitor;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.RunnerSettings;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import com.jetbrains.python.run.AbstractPythonRunConfiguration;
import com.jetbrains.python.run.PythonRunConfigurationExtension;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class PyVmMonitorRunExtension extends PythonRunConfigurationExtension {

    @Override
    protected void readExternal(@NotNull AbstractPythonRunConfiguration runConfiguration, @NotNull Element element) throws InvalidDataException {
        System.out.println("PyVmMonitorRunExtension: readExternal");
    }

    @Override
    protected void writeExternal(@NotNull AbstractPythonRunConfiguration runConfiguration, @NotNull Element element) throws WriteExternalException {

    }

    @Nullable
    @Override
    protected SettingsEditor createEditor(@NotNull AbstractPythonRunConfiguration configuration) {
        return new SettingsEditor() {
            @Override
            protected void resetEditorFrom(Object s) {

            }

            @Override
            protected void applyEditorTo(Object s) throws ConfigurationException {

            }

            @NotNull
            @Override
            protected JComponent createEditor() {
                return new JLabel("PyVmMonitor createEditor");
            }
        };
    }


    @Nullable
    @Override
    protected String getEditorTitle() {
        return "PyVmMonitor getEditorTitle";
    }

    @Override
    protected boolean isApplicableFor(@NotNull AbstractPythonRunConfiguration configuration) {
        System.out.println("PyVmMonitor: isApplicableFor");
        return true;
    }

    @Override
    protected boolean isEnabledFor(@NotNull AbstractPythonRunConfiguration applicableConfiguration, @Nullable RunnerSettings runnerSettings) {
        System.out.println("PyVmMonitor: isEnabledFor");
        return true;
    }

    @Override
    protected void patchCommandLine(AbstractPythonRunConfiguration configuration, @Nullable RunnerSettings runnerSettings, GeneralCommandLine cmdLine, String runnerId) throws ExecutionException {
        System.out.println("PyVmMonitor patchCommandLine");
    }
}
