package de.tarent.aa.veraweb.cucumber.formatter;

import gherkin.formatter.PrettyFormatter;
import gherkin.formatter.model.Feature;
import gherkin.formatter.model.Scenario;
import gherkin.formatter.model.Step;

/**
 * This class is a workaround for a missing hook! Because the hook which is called after each step doesn't provide
 * information about the current feature or current scenario and so on. This class represents a formatter that we refer
 * to the cucumber-cli.
 * 
 * @author Sven Schumann <s.schumann@tarent.de>
 * @version 1.0
 * 
 */
public class RuntimeInfoCatcher extends PrettyFormatter {

    /**
     * System property 'tarent.cucumber.colored'.
     */
    public static final String COLORED_OUTPUT_SYSTEM_PROPERTY = "tarent.cucumber.colored";

    /**
     * {@link RuntimeInfoCatcher} instance.
     */
    private static RuntimeInfoCatcher instance;

    /**
     * Current feature.
     */
    private Feature currentFeature;

    /**
     * Current scenario.
     */
    private Scenario currentScenario;

    /**
     * Current step.
     */
    private Step currentStep;

    /**
     * Current scenario count.
     */
    private int currentScenarioCount = 0;

    /**
     * Current step count.
     */
    private int currentStepCount = 0;

    /**
     * Constructor.
     * 
     * @param appendable
     *            appendable
     * @param monochrome
     *            monochrome
     * @param executing
     *            executing
     */
    public RuntimeInfoCatcher(Appendable appendable, boolean monochrome, boolean executing) {
        super(appendable,
                !Boolean.valueOf(System.getProperty(COLORED_OUTPUT_SYSTEM_PROPERTY, Boolean.TRUE.toString())),
                executing);
        instance = this;
    }

    /**
     * Constructor.
     * 
     * @param appendable
     *            appendable
     */
    public RuntimeInfoCatcher(Appendable appendable) {
        super(appendable,
                !Boolean.valueOf(System.getProperty(COLORED_OUTPUT_SYSTEM_PROPERTY, Boolean.TRUE.toString())), true);
        instance = this;
    }

    public static RuntimeInfoCatcher getInstance() {
        return instance;
    }

    @Override
    public void feature(Feature feature) {
        super.feature(feature);

        if (!feature.equals(currentFeature)) {
            currentScenarioCount = 0;
            currentStepCount = 0;
        }

        this.currentFeature = feature;
    }

    @Override
    public void scenario(Scenario scenario) {
        super.scenario(scenario);

        if (!scenario.equals(currentScenario)) {
            currentScenarioCount++;
            currentStepCount = 0;
        }

        this.currentScenario = scenario;
    }

    @Override
    public void step(Step step) {
        super.step(step);

        if (!step.equals(currentStep)) {
            currentStepCount++;
        }

        this.currentStep = step;
    }

    public Feature getCurrentFeature() {
        return currentFeature;
    }

    public Scenario getCurrentScenario() {
        return currentScenario;
    }

    public Step getCurrentStep() {
        return currentStep;
    }

    public int getCurrentScenarioCount() {
        return currentScenarioCount;
    }

    public int getCurrentStepCount() {
        return currentStepCount;
    }
}
