package com.brainwy.pyvmmonitor;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.jetbrains.python.run.PythonRunConfiguration;
import org.jetbrains.annotations.NotNull;

/**
 * This is the run configuration (has the editor and preferences which show at run > edit configurations).
 */
public class PyVmMonitorPythonRunConfiguration extends PythonRunConfiguration {

    protected PyVmMonitorPythonRunConfiguration(Project project, ConfigurationFactory configurationFactory) {
        super(project, configurationFactory);
    }

    @Override
    protected SettingsEditor<? extends RunConfiguration> createConfigurationEditor() {
        return new PyVmMonitorPythonRunConfigurationEditor(this);
    }

    @Override
    public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment env) throws ExecutionException {
        return new PyVmMonitorPythonScriptCommandLineState(this, env);
    }
}