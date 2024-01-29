/*
 * Copyright 2024 the original author or authors.
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

package org.gradle.internal.component.resolution.failure.describer;

import org.gradle.api.internal.DocumentationRegistry;
import org.gradle.internal.component.IncompatibleArtifactVariantsException;
import org.gradle.internal.component.resolution.failure.ResolutionCandidateAssessor.AssessedCandidate;
import org.gradle.internal.component.resolution.failure.failures.InvalidMultipleVariantsSelectionFailure2;

import javax.inject.Inject;

public class InvalidMultipleVariantsFailureDescriber2 extends AbstractResolutionFailureDescriber2<IncompatibleArtifactVariantsException, InvalidMultipleVariantsSelectionFailure2> {
    private static final String INCOMPATIBLE_VARIANTS_PREFIX = "Incompatible variant errors are explained in more detail at ";
    private static final String INCOMPATIBLE_VARIANTS_SECTION = "sub:variant-incompatible";

    @Inject
    public InvalidMultipleVariantsFailureDescriber2(DocumentationRegistry documentationRegistry) {
        super(documentationRegistry);
    }

    @Override
    public Class<InvalidMultipleVariantsSelectionFailure2> getDescribedFailureType() {
        return InvalidMultipleVariantsSelectionFailure2.class;
    }

    @Override
    public IncompatibleArtifactVariantsException describeFailure(InvalidMultipleVariantsSelectionFailure2 failure) {
        String msg = buildIncompatibleArtifactVariantsFailureMsg(failure);
        IncompatibleArtifactVariantsException result = new IncompatibleArtifactVariantsException(msg);
        suggestSpecificDocumentation(result, INCOMPATIBLE_VARIANTS_PREFIX, INCOMPATIBLE_VARIANTS_SECTION);
        suggestReviewAlgorithm(result);
        return result;
    }

    private String buildIncompatibleArtifactVariantsFailureMsg(InvalidMultipleVariantsSelectionFailure2 failure) {
        StringBuilder sb = new StringBuilder("Multiple incompatible variants of ")
            .append(failure.getRequestedName())
            .append(" were selected:\n");
        for (AssessedCandidate assessedCandidate : failure.getAssessedCandidates()) {
            sb.append("   - Variant ").append(assessedCandidate.getDisplayName()).append(" has attributes ");
            formatAttributes(sb, assessedCandidate.getAllCandidateAttributes());
            sb.append("\n");
        }
        return sb.toString();
    }
}
