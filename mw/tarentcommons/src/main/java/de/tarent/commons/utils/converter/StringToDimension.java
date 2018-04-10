package de.tarent.commons.utils.converter;

import java.awt.Dimension;

import de.tarent.commons.utils.AbstractConverter;

/**
 * Converts a String to a Dimension.
 */
public class StringToDimension extends AbstractConverter {

	public Class getSourceType() {
        return String.class;
    }

	public Class getTargetType() {
        return Dimension.class;
    }

	public Object doConversion(Object sourceData) throws Exception {
		String value = ((String)sourceData);
		int i = value.indexOf(',');
		int width = Integer.parseInt(value.subSequence(0, i).toString());
		while (value.charAt(i+1) == ' ')
			i++;
		int heigth = Integer.parseInt(value.subSequence(i+1, value.length()).toString());
		return new Dimension(width, heigth);
	}

}
