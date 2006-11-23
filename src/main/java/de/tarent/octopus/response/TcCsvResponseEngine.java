/* $Id: TcCsvResponseEngine.java,v 1.2 2006/11/23 15:45:02 kleinhenz Exp $
 * 
 * tarent-octopus, Webservice Data Integrator and Applicationserver
 * Copyright (C) 2002 tarent GmbH
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 * tarent GmbH., hereby disclaims all copyright
 * interest in the program 'tarent-octopus'
 * (which makes passes at compilers) written
 * by Christoph Jerolimov.
 * signature of Elmar Geese, 1 June 2002
 * Elmar Geese, CEO tarent GmbH
 */

package de.tarent.octopus.response;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.tarent.octopus.config.TcCommonConfig;
import de.tarent.octopus.config.TcConfig;
import de.tarent.octopus.config.TcModuleConfig;
import de.tarent.octopus.content.TcContent;
import de.tarent.octopus.request.TcRequest;
import de.tarent.octopus.request.TcResponse;

public class TcCsvResponseEngine implements TcResponseEngine
{
    public void sendResponse(TcConfig tcConfig, TcResponse tcResponse, TcContent tcContent, TcResponseDescription desc, TcRequest tcRequest)
        	throws ResponseProcessingException 
    {
        // Daten fuer die Ausgabe holen
        Object data = tcContent.get(desc.getDescName());

        // OutputStream holen
        OutputStream os = tcResponse.getOutputStream();
        
        try
        {
            if (data instanceof Object[])
                generateCSV(os, Arrays.asList(((Object[])data)));
            else if (data instanceof List)
                generateCSV(os, (List)data);
            else
                throw new ResponseProcessingException("Given data in response field " + desc.getDescName() + " has to be either List or Array.");

            os.close();
        }
        catch (IOException e)
        {
            throw new ResponseProcessingException("Error processing CSV output.", e);
        }
    }
    
    private void generateCSV(OutputStream os, List processMe) throws ResponseProcessingException, IOException
    {
        // generate header
        Object firstEntry = processMe.get(0);
        if (firstEntry instanceof Map)
            generateHeaderLine(os, (Map)firstEntry);
        
        for (int i=0; i<processMe.size(); i++)
            generateCSVLine(os, processMe.get(i));
    }

    private void generateHeaderLine(OutputStream os, Map input) throws IOException
    {
        Iterator iter = input.keySet().iterator();
        while (iter.hasNext())
        {
            String thisValue = (String)iter.next();
            thisValue = thisValue.replace("\"", "\"\"");
            os.write("\"".getBytes());
            os.write(thisValue.getBytes());
            os.write("\"".getBytes());
            if (iter.hasNext())
                os.write(";".getBytes());            
        }
        
        os.write("\n".getBytes());        
    }

    private void generateCSVLine(OutputStream os, Object input) throws ResponseProcessingException, IOException
    {
        if (input instanceof Map)
            generateCSVLine(os, new ArrayList(((Map)input).values()));
        else if (input instanceof Object[])
            generateCSVLine(os, Arrays.asList((Object[])input));
        else if (input instanceof List)
            generateCSVLine(os, (List)input);
        else
            throw new ResponseProcessingException("Given second level data in response field has to be either List, Map or Array.");
    }
    
    private void generateCSVLine(OutputStream os, List input) throws IOException
    {
        for (int i=0; i<input.size(); i++)
        {
            String thisValue = input.get(i)!=null ? input.get(i).toString() : "";
            thisValue = thisValue.replace("\"", "\"\"");
            os.write("\"".getBytes());
            os.write(thisValue.getBytes());
            os.write("\"".getBytes());
            if (i<input.size()-1)
                os.write(";".getBytes());
        }
        
        os.write("\n".getBytes());
    }
    
    protected String getMimeType(String filename)
    {
        return "text/plain";
    }

    public void init(TcModuleConfig moduleConfig, TcCommonConfig commonConfig)
    {
    }
}
