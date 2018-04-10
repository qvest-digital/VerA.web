package de.tarent.commons.utils.converter;

import de.tarent.commons.utils.AbstractConverter;

import java.io.BufferedReader;
import java.sql.Clob;

public class ClobToString extends AbstractConverter {

	 public Class getTargetType() {
	        return String.class;
	    }

	    public Class getSourceType() {
	        return Clob.class;
	    }
	public Object doConversion(Object sourceData) throws Exception {
		if (sourceData == null)
	        return  "";

	      StringBuffer strOut = new StringBuffer();
	      String aux;

	      BufferedReader br = new BufferedReader(((Clob)sourceData).getCharacterStream());

	      while ((aux=br.readLine())!=null)
	             strOut.append(aux);

	      return strOut.toString();
	}

}
