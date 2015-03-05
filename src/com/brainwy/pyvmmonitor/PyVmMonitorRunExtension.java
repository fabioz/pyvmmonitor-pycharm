package com.brainwy.pyvmmonitor;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.ParamsGroup;
import com.intellij.execution.configurations.RunnerSettings;
import com.intellij.ide.util.PropertiesComponent;
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
    public static final String PROFILE_ON = "PROFILE_ON"; // empty, "false" or "true"

    public static final String PROFILE_MODE_YAPPI = "YAPPI";
    public static final String PROFILE_MODE_LSPROF = "LSPROF";
    public static final String PROFILE_MODE_NONE = "NONE";
    public static final String PROFILE_MODE_INHERIT_GLOBAL = "INHERIT_GLOBAL";

    public static final Key<Object> KEY_INITIAL_PROFILE_MODE = Key.create(PyVmMonitorRunExtension.INITIAL_PROFILE_MODE);


    public static boolean isValidInitialProfileMode(String profileMode, boolean acceptGlobal) {
        if (profileMode == null) {
            return false;
        }
        if(acceptGlobal){
            if(profileMode.equals(PROFILE_MODE_INHERIT_GLOBAL)){
                return true;
            }
        }
        return profileMode.equals(PROFILE_MODE_LSPROF) || profileMode.equals(PROFILE_MODE_YAPPI) ||
                profileMode.equals(PROFILE_MODE_NONE);
    }

    @Override
    protected void readExternal(@NotNull AbstractPythonRunConfiguration runConfiguration, @NotNull Element element) throws InvalidDataException {
        String value = JDOMExternalizerUtil.readField(element, INITIAL_PROFILE_MODE, PROFILE_MODE_INHERIT_GLOBAL);
        runConfiguration.putUserData(KEY_INITIAL_PROFILE_MODE, value);
    }

    private static String getDefaultPyVmMonitorLocation() {
        //Reference: org.python.pydev.debug.profile.PyProfilePreferences
        File settings = null;

        try {
            if (SystemInfo.isMac) {
                settings = new File(System.getProperty("user.home"), "Library");
                settings = new File(settings, "Application Support");
                settings = new File(settings, "Brainwy");
                settings = new File(settings, "PyVmMonitor.ini");

            } else if (SystemInfo.isLinux) {
                settings = new File(System.getProperty("user.home"), ".config/Brainwy/pyvmmonitor.ini");

            } else if (SystemInfo.isWindows) {
                //It may not be available in all versions of windows, but if it is, let's use it...
                String env = System.getenv("LOCALAPPDATA");
                if (env != null && env.length() > 0 && new File(env).exists()) {
                    settings = new File(new File(env, "Brainwy"), "PyVmMonitor.ini");
                }
            }
        } catch (Exception e) {
            Log.log(e);
        }
        String defaultLocation = null;

        try {
            if (settings != null && settings.exists()) {
                Properties props = new Properties();
                props.load(new FileInputStream(settings));
                String property = props.getProperty("pyvmmonitor_ui_executable");
                if (property != null) {
                    defaultLocation = property;
                }
            }
        } catch (Exception e) {
            Log.log(e);
        }
        return defaultLocation;
    }

    @Override
    protected void writeExternal(@NotNull AbstractPythonRunConfiguration runConfiguration, @NotNull Element element) throws WriteExternalException {
        String initialProfileModeFromRunConfiguration = getInitialProfileModeFromRunConfiguration(runConfiguration);
        JDOMExternalizerUtil.writeField(element, INITIAL_PROFILE_MODE, initialProfileModeFromRunConfiguration);
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

    public static String getGlobalPyVmMonitorLocation() {
        PropertiesComponent instance = PropertiesComponent.getInstance();
        String location = instance.getValue(PyVmMonitorRunExtension.PY_VM_MONITOR_LOCATION);
        if (location == null || location.toString().length() == 0 || !new File(location.toString()).exists()) {
            location = getDefaultPyVmMonitorLocation();
        }
        if (location == null) {
            location = "";
        }

        if (!new File(location).exists() || !new File(location).isFile()) {
            if (SystemInfo.isMac) {
                File f = new File(location, "Contents");
                f = new File(f, "MacOS");
                f = new File(f, "pyvmmonitor-ui");
                if (f.exists()) {
                    return f.getAbsolutePath();
                }
            }
        }


        return location;
    }

    public static String getGlobalInitialProfileMode() {
        PropertiesComponent instance = PropertiesComponent.getInstance();
        String initialProfileMode = instance.getValue(PyVmMonitorRunExtension.INITIAL_PROFILE_MODE);

        if (initialProfileMode == null) {
            initialProfileMode = "";
        }
        if (!PyVmMonitorRunExtension.isValidInitialProfileMode(initialProfileMode.toString(), false)) {
            initialProfileMode = PyVmMonitorRunExtension.PROFILE_MODE_LSPROF;
        }

        return initialProfileMode;
    }

    public static void setGlobalInitialProfileMode(String globalInitialProfileMode) {
        if (isValidInitialProfileMode(globalInitialProfileMode, false)) {
            PropertiesComponent instance = PropertiesComponent.getInstance();
            instance.setValue(PyVmMonitorRunExtension.INITIAL_PROFILE_MODE, globalInitialProfileMode);
        }
    }

    public static void setGlobalInitialLocation(String globalInitialLocation) {
        if (globalInitialLocation != null && new File(globalInitialLocation).exists()) {
            PropertiesComponent instance = PropertiesComponent.getInstance();
            instance.setValue(PyVmMonitorRunExtension.PY_VM_MONITOR_LOCATION, globalInitialLocation);
        }
    }


    public static boolean isProfileOn() {
        PropertiesComponent instance = PropertiesComponent.getInstance();
        String profileOn = instance.getValue(PyVmMonitorRunExtension.PROFILE_ON);
        if (profileOn != null && profileOn.equals("true")) {
            return true;
        }
        return false;
    }


    @Override
    protected void patchCommandLine(AbstractPythonRunConfiguration configuration, @Nullable RunnerSettings runnerSettings, GeneralCommandLine cmdLine, String runnerId) throws ExecutionException {
        //Reference: org.python.pydev.debug.profile.PyProfilePreferences.addProfileArgs(List<String>, boolean, boolean)
        if (!isProfileOn()) {
            return;
        }

        if (PyDebugRunner.PY_DEBUG_RUNNER.equals(runnerId)) {
            return; //Don't do it for the debugger.
        }

        ParamsGroup paramsGroup = cmdLine.getParametersList().getParamsGroup(PythonCommandLineState.GROUP_DEBUGGER);

        String location = getGlobalPyVmMonitorLocation();

        String initialProfileMode = getInitialProfileModeFromRunConfiguration(configuration);
        if (initialProfileMode.equals(PyVmMonitorRunExtension.PROFILE_MODE_INHERIT_GLOBAL)) {
            initialProfileMode = getGlobalInitialProfileMode();
        }

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
        Object initialProfileMode = configuration.getUserData(KEY_INITIAL_PROFILE_MODE);
        if (initialProfileMode == null) {
            initialProfileMode = "";
        }
        if (!PyVmMonitorRunExtension.isValidInitialProfileMode(initialProfileMode.toString(), true)) {
            initialProfileMode = PyVmMonitorRunExtension.PROFILE_MODE_INHERIT_GLOBAL;
        }
        return initialProfileMode.toString();
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
