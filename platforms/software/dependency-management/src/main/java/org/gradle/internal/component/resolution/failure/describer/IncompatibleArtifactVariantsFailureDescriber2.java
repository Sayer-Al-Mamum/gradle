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
import org.gradle.api.internal.attributes.AttributeDescriber;
import org.gradle.internal.component.NoMatchingArtifactVariantsException;
import org.gradle.internal.component.model.AttributeDescriberSelector;
import org.gradle.internal.component.resolution.failure.ResolutionCandidateAssessor.AssessedCandidate;
import org.gradle.internal.component.resolution.failure.failures.IncompatibleResolutionFailure2;
import org.gradle.internal.logging.text.StyledTextOutput;
import org.gradle.internal.logging.text.TreeFormatter;

import javax.inject.Inject;

import static org.gradle.internal.exceptions.StyledException.style;

public class IncompatibleArtifactVariantsFailureDescriber2 extends AbstractResolutionFailureDescriber2<NoMatchingArtifactVariantsException, IncompatibleResolutionFailure2> {
    private static final String NO_MATCHING_VARIANTS_PREFIX = "No matching variant errors are explained in more detail at ";
    private static final String NO_MATCHING_VARIANTS_SECTION = "sub:variant-no-match";

    @Inject
    public IncompatibleArtifactVariantsFailureDescriber2(DocumentationRegistry documentationRegistry) {
        super(documentationRegistry);
    }

    @Override
    public Class<IncompatibleResolutionFailure2> getDescribedFailureType() {
        return IncompatibleResolutionFailure2.class;
    }

    @Override
    public NoMatchingArtifactVariantsException describeFailure(IncompatibleResolutionFailure2 failure) {
        String msg = buildIncompatibleArtifactVariantsFailureMsg(failure);
        NoMatchingArtifactVariantsException result = new NoMatchingArtifactVariantsException(msg);
        suggestSpecificDocumentation(result, NO_MATCHING_VARIANTS_PREFIX, NO_MATCHING_VARIANTS_SECTION);
        suggestReviewAlgorithm(result);
        return result;
    }

    private String buildIncompatibleArtifactVariantsFailureMsg(IncompatibleResolutionFailure2 failure) {
        AttributeDescriber describer = AttributeDescriberSelector.selectDescriber(failure.getRequestedAttributes(), failure.getSchema());
        TreeFormatter formatter = new TreeFormatter();
        formatter.node("No variants of " + style(StyledTextOutput.Style.Info, failure.getRequestedName()) + " match the consumer attributes");
        formatter.startChildren();
        for (AssessedCandidate assessedCandidate : failure.getCandidates()) {
            formatter.node(assessedCandidate.getDisplayName());
            formatAttributeMatchesForIncompatibility(assessedCandidate, formatter, describer);
        }
        formatter.endChildren();
        return formatter.toString();
    }
}
