package de.tarent.aa.veraweb.cucumber.stepdefinitions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Map;
import java.util.Map.Entry;

import org.joda.time.DateMidnight;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.springframework.beans.factory.annotation.Autowired;

import de.tarent.aa.veraweb.cucumber.env.GlobalConfig;
import de.tarent.aa.veraweb.cucumber.env.Utils;
import de.tarent.aa.veraweb.cucumber.pagedefinitions.ElementDefinition;
import de.tarent.aa.veraweb.cucumber.pagedefinitions.HtmlType;
import de.tarent.aa.veraweb.cucumber.pagedefinitions.PageDefinition;
import de.tarent.aa.veraweb.cucumber.utils.NameUtil;
import de.tarent.aa.veraweb.selenium.AdvancedWebDriver;

/**
 * Super class for all {@link AbstractStepDefinitions}.
 * 
 * @author Michael Kutz, tarent Solutions GmbH
 * @author Valentin But (v.but@tarent.de), tarent solutions GmbH
 */
public abstract class AbstractStepDefinitions {

    protected static PageDefinition page;

    /**
     * Injected {@link GlobalConfig}.
     */
    @Autowired
    protected GlobalConfig config;

    /**
     * Injected {@link AdvancedWebDriver}.
     */
    @Autowired
    protected AdvancedWebDriver advancedDriver;

    /**
     * Injected {@link AdvancedWebDriver}.
     */
    @Autowired
    protected WebDriver driver;

    /**
     * Enters the data set with given {@code name} into correspponding fields.
     * 
     * @param name
     *            name of the data set
     */
    protected void whenFillFields(String name) {
        advancedDriver.fillPageFields(NameUtil.nameToEnumName(name));
    }
    
    protected void whenFillFields(Map<String, ElementDefinition> data) {
        for (Entry<String, ElementDefinition> entry : data.entrySet()) {
            whenFillField(entry.getKey(), entry.getValue());
        }
        
    }

    /**
     * Enters the given {@code value} into the given {@code elementDefinition}.
     * 
     * @param elementDefinition
     *            the {@link ElementDefinition} of the field that should be filled.
     * @param value
     *            the {@link String} the field should be filled with.
     */
    protected void whenFillField(String value, ElementDefinition elementDefinition) {
        WebElement element = findPageElement(elementDefinition);
        if (elementDefinition.type == HtmlType.SELECT){
        	new Select(element).selectByVisibleText(value);
        } else {
        	element.clear();
            element.sendKeys(value);
        }        
    }

    /**
     * Clears the given {@code elementDefinition}.
     * 
     * @param elementDefinition
     *            the {@link ElementDefinition} of the field that should be cleared.
     */
    protected void whenNotFillField(ElementDefinition elementDefinition) {
        findPageElement(elementDefinition).clear();
    }

    /**
     * Enters the given {@code date} into the given {@code elementDefinition}.
     * 
     * @param elementDefinition
     *            the {@link ElementDefinition} of the field that should be filled.
     * @param date
     *            the {@link DateMidnight} the field should be filled with.
     */
    protected void whenFillField(DateMidnight date, ElementDefinition elementDefinition) {
        whenFillField(date.toString(Utils.DEFAULT_DATETIME_FORMATTER), elementDefinition);
    }

    /**
     * Fails if the current page does not match the given {@code pageDefinition}, sets the {@link #page} otherwise.
     * 
     * @param pageDefinition
     *            the {@link PageDefinition} that should match the current page.
     */
    protected void thenAtPage(PageDefinition pageDefinition) {
        assertTrue(pageDefinition.url == null || driver.getCurrentUrl().endsWith(pageDefinition.url));
        thenElementsPresent(pageDefinition.elements);
        page = pageDefinition;
    }

    /**
     * Will {@link #fail()} if the given {@code elementDefinition} can not be found on the current page.
     * 
     * @param elementDefinition
     *            the {@link ElementDefinition} of the required element.
     */
    protected void thenElementPresent(ElementDefinition elementDefinition) {
        try {
            findPageElement(elementDefinition);
        } catch (NoSuchElementException e) {
            throw new IllegalStateException(String.format(
                    "Required element \"%1$s\" cound not be found by %2$s at \"%3$s\".", elementDefinition,
                    elementDefinition.by, driver.getCurrentUrl()), e);
        }
    }

    /**
     * Will {@link #fail()} if the given {@code elementDefinition} can be found on the current page.
     * 
     * @param elementDefinition
     *            the {@link ElementDefinition} of the element that should not be present.
     */
    protected void thenElementNotPresent(ElementDefinition elementDefinition) {
        assertTrue(driver.findElements(elementDefinition.by).isEmpty());
    }

    /**
     * Will {@link #fail()} if not all given {@code elementDefinitions} can be found on the current page.
     * 
     * @param elementDefinitions
     *            {@link ElementDefinition}s of all required elements.
     */
    protected void thenElementsPresent(ElementDefinition... elementDefinitions) {
        boolean allRequiredFound = true;
        StringBuilder messageBuilder = new StringBuilder();
        for (ElementDefinition elementDefinition : elementDefinitions) {
            if (elementDefinition.required) {
                try {
                    findPageElement(elementDefinition);
                } catch (NoSuchElementException e) {
                    messageBuilder.append(String.format("\n - \"%1$s\" %2$s.", elementDefinition, elementDefinition.by));
                    allRequiredFound = false;
                }
            }
        }

        if (!allRequiredFound) {
            new IllegalStateException(String.format("Not all required elements were found at %1$s: %2$s",
                    driver.getCurrentUrl(), messageBuilder.toString()));
        }
    }

    /**
     * Navigates the {@link #driver} to the given {@link PageDefinition#url} and sets {@link #page}.
     * 
     * @param pageDefinition
     *            the {@link PageDefinition} of the page to navigate to.
     */
    protected void whenNavigateToPage(PageDefinition pageDefinition) {
        if (pageDefinition.url == null) {
            throw new IllegalArgumentException("The given pageDefinition " + pageDefinition
                    + " has no URL an can therefore not be accessed directly!");
        }
        driver.get(config.getVerawebBaseUrl() + pageDefinition.url);

        page = pageDefinition;
    }

    /**
     * Clicks the given {@code elementDefinition}.
     * 
     * @param elementDefinition
     *            the {@link ElementDefinition} that should be clicked.
     */
    protected void whenClickElement(ElementDefinition elementDefinition) {
        findPageElement(elementDefinition).click();
        if (elementDefinition.nextPageDefinition != null) {
            page = elementDefinition.nextPageDefinition;
        }
    }

    /**
     * Fails unless the given {@code elementDefinition}'s text equals the given {@code expectedText}.
     * 
     * @param elementDefinition
     *            the {@link ElementDefinition} whose text should be equal to the given {@code expectedText}.
     * @param expectedText
     *            the expected text.
     */
    protected void thenElementTextEquals(ElementDefinition elementDefinition, String expectedText) {
        String acturalText = findPageElement(elementDefinition).getText();
        assertEquals(expectedText, acturalText);
    }

    /**
     * Fails unless the given {@code elementDefinition}'s text matches the given {@code expression}.
     * 
     * @param elementDefinition
     *            the {@link ElementDefinition} whose text should match the given {@code expression}.
     * @param expression
     *            the regular expression that should be matched by the element's text.
     */
    protected void thenElementTextMatches(ElementDefinition elementDefinition, String expression) {
        String acturalText = findPageElement(elementDefinition).getText();
        assertTrue(acturalText.matches(expression));
    }

    /**
     * Returns the {@link WebElement} defined by the given {@link ElementDefinition}.
     * 
     * @param elementDefinition
     *            the {@link ElementDefinition} to look for.
     * @return the {@link WebElement} for the given {@link ElementDefinition}.
     */
    private WebElement findPageElement(ElementDefinition elementDefinition) {
        return driver.findElement(elementDefinition.by);
    }
}
