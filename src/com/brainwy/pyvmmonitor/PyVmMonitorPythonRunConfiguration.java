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
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizerUtil;
import com.intellij.openapi.util.WriteExternalException;
import com.jetbrains.python.run.AbstractPythonRunConfiguration;
import com.jetbrains.python.run.PythonRunConfiguration;
import com.jetbrains.python.run.PythonRunConfigurationParams;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

/**
 * This is the run configuration (has the editor and preferences which show at run > edit configurations).
 */
public class PyVmMonitorPythonRunConfiguration extends PythonRunConfiguration implements PyVmMonitorPythonRunConfigurationParams{
    public static final String INITIAL_PROFILE_MODE = "INITIAL_PROFILE_MODE";
    public static final String PY_VM_MONITOR_LOCATION = "PY_VM_MONITOR_LOCATION";

    private int myInitialProfileMode;
    private String myPyVmMonitorLocation;

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

    @Override
    public void readExternal(Element element) throws InvalidDataException {
        super.readExternal(element);
        myInitialProfileMode = Integer.parseInt(JDOMExternalizerUtil.readField(element, INITIAL_PROFILE_MODE, "0"));
        myPyVmMonitorLocation = JDOMExternalizerUtil.readField(element, PY_VM_MONITOR_LOCATION);
    }

    @Override
    public void writeExternal(Element element) throws WriteExternalException {
        JDOMExternalizerUtil.writeField(element, INITIAL_PROFILE_MODE, Integer.toString(myInitialProfileMode));
        JDOMExternalizerUtil.writeField(element, PY_VM_MONITOR_LOCATION, myPyVmMonitorLocation);
        super.writeExternal(element);
    }

    public static void copyParams1(PyVmMonitorPythonRunConfigurationParams source, PyVmMonitorPythonRunConfigurationParams target) {
        PythonRunConfiguration.copyParams(source, target);
        target.setInitialProfileMode(source.getInitialProfileMode());
        target.setPyVmMonitorLocation(source.getPyVmMonitorLocation());
    }

    @Override
    public void setPyVmMonitorLocation(String location) {
        this.myPyVmMonitorLocation = location;
    }

    @Override
    public String getPyVmMonitorLocation() {
        return this.myPyVmMonitorLocation;
    }

    @Override
    public int getInitialProfileMode() {
        return myInitialProfileMode;
    }

    @Override
    public void setInitialProfileMode(int initialProfileMode) {
        myInitialProfileMode = initialProfileMode;
    }
}