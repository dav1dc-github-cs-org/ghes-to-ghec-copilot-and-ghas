package com.example.flightsim.scenario;

import java.util.Map;
import org.apache.commons.text.StringSubstitutor;

/**
 * Fills the instructor's briefing template, for example
 * {@code "Brief ${trainee} on ${scenario} before engine start."}.
 */
public final class BriefingTemplate {

    private final String template;

    /**
     * Creates a template.
     *
     * @param template text with {@code ${name}} placeholders
     */
    public BriefingTemplate(String template) {
        this.template = template;
    }

    /**
     * Replaces the placeholders. Unknown placeholders are left as they are so a missing value is
     * visible in the briefing rather than silently blank.
     *
     * @param values placeholder values
     * @return filled-in text
     */
    public String render(Map<String, String> values) {
        StringSubstitutor substitutor = new StringSubstitutor(values);
        substitutor.setEnableUndefinedVariableException(false);
        return substitutor.replace(template);
    }
}
