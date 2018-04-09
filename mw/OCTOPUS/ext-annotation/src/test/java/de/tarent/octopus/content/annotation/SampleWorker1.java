/*
 * tarent-octopus annotation extension,
 * an opensource webservice and webapplication framework (annotation extension)
 * Copyright (c) 2006-2007 tarent GmbH
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License,version 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 *
 * tarent GmbH., hereby disclaims all copyright
 * interest in the program 'tarent-octopus annotation extension'
 * Signature of Elmar Geese, 11 June 2007
 * Elmar Geese, CEO tarent GmbH.
 */

package de.tarent.octopus.content.annotation;

import de.tarent.octopus.config.ModuleConfig;
import de.tarent.octopus.content.annotation.InOutParam;

import junit.framework.Assert;
import junit.framework.TestCase;

import javax.jws.*;
import java.util.*;

@Version("0.4.2")
public class SampleWorker1 {

    public boolean wasInitCalled = false;

    public void init(ModuleConfig tmc)
    {
	wasInitCalled = true;
    }

    @WebMethod()
    @Result("helloWorldResult")
    public String helloWorld()
    {
	return "Hello World!";
    }

    @WebMethod()
    @Result() //using default: "return"
    public String helloWorldWithDefaultResult()
    {
	return "Hello World!";
    }

    @WebMethod(operationName="otherName")
    @Result() //using default: "return"
    public String helloWorldWithOtherName()
    {
	return "Hello World!";
    }

    @WebMethod()
    @Result() //using default: "return"
    public String helloWorldWithArgument(@WebParam(name="firstname")
					 String name)
    {
	return "Hello "+name;
    }

    @WebMethod()
    @Result() //using default: "return"
    public String nameAnnotation(@Name("firstname")
				 String name)
    {
	return "Hello "+name;
    }

    @WebMethod()
    public void optionalArguments(@Name("mandatoryByDefault")
				  String p1,

				  @Name("mandatory")
				  @Optional(false)
				  String p2,

				  @Name("optional")
				  @Optional(true)
				  String p3,

				  @Name("optional")
				  @Optional() //defaultvalue
				  String p4)
    {
	return;
    }

    @WebMethod()
    public void testTypesSimple(@Name("testCase") TestCase testCase,
				@Name("int") Integer int1,
				@Name("int") int int2,
				@Name("long") Long long1,
				@Name("long") long long2,
				@Name("float") Float float1,
				@Name("float") float float2,
				@Name("double") Double double1,
				@Name("double") double double2,
				@Name("boolean") Boolean boolean1,
				@Name("boolean") boolean boolean2,
				@Name("list") List list,
				@Name("map") Map map)
    {
	Assert.assertEquals("Integer Übergabe", 42, (int)int1);
	Assert.assertEquals("int Übergabe", 42, int2);

	Assert.assertEquals("Long Übergabe", 42, (long)long1);
	Assert.assertEquals("long Übergabe", 42, long2);

	Assert.assertEquals("Float Übergabe", 42f, (float)float1);
	Assert.assertEquals("float Übergabe", 42f, float2);

	Assert.assertEquals("Double Übergabe", 42d, (double)double1);
	Assert.assertEquals("double Übergabe", 42d, double2);

	Assert.assertEquals("Boolean Übergabe", true, (boolean)boolean1);
	Assert.assertEquals("boolean Übergabe", true, boolean2);

	Assert.assertTrue("List Übergabe", list.size()== 1);
	Assert.assertTrue("Map Übergabe", map.size()== 1);
    }

    @WebMethod()
    public void testParameterConversions(@Name("testCase") TestCase testCase,

					 @Name("int1")
					 Integer int1,

					 @Name("int2") @Optional()
					 int int2,

					 @Name("long1")
					 Long long1,

					 @Name("long2") @Optional()
					 long long2,

					 @Name("float1")
					 Float float1,

					 @Name("float2") @Optional()
					 float float2,

					 @Name("double1")
					 Double double1,

					 @Name("double2") @Optional()
					 double double2,

					 @Name("boolean1")
					 Boolean boolean1,

					 @Name("boolean2") @Optional()
					 boolean boolean2,

					 @Name("list")
					 List list,

					 @Name("map")
					 MyMapBean mapBan)
    {
	Assert.assertEquals("Integer Übergabe", 42, (int)int1);
	Assert.assertEquals("int Übergabe", 0, int2);

	Assert.assertEquals("Long Übergabe", 42, (long)long1);
	Assert.assertEquals("long Übergabe", 0, long2);

	Assert.assertEquals("Float Übergabe", 42f, (float)float1);
	Assert.assertEquals("float Übergabe", 0f, float2);

	Assert.assertEquals("Double Übergabe", 42d, (double)double1);
	Assert.assertEquals("double Übergabe", 0d, double2);

	Assert.assertEquals("Boolean Übergabe", true, (boolean)boolean1);
	Assert.assertEquals("boolean Übergabe", false, boolean2);

	Assert.assertTrue("List Übergabe", list.size()== 1);

	Assert.assertEquals("MapBean Übergabe", "Frank", mapBan.getName());
	Assert.assertEquals("MapBean Übergabe", "Prüm", mapBan.getCity());
    }

    @WebMethod
    public void testInOuts(@Name("p1")
			   InOutParam<String> p1,

			   @Name("p2")
			   InOutParam<Integer> p2,

			   @Name("p3")
			   InOutParam<Boolean> p3) {
	p1.set(p1.get()+".suffix");
	p2.set(p2.get()+2);
	p3.set(!p3.get());
    }

}
