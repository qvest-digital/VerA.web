package de.tarent.octopus.request;

public interface RequestHeader {
	
	/**
	 * Returns requested header information defined by constants available in this interface or the RequestHeader implementation
	 * 
	 * @param headerInformationType constant indentifier for the requested header information
	 * @return header information <code>Object</code>
	 */
	public Object getHeaderInformationObject(String headerInformationType);
	
	/**
	 * Returns requested header information defined by constants available in this interface or the RequestHeader implementation
	 * 
	 * @param headerInformationType constant indentifier for the requested header information
	 * @return header information <code>Object</code>
	 */
	public String getHeaderInformationString(String headerInformationType);

}
