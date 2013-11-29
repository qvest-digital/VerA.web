package de.tarent.aa.veraweb.selenium;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;

import de.tarent.cucumber.datamanager.DataManager;
import de.tarent.cucumber.datamanager.entity.DataSet;
import de.tarent.cucumber.datamanager.entity.Field;

/**
 * Modified web driver with functionality for fill/check HTML fields.
 * 
 * @author Valentin But (v.but@tarent.de), tarent solutions GmbH
 * @version 1.0
 * 
 */
public class AdvancedWebDriver {

    /**
     * Log.
     */
    private static final Log LOG = LogFactory.getLog(AdvancedWebDriver.class);

    /**
     * HTML attribute 'value'.
     */
    public static final String HTML_ATTRIBUTE_VALUE = "value";

    /**
     * HTML tag 'option'.
     */
    public static final String HTML_TAG_OPTION = "option";

    /**
     * HTML type 'span'.
     */
    public static final String HTML_TYPE_SPAN = "span";

    /**
     * HTML type 'select'.
     */
    public static final String HTML_TYPE_SELECT = "select";

    /**
     * HTML type 'textarea'.
     */
    public static final String HTML_TYPE_TEXTAREA = "textarea";

    /**
     * HTML type 'checkbox'.
     */
    public static final String HTML_TYPE_CHECKBOX = "checkbox";

    /**
     * HTML type 'input'.
     */
    public static final String HTML_TYPE_INPUT = "input";

    /**
     * HTML type 'radio'.
     */
    public static final String HTML_TYPE_RADIO = "radio";

    /**
     * Injected {@link WebDriver}.
     */
    @Autowired
    private WebDriver browser;

    /**
     * Data manager.
     */
    private DataManager manager;

    /**
     * Constructor.
     * 
     * @param driver
     *            web driver
     * @throws Exception
     *             exception
     */
    public AdvancedWebDriver(WebDriver driver) throws Exception {
        this.browser = driver;
        this.manager = DataManager.getInstance();

        // ein mehrfaches aufrufen der init-methode hat keinerlei negative auswirkungen!
        this.manager.init();
    }

    /**
     * Check whether given data set exists.
     * 
     * @param dataSetName
     *            name of data set
     * @param instanceClass
     *            clazz
     */
    private void checkDataSet(String dataSetName, Class<?> instanceClass) {
        if (!manager.contains(dataSetName)) {
            throw new RuntimeException("Es wurde kein Datensatz mit den Namen '" + dataSetName + "' gefunden !");
        }

        if (!instanceClass.isInstance(manager.get(dataSetName))) {
            throw new RuntimeException("Der gefundene Datensatz (" + dataSetName + ") wird nicht unterstützt!");
        }
    }

    /**
     * Check whether page fields are specified correctly.
     * 
     * @param dataSetName
     *            name of data set
     * @return list of {@link Field}
     */
    public List<Field> checkPageFields(String dataSetName) {
        checkDataSet(dataSetName, DataSet.class);
        DataSet set = (DataSet) manager.get(dataSetName);

        List<Field> resultErrors = new ArrayList<Field>();
        for (Field field : set.getValues()) {
            if (field == null) {
                continue; // kann vorkommen, wenn Benutzer das komma hinter dem letzen eintrag nicht weggemacht hat!
            }

            if (field.getSkip() != null) {
                // mit diesem wert gekennzeichnete felder werden übersprungen!
                printSkipMessage(field);
                continue;
            }

            if (field.getType() == null) {
                throw new RuntimeException("Ungültiger Typ des Feldes '" + field.getLabel() + " (" + field.getId()
                        + ")'!");
            }
            // check span
            if (field.getType().equalsIgnoreCase(HTML_TYPE_SPAN)) {
                if (!checkSpan(field)) {
                    resultErrors.add(field);
                }
                // check radio,checkbox,input
            } else if (field.getType().equalsIgnoreCase(HTML_TYPE_CHECKBOX)
                    || field.getType().equalsIgnoreCase(HTML_TYPE_INPUT)
                    || field.getType().equalsIgnoreCase(HTML_TYPE_RADIO)) {

                if (!checkInput(field)) {
                    resultErrors.add(field);
                }
            } else if (field.getType().equalsIgnoreCase(HTML_TYPE_TEXTAREA)) {
                if (!checkTextarea(field)) {
                    resultErrors.add(field);
                }
            } else if (field.getType().equalsIgnoreCase(HTML_TYPE_SELECT)) {
                if (!checkSelect(field)) {
                    resultErrors.add(field);
                }
            } else {
                throw new RuntimeException("Ungültiger Typ des Feldes '" + field.getLabel() + " (" + field.getId()
                        + ")'!");
            }
        }

        return resultErrors;
    }

    /**
     * Print skip message.
     * 
     * @param field
     *            field
     */
    private void printSkipMessage(Field field) {
        LOG.warn("[WARN]Das Datum " + field.getLabel() + "(" + field.getId() + ") wird nicht abgetestet, "
                + "da es als >Skip< markiert wurde. Der genannte Grund: " + field.getSkip());
    }

    /**
     * Fill page fields.
     * 
     * @param dataSetName
     *            name of data set
     */
    public void fillPageFields(String dataSetName) {
        checkDataSet(dataSetName, DataSet.class);
        fillPageFields((DataSet) manager.get(dataSetName));
    }

    /**
     * Check required fields.
     * 
     * @param dataSetName
     *            name of data set
     * @return list of fields not found
     */
    public List<Field> checkRequiredFields(String dataSetName) {
        checkDataSet(dataSetName, DataSet.class);

        DataSet set = (DataSet) manager.get(dataSetName);
        List<Field> fields = new ArrayList<Field>();
        for (Field field : set.getValues()) {
            if (field == null) {
                continue; // kann vorkommen, wenn Benutzer das komma hinter dem letzen eintrag nicht weggemacht hat!
            }

            if (field.getType() == null || field.getType().equalsIgnoreCase(HTML_TYPE_CHECKBOX)
                    || field.getType().equalsIgnoreCase(HTML_TYPE_INPUT)
                    || field.getType().equalsIgnoreCase(HTML_TYPE_RADIO)
                    || field.getType().equalsIgnoreCase(HTML_TYPE_SELECT)
                    || field.getType().equalsIgnoreCase(HTML_TYPE_TEXTAREA)) {

                try {
                    browser.findElement(By.xpath("//*[@id='" + field.getId() + "' and @class='error']"));
                } catch (NoSuchElementException e) {
                    fields.add(field);
                }
            }
        }

        return fields;
    }

    /**
     * Fill page fields with given data.
     * 
     * @param set
     *            data set
     */
    private void fillPageFields(DataSet set) {
        for (Field field : set.getValues()) {
            if (field == null) {
                continue; // kann vorkommen, wenn Benutzer das komma hinter dem letzen eintrag nicht weggemacht hat!
            }

            if (field.getSkip() != null) {
                // mit diesem wert gekennzeichnete felder werden übersprungen!
                printSkipMessage(field);
                continue;
            }

            if (field.getType().equalsIgnoreCase(HTML_TYPE_CHECKBOX)) {
                setCheckboxValue(field);
            } else if (field.getType().equalsIgnoreCase(HTML_TYPE_INPUT)) {
                setInputValue(field);
            } else if (field.getType().equalsIgnoreCase(HTML_TYPE_TEXTAREA)) {
                setTextareaValue(field);
            } else if (field.getType().equalsIgnoreCase(HTML_TYPE_SELECT)) {
                setSelectValue(field);
            } else if (field.getType().equalsIgnoreCase(HTML_TYPE_RADIO)) {
                setRadioValue(field);
            }
        }
    }

    /**
     * Set value for checkbox element.
     * 
     * @param field
     *            checkbox element
     */
    private void setCheckboxValue(Field field) {
        WebElement element = null;

        if (field.getId() != null) {
            element = browser.findElement(By.id(field.getId()));
        } else if (field.getXpath() != null) {
            element = browser.findElement(By.xpath(field.getXpath()));
        } else {
            // TODO: Implements Label referencing
            throw new RuntimeException("Kann das angegebene Feld nicht befüllen! " + field);
        }

        if ("false".equals(field.getValue())) {
            if (element.isSelected()) {
                element.click();
            }
        } else {
            // we want to select it (if is always selected then do nothing)
            if (!element.isSelected()) {
                element.click();
            }
        }
    }

    /**
     * Set value for input element.
     * 
     * @param field
     *            input element
     */
    private void setInputValue(Field field) {
        WebElement element = null;

        if (field.getId() != null) {
            element = browser.findElement(By.id(field.getId()));
        } else if (field.getXpath() != null) {
            element = browser.findElement(By.xpath(field.getXpath()));
        } else {
            // TODO:Implements Label referencing
            throw new RuntimeException("Kann das angegebene Feld nicht befüllen! " + field);
        }

        element.clear();
        element.sendKeys(field.getValue());
    }

    /**
     * Set value for text area element.
     * 
     * @param field
     *            text area element
     */
    private void setTextareaValue(Field field) {
        WebElement element = null;

        if (field.getId() != null) {
            element = browser.findElement(By.id(field.getId()));
        } else if (field.getXpath() != null) {
            element = browser.findElement(By.xpath(field.getXpath()));
        } else {
            // TODO:Implements Label referencing
            throw new RuntimeException("Kann das angegebene Feld nicht befüllen! " + field);
        }

        element.clear();
        element.sendKeys(field.getValue());
    }

    /**
     * Set value for select element.
     * 
     * @param field
     *            select element
     */
    private void setSelectValue(Field field) {
        if (field.getValue() == null || field.getValue().equals("")) {
            // do not select anything
            return;
        }

        List<WebElement> options;
        if (field.getId() != null) {
            options = browser.findElement(By.id(field.getId())).findElements(By.tagName(HTML_TAG_OPTION));
        } else if (field.getXpath() != null) {
            options = browser.findElement(By.xpath(field.getXpath())).findElements(By.tagName(HTML_TAG_OPTION));
        } else {
            // TODO:Implements Label referencing
            throw new RuntimeException("Kann das angegebene Feld nicht befüllen! " + field);
        }

        for (WebElement option : options) {
            String valueAttribute = option.getAttribute(HTML_ATTRIBUTE_VALUE);

            if (option.getText().equals(field.getValue())
                    || (valueAttribute != null && valueAttribute.equals(field.getValue()))) {

                option.click();
                break; // we found it!
            }
        }
    }

    /**
     * Set value for radio button element.
     * 
     * @param field
     *            radio button element
     */
    private void setRadioValue(Field field) {
        WebElement element = null;

        if (field.getId() != null) {
            element = browser.findElement(By.id(field.getId()));
        } else if (field.getXpath() != null) {
            element = browser.findElement(By.xpath(field.getXpath()));
        } else {
            // TODO:Implements Label referencing
            throw new RuntimeException("Kann das angegebene Feld nicht befüllen! " + field);
        }

        if (element.isDisplayed()) {
            element.click();
        } else {
            if (browser instanceof JavascriptExecutor) {
                ((JavascriptExecutor) browser).executeScript("arguments[0].checked = true;", element);
            } else {
                element.click();
            }
        }
    }

    /**
     * Check value of span element.
     * 
     * @param field
     *            span element
     * @return <code>true</code> if given value equals expected value from field, otherwise <code>false</code>
     */
    private boolean checkSpan(Field field) {
        String expectedValue = field.getValue();
        String givenValue = null;

        if (field.getId() != null) {
            givenValue = browser.findElement(By.id(field.getId())).getText();
        } else if (field.getXpath() != null) {
            givenValue = browser.findElement(By.xpath(field.getXpath())).getText();
        } else {
            // TODO:Implements Label referencing
            return false;
        }

        field.set_value(givenValue);
        if (expectedValue == null) {
            return givenValue == null || "".equals(givenValue);
        }

        return expectedValue.equals(givenValue);
    }

    /**
     * Check value of input element.
     * 
     * @param field
     *            input element
     * @return <code>true</code> if given value equals expected value from field, otherwise <code>false</code>
     */
    private boolean checkInput(Field field) {
        WebElement element = null;
        if (field.getId() != null) {
            element = browser.findElement(By.id(field.getId()));
        } else if (field.getXpath() != null) {
            element = browser.findElement(By.xpath(field.getXpath()));
        } else {
            // TODO:Implements Label referencing
            return false;
        }

        if ("true".equals(field.getValue()) || "false".equals(field.getValue())) {

            Boolean shouldSelected = Boolean.parseBoolean(field.getValue());
            return shouldSelected == element.isSelected();
        } else {
            String givenValue = element.getAttribute(HTML_ATTRIBUTE_VALUE);

            return field.getValue().equals(givenValue);
        }
    }

    /**
     * Check value of text area element.
     * 
     * @param field
     *            span element
     * @return <code>true</code> if given value equals expected value from field, otherwise <code>false</code>
     */
    private boolean checkTextarea(Field field) {
        String expectedValue = field.getValue();
        String givenValue = null;

        if (field.getId() != null) {
            givenValue = browser.findElement(By.id(field.getId())).getAttribute(HTML_ATTRIBUTE_VALUE);
        } else if (field.getXpath() != null) {
            givenValue = browser.findElement(By.xpath(field.getXpath())).getAttribute(HTML_ATTRIBUTE_VALUE);
        } else {
            // TODO:Implements Label referencing
            return false;
        }

        field.set_value(givenValue);
        if (expectedValue == null)
            return givenValue == null || givenValue.equals("");

        return expectedValue.equals(givenValue);
    }

    /**
     * Check value of select element.
     * 
     * @param field
     *            span element
     * @return <code>true</code> if given value equals expected value from field, otherwise <code>false</code>
     */
    private boolean checkSelect(Field field) {
        String expectedValue = field.getValue();
        String givenValue = null;

        List<WebElement> options;
        if (field.getId() != null) {
            options = browser.findElement(By.id(field.getId())).findElements(By.tagName(HTML_TAG_OPTION));
        } else if (field.getXpath() != null) {
            options = browser.findElement(By.xpath(field.getXpath())).findElements(By.tagName(HTML_TAG_OPTION));
        } else {
            // TODO:Implements Label referencing
            return false;
        }

        for (WebElement option : options) {
            if (option.isSelected()) {
                givenValue = option.getText();
                break;
            }
        }

        field.set_value(givenValue);
        if (expectedValue == null) {
            return givenValue == null || "".equals(givenValue);
        }

        return expectedValue.equals(givenValue);
    }

}
