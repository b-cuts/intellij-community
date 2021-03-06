/*
 * Copyright 2000-2016 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.intellij.debugger.ui.breakpoints;

import com.intellij.debugger.engine.DebugProcessImpl;
import com.intellij.debugger.engine.SuspendContextImpl;
import com.intellij.debugger.engine.evaluation.EvaluateException;
import com.intellij.debugger.engine.events.SuspendContextCommandImpl;
import com.intellij.debugger.memory.utils.StackFrameItem;
import com.intellij.debugger.settings.DebuggerSettings;
import com.intellij.openapi.util.Key;
import com.intellij.util.containers.ContainerUtil;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.Value;
import com.sun.jdi.event.LocatableEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.java.debugger.breakpoints.properties.JavaMethodBreakpointProperties;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author egor
 */
public class StackCapturingLineBreakpoint extends WildcardMethodBreakpoint {
  private final DebugProcessImpl myDebugProcess;
  public static final Key<Map<ObjectReference, List<StackFrameItem>>> CAPTURED_STACKS = Key.create("CAPTURED_STACKS");
  private static final int MAX_STORED_STACKS = 1000;

  private final JavaMethodBreakpointProperties myProperties = new JavaMethodBreakpointProperties();

  public StackCapturingLineBreakpoint(DebugProcessImpl debugProcess, String className, String methodName) {
    super(debugProcess.getProject(), null);
    myDebugProcess = debugProcess;
    myProperties.EMULATED = true;
    myProperties.WATCH_EXIT = false;
    myProperties.myClassPattern = className;
    myProperties.myMethodName = methodName;
  }

  @NotNull
  @Override
  protected JavaMethodBreakpointProperties getProperties() {
    return myProperties;
  }

  @Override
  public String getSuspendPolicy() {
    return DebuggerSettings.SUSPEND_THREAD;
  }

  @Override
  public boolean processLocatableEvent(SuspendContextCommandImpl action, LocatableEvent event) throws EventProcessingException {
    try {
      SuspendContextImpl suspendContext = action.getSuspendContext();
      if (suspendContext != null) {
        Map<ObjectReference, List<StackFrameItem>> stacks = myDebugProcess.getUserData(CAPTURED_STACKS);
        if (stacks == null) {
          stacks = new LinkedHashMap<ObjectReference, List<StackFrameItem>>() {
            @Override
            protected boolean removeEldestEntry(Map.Entry eldest) {
              return size() > MAX_STORED_STACKS;
            }
          };
          myDebugProcess.putUserData(CAPTURED_STACKS, Collections.synchronizedMap(stacks));
        }
        Value key = ContainerUtil.getFirstItem(suspendContext.getFrameProxy().getArgumentValues());
        if (key instanceof ObjectReference) {
          stacks.put((ObjectReference)key, StackFrameItem.createFrames(suspendContext.getThread()));
        }
      }
    }
    catch (EvaluateException ignored) {
    }
    return false;
  }

  public static void track(DebugProcessImpl debugProcess, String classFqn, String methodName) {
    StackCapturingLineBreakpoint breakpoint = new StackCapturingLineBreakpoint(debugProcess, classFqn, methodName);
    breakpoint.createRequest(debugProcess);
  }
}
