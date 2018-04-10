package de.tarent.octopus.content.annotation;

import java.util.HashMap;

public class MyMapBean extends HashMap {
    /** serialVersionUID */
	private static final long serialVersionUID = -5267393785945151012L;

	public String getName() {
        return (String)get("name");
    }

    public void setName(String name) {
        put("name", name);
    }

    public String getCity() {
        return (String)get("city");
    }

    public void setCity(String city) {
        put("city", city);
    }
}
