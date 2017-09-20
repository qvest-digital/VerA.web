package de.tarent.octopus.content;

/*-
 * tarent-octopus, Webservice Data Integrator and Application Server
 * Copyright © 2002–2017 tarent solutions GmbH and its contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
/**
 * @author Jens Neumaier, tarent GmbH
 *
 */
public interface CookieMap {
    
    /** Prefix for the Map in which cookies will be stored **/ 
    public static final String PREFIX_COOKIE_MAP = "cookies";
    
    /** New cookies will be stored in the octopus-content.
     * 	Each cookie is saved in another Map either as a Cookie-Object
     * 	in the field "cookie" that has to be assigned manually or
     *  in the field "value" which will automatically be used
     *  if a user preference is saved in the PersonalConfig.
     */
    public static final String COOKIE_MAP_FIELD_VALUE = "value";
    public static final String COOKIE_MAP_FIELD_COOKIE = "cookie";
    
    /** Configuration settings for default cookie creation 
     * 	
     * 	e.g. <param name="cookies.defaultMaxAge" value="5000000"/>
     */
    public static final String PREFIX_CONFIG_MAP = "cookies";
    public static final String CONFIG_MAXAGE = "defaultMaxAge";
}
