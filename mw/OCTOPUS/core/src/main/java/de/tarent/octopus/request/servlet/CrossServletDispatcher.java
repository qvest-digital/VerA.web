package de.tarent.octopus.request.servlet;
import java.io.IOException;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import de.tarent.octopus.client.OctopusConnectionFactory;
import de.tarent.octopus.request.Octopus;

public class CrossServletDispatcher {
    public void dispatch(String webapp, String module, String task, Map parameters) {
        parameters.put("module", module);
        parameters.put("task", task);
        dispatch("/" + webapp + "/OCTOPUS/" + task, parameters);
    }

    public void dispatch(String url, Map parameters) {
        try {
            RequestDispatcher dispatcher = getRequestDispatcher(url);
            ServletRequest request = null;
            ServletResponse response = null;
            dispatcher.include(request, response);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static RequestDispatcher getRequestDispatcher(String url) {
        return getServletContext().getRequestDispatcher(url);
    }

    private static ServletContext getServletContext() {
        return getOctopusServletConfiguration().getServletContext();
    }

    private static ServletModuleLookup getOctopusServletConfiguration() {
        return (ServletModuleLookup) getOctopus().getModuleLookup();
    }

    private static Octopus getOctopus() {
        return OctopusConnectionFactory.getInstance().getInternalOctopusInstance();
    }
}
