/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.language.base.internal;

import org.gradle.api.internal.file.SourceDirectorySetFactory;
import org.gradle.internal.Cast;
import org.gradle.language.base.LanguageSourceSet;
import org.gradle.language.base.sources.BaseLanguageSourceSet;
import org.gradle.model.internal.core.BaseInstanceFactory;
import org.gradle.model.internal.core.MutableModelNode;
import org.gradle.model.internal.type.ModelType;
import org.gradle.platform.base.binary.BaseBinarySpec;
import org.gradle.platform.base.internal.ComponentSpecInternal;

public class LanguageSourceSetFactory extends BaseInstanceFactory<LanguageSourceSet, BaseLanguageSourceSet> {

    private final SourceDirectorySetFactory sourceDirectorySetFactory;

    public LanguageSourceSetFactory(String displayName, SourceDirectorySetFactory sourceDirectorySetFactory) {
        super(displayName, LanguageSourceSet.class, BaseLanguageSourceSet.class);
        this.sourceDirectorySetFactory = sourceDirectorySetFactory;
    }

    @Override
    protected <S extends LanguageSourceSet> ImplementationFactory<S> forType(ModelType<S> publicType, final ModelType<? extends BaseLanguageSourceSet> implementationType) {
        return new ImplementationFactory<S>() {
            @Override
            public S create(ModelType<? extends S> publicType, String sourceSetName, MutableModelNode modelNode) {
                return Cast.uncheckedCast(BaseLanguageSourceSet.create(publicType.getConcreteClass(), implementationType.getConcreteClass(), sourceSetName, determineParentName(modelNode), sourceDirectorySetFactory));
            }
        };
    }

    private String determineParentName(MutableModelNode modelNode) {
        MutableModelNode grandparentNode = modelNode.getParent().getParent();
        // Special case handling of a LanguageSourceSet that is part of `ComponentSpec.sources` or `BinarySpec.sources`
        if (grandparentNode != null) {
            if (grandparentNode.getPrivateData() instanceof BaseBinarySpec) {
                // TODO:DAZ Should be using binary.projectScopedName for uniqueness
                BaseBinarySpec binarySpecInternal = (BaseBinarySpec) grandparentNode.getPrivateData();
                return binarySpecInternal.getComponent() == null ? binarySpecInternal.getName() : binarySpecInternal.getComponent().getName();
            }
            if (grandparentNode.getPrivateData() instanceof ComponentSpecInternal) {
                return ((ComponentSpecInternal) grandparentNode.getPrivateData()).getName();
            }
        }

        return modelNode.getParent().getPath().getName();
    }


}
