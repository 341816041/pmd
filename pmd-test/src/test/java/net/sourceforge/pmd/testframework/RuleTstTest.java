/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.testframework;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.Test;
import org.mockito.Mockito;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.RuleTargetSelector;
import net.sourceforge.pmd.test.lang.DummyLanguageModule.DummyRootNode;

public class RuleTstTest {
    private LanguageVersion dummyLanguage = LanguageRegistry.findLanguageByTerseName("dummy").getDefaultVersion();

    private Rule rule = mock(Rule.class);

    private RuleTst ruleTester = new RuleTst() {
    };

    @Test
    public void shouldCallStartAndEnd() {
        when(rule.getLanguage()).thenReturn(dummyLanguage.getLanguage());
        when(rule.getName()).thenReturn("test rule");
        when(rule.getTargetSelector()).thenReturn(RuleTargetSelector.forRootOnly());
        when(rule.deepCopy()).thenReturn(rule);

        ruleTester.runTestFromString("the code", rule, dummyLanguage, false);

        verify(rule).start(any(RuleContext.class));
        verify(rule).end(any(RuleContext.class));
        verify(rule).getLanguage();
        verify(rule, times(2)).getTargetSelector();
        verify(rule).getMinimumLanguageVersion();
        verify(rule).getMaximumLanguageVersion();
        verify(rule).apply(any(Node.class), any(RuleContext.class));
        verify(rule, times(4)).getName();
        verify(rule).getPropertiesByPropertyDescriptor();
    }

    @Test
    public void shouldAssertLinenumbersSorted() {
        when(rule.getLanguage()).thenReturn(dummyLanguage.getLanguage());
        when(rule.getName()).thenReturn("test rule");
        when(rule.getMessage()).thenReturn("test rule");
        when(rule.getTargetSelector()).thenReturn(RuleTargetSelector.forRootOnly());
        when(rule.deepCopy()).thenReturn(rule);

        Mockito.doAnswer(invocation -> {
            RuleContext context = invocation.getArgument(1, RuleContext.class);
            // the violations are reported out of order
            context.addViolation(new DummyRootNode().withCoords(15, 1, 15, 5));
            context.addViolation(new DummyRootNode().withCoords(1, 1, 2, 5));
            return null;
        }).when(rule).apply(any(Node.class), Mockito.any(RuleContext.class));

        TestDescriptor testDescriptor = new TestDescriptor("the code", "sample test", 2, rule, dummyLanguage);
        testDescriptor.setReinitializeRule(false);
        testDescriptor.setExpectedLineNumbers(Arrays.asList(1, 15));

        ruleTester.runTest(testDescriptor);
    }
}
