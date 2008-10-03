/*
 * Copyright 2000-2008 JetBrains s.r.o.
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
package git4idea.commands;

import java.util.EventListener;

/**
 * Listener for event common for all handlers
 */
public interface GitHandlerListener extends EventListener {
  /**
   * This method is invoked when git process is terminated
   *
   * @param exitCode a exit code
   */
  void processTerminted(int exitCode);

  /**
   * This method is invoked if starting git process failed with exeption
   *
   * @param exception an exeption
   */
  void startFailed(Throwable exception);

}
