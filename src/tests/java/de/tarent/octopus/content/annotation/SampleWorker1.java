
package de.tarent.octopus.content.annotation;

import de.tarent.octopus.config.TcModuleConfig;
import de.tarent.octopus.server.OctopusContext;
import junit.framework.TestCase;

import javax.jws.*;
import java.util.*;

@Version("0.4.2")
public class SampleWorker1 {

    public boolean wasInitCalled = false;

    public void init(TcModuleConfig tmc) 
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
        testCase.assertEquals("Integer Übergabe", 42, (int)int1);
        testCase.assertEquals("int Übergabe", 42, int2);

        testCase.assertEquals("Long Übergabe", 42, (long)long1);
        testCase.assertEquals("long Übergabe", 42, long2);

        testCase.assertEquals("Float Übergabe", 42f, (float)float1);
        testCase.assertEquals("float Übergabe", 42f, float2);

        testCase.assertEquals("Double Übergabe", 42d, (double)double1);
        testCase.assertEquals("double Übergabe", 42d, double2);

        testCase.assertEquals("Boolean Übergabe", true, (boolean)boolean1);
        testCase.assertEquals("boolean Übergabe", true, boolean2);
        
        testCase.assertTrue("List Übergabe", list.size()== 1);
        testCase.assertTrue("List Übergabe", map.size()== 1);        
    }
}