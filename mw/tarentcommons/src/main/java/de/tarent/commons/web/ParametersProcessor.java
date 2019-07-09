package de.tarent.commons.web;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Contains a method for encoding a url with many parameters objects.
 *
 * @author Tim Steffens
 */
public class ParametersProcessor {

    List commonParametersList;

    public ParametersProcessor() {
        commonParametersList = new LinkedList();
    }

    /**
     * adds {@code parameters} to the list of common parameters
     *
     * @param parameters
     */
    public void addCommonParameters(Parameters parameters) {
        if (parameters != null) {
            commonParametersList.add(parameters);
        }
    }

    public String encodeUrl(String url) throws UnsupportedEncodingException {
        return encodeUrl(url, null);
    }

    /**
     * Attaches the common parameters and those in {@code parametersList}.
     */
    public String encodeUrl(String url, Parameters parameters) throws UnsupportedEncodingException {

        String newUrl =
          url == null
            ? ""
            : url;
        if (parameters != null) {
            newUrl = parameters.encodeUrl(newUrl);
        }

        Iterator parametersIt = commonParametersList.iterator();
        while (parametersIt.hasNext()) {
            Parameters commonParameters = (Parameters) parametersIt.next();
            newUrl = commonParameters.encodeUrl(newUrl);
        }

        return newUrl;
    }
}
