package de.tarent.octopus.request;

public interface RequestHeader {
	
	/**
	 * Returns requested header information defined by constants available in this interface or the RequestHeader implementation
	 * 
	 * @param headerType constant indentifier for the requested header information
	 * @return header as <code>Object</code>
	 */
	public Object getObject(String headerType);
	
	/**
	 * Returns requested header information defined by constants available in this interface or the RequestHeader implementation
	 * 
	 * @param headerType constant indentifier for the requested header information
	 * @return header as <code>String</code>
	 */
	public String getString(String headerType);

}
