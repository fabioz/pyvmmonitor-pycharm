package com.brainwy.pyvmmonitor;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.ParamsGroup;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.util.ArrayUtil;
import com.jetbrains.python.run.CommandLinePatcher;
import com.jetbrains.python.run.PythonCommandLineState;
import com.jetbrains.python.run.PythonScriptCommandLineState;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Everything else is just scaffolding to get to this class...
 */
public class PyVmMonitorPythonScriptCommandLineState extends PythonScriptCommandLineState {
    public PyVmMonitorPythonScriptCommandLineState(PyVmMonitorPythonRunConfiguration pyVmMonitorPythonRunConfiguration, ExecutionEnvironment env) {
        super(pyVmMonitorPythonRunConfiguration, env);
    }

    @NotNull
    @Override
    public ExecutionResult execute(Executor executor, CommandLinePatcher... patchers) throws ExecutionException {
        if(patchers == null){
            patchers = new CommandLinePatcher[0];
        }
        return super.execute(executor, ArrayUtil.append(patchers, new CommandLinePatcher() {
            @Override
            public void patchCommandLine(GeneralCommandLine commandLine) {
                //TODO: This is the place where things actually happen (i.e.: we add the parameters to run with PyVmMonitor).
                commandLine.getParametersList().getParamsGroup(PythonCommandLineState.GROUP_DEBUGGER).addParameterAt(0, "my.test.py");
            }
        }));
    }
}
