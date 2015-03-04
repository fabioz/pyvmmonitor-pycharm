package com.brainwy.pyvmmonitor;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.ParamsGroup;
import com.intellij.execution.configurations.RunnerSettings;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.util.*;
import com.jetbrains.python.debugger.PyDebugRunner;
import com.jetbrains.python.run.AbstractPythonRunConfiguration;
import com.jetbrains.python.run.PythonCommandLineState;
import com.jetbrains.python.run.PythonRunConfigurationExtension;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PyVmMonitorRunExtension extends PythonRunConfigurationExtension {

    public static final String PY_VM_MONITOR_LOCATION = "PyVmMonitorLocation";
    public static final String INITIAL_PROFILE_MODE = "PyVmMonitorInitialProfileMode";

    public static final String PROFILE_MODE_YAPPI = "YAPPI";
    public static final String PROFILE_MODE_LSPROF = "DETERMINISTIC";
    public static final String PROFILE_MODE_NONE = "NONE";

    public static boolean isValidInitialProfileMode(String profileMode) {
        if (profileMode == null) {
            return false;
        }
        return profileMode.equals(PROFILE_MODE_LSPROF) || profileMode.equals(PROFILE_MODE_LSPROF) || profileMode.equals(PROFILE_MODE_NONE);
    }

    @Override
    protected void readExternal(@NotNull AbstractPythonRunConfiguration runConfiguration, @NotNull Element element) throws InvalidDataException {
        String location = JDOMExternalizerUtil.readField(element, PY_VM_MONITOR_LOCATION);
        if (location == null || location.length() == 0) {
            location = getDefaultPyVmMonitorLocation();
        }

        if (location == null) {
            location = "";
        }


        runConfiguration.putUserData(Key.create(INITIAL_PROFILE_MODE), JDOMExternalizerUtil.readField(element, INITIAL_PROFILE_MODE, PROFILE_MODE_YAPPI));
        runConfiguration.putUserData(Key.create(PY_VM_MONITOR_LOCATION), location);
    }

    private String getDefaultPyVmMonitorLocation() {
        //TODO: Cover other platforms!
        //Reference: org.python.pydev.debug.profile.PyProfilePreferences
        String location = null;
        if (SystemInfo.isWindows) {
            try {
                //It may not be available in all versions of windows, but if it is, let's use it...
                String env = System.getenv("LOCALAPPDATA");
                if (env != null && env.length() > 0 && new File(env).exists()) {
                    File settings = new File(new File(env, "Brainwy"), "PyVmMonitor.ini");
                    if (settings.exists()) {
                        Properties props = new Properties();
                        props.load(new FileInputStream(settings));
                        String property = props.getProperty("pyvmmonitor_ui_executable");
                        location = property;
                    }
                }
            } catch (Exception e) {
                Log.log(e);
            }
        }
        return location;
    }

    @Override
    protected void writeExternal(@NotNull AbstractPythonRunConfiguration runConfiguration, @NotNull Element element) throws WriteExternalException {
        JDOMExternalizerUtil.writeField(element, INITIAL_PROFILE_MODE, getInitialProfileModeFromRunConfiguration(runConfiguration));
        JDOMExternalizerUtil.writeField(element, PY_VM_MONITOR_LOCATION, getPyVmMonitorLocationFromRunConfiguration(runConfiguration));
    }

    @Nullable
    @Override
    protected SettingsEditor<AbstractPythonRunConfiguration> createEditor(@NotNull AbstractPythonRunConfiguration configuration) {
        return new PyVmMonitorPythonRunConfigurationEditor(configuration);
    }


    @Nullable
    @Override
    protected String getEditorTitle() {
        return "PyVmMonitor (profile)";
    }

    @Override
    protected boolean isApplicableFor(@NotNull AbstractPythonRunConfiguration configuration) {
        return true;
    }

    @Override
    protected boolean isEnabledFor(@NotNull AbstractPythonRunConfiguration applicableConfiguration, @Nullable RunnerSettings runnerSettings) {
        return true;
    }

    @Override
    protected void patchCommandLine(AbstractPythonRunConfiguration configuration, @Nullable RunnerSettings runnerSettings, GeneralCommandLine cmdLine, String runnerId) throws ExecutionException {
        //Reference: org.python.pydev.debug.profile.PyProfilePreferences.addProfileArgs(List<String>, boolean, boolean)

        if (PyDebugRunner.PY_DEBUG_RUNNER.equals(runnerId)) {
            return; //Don't do it for the debugger.
        }

        ParamsGroup paramsGroup = cmdLine.getParametersList().getParamsGroup(PythonCommandLineState.GROUP_DEBUGGER);

        String location = getPyVmMonitorLocationFromRunConfiguration(configuration);

        String initialProfileMode = getInitialProfileModeFromRunConfiguration(configuration);

        final String pyVmMonitorUILocation = location.toString();

        boolean actualRun = true;
        if (pyVmMonitorUILocation == null || pyVmMonitorUILocation.length() == 0) {
            if (actualRun) {
                SwingUtilities.invokeLater(new Runnable() {

                    public void run() {
                        openWarning(
                                "Unable to run in profile mode.",
                                "Unable to run in profile mode: pyvmmonitor-ui location not specified.");
                    }
                });
            }
            return;

        }

        if (!new File(pyVmMonitorUILocation).exists()) {
            if (actualRun) {
                SwingUtilities.invokeLater(new Runnable() {

                    public void run() {
                        openWarning(
                                "Unable to run in profile mode.",
                                "Unable to run in profile mode: Invalid location for pyvmmonitor-ui: "
                                        + pyVmMonitorUILocation);
                    }
                });
            }
            return;
        }

        // Ok, we have the pyvmmonitor-ui executable location, let's discover the pyvmmonitor.__init__ location
        // for doing the launch.
        File file = new File(pyVmMonitorUILocation);
        File publicApi = new File(file.getParentFile(), "public_api");
        File pyvmmonitorFolder = new File(publicApi, "pyvmmonitor");
        final File pyvmmonitorInit = new File(pyvmmonitorFolder, "__init__.py");
        if (!pyvmmonitorInit.exists()) {
            if (actualRun) {
                SwingUtilities.invokeLater(new Runnable() {

                    public void run() {
                        openWarning(
                                "Unable to run in profile mode.",
                                "Unable to run in profile mode: Invalid location for pyvmmonitor/__init__.py: "
                                        + pyvmmonitorInit);
                    }
                });
            }
            return;

        }

        // Now, for the profile to work we have to change the initial script to be pyvmmonitor.__init__.
        List<String> cmdArgs = new ArrayList<String>();
        cmdArgs.add(pyvmmonitorInit.getAbsolutePath());

        if (initialProfileMode.equals(PyVmMonitorRunExtension.PROFILE_MODE_YAPPI)) {
            cmdArgs.add("--profile=yappi");
        } else if (initialProfileMode.equals(PyVmMonitorRunExtension.PROFILE_MODE_LSPROF)) {
            cmdArgs.add("--profile=lsprof");
        } else {
            //Don't pass profile mode
        }

        // We'll spawn the UI ourselves (so, ask the backend to skip that step).
        // We have to do that because otherwise the process we launch will 'appear' to be live unless we
        // also close the profiler.
        cmdArgs.add("--spawn-ui=false");

        for (String s : cmdArgs) {
            paramsGroup.addParameter(s);
        }


        if (actualRun) {
            try {
                Runtime.getRuntime().exec(new String[]{pyVmMonitorUILocation, "--default-port-single-instance"}, null,
                        new File(pyVmMonitorUILocation).getParentFile());
            } catch (Exception e) {
                openWarning("Error starting PyVmMonitor (see notifications view).", "Error starting PyVmMonitor");
                Log.log(e);
            }
        }
    }

    private String getInitialProfileModeFromRunConfiguration(AbstractPythonRunConfiguration configuration) {
        Object initialProfileMode = configuration.getUserData(Key.create(PyVmMonitorRunExtension.INITIAL_PROFILE_MODE));
        if (initialProfileMode == null) {
            initialProfileMode = "";
        }
        if (!PyVmMonitorRunExtension.isValidInitialProfileMode(initialProfileMode.toString())) {
            initialProfileMode = PyVmMonitorRunExtension.PROFILE_MODE_LSPROF;
        }
        return initialProfileMode.toString();
    }

    private String getPyVmMonitorLocationFromRunConfiguration(AbstractPythonRunConfiguration configuration) {
        Object location = configuration.getUserData(Key.create(PyVmMonitorRunExtension.PY_VM_MONITOR_LOCATION));
        if (location == null || location.toString().length() == 0 || !new File(location.toString()).exists()) {
            location = getDefaultPyVmMonitorLocation();
        }
        if (location == null) {
            location = "";
        }
        return location.toString();
    }

    private void openWarning(final String title, final String message) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JOptionPane.showMessageDialog(new JFrame(), message, title, JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void openError(final String title, final String message) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JOptionPane.showMessageDialog(new JFrame(), message, title, JOptionPane.ERROR_MESSAGE);
            }
        });
    }

}
