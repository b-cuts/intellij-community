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
package org.jetbrains.jps.javac.ast.api;

import com.intellij.util.containers.SLRUCache;
import org.jetbrains.annotations.NotNull;

import javax.lang.model.element.Element;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

public class JavacNameTable {
  private final SLRUCache<Name, String> myParsedNameCache;
  private final Elements myElements;

  public JavacNameTable(Elements elements) {
    myParsedNameCache = new SLRUCache<Name, String>(1000, 1000) {
      @NotNull
      @Override
      public String createValue(Name key) {
        return key.toString();
      }
    };
    myElements = elements;
  }

  @NotNull
  public String parseName(Name name) {
    return myParsedNameCache.get(name);
  }

  @NotNull
  public String parseBinaryName(Element element) {
    return parseName(myElements.getBinaryName((TypeElement)element));
  }
}
