
package de.tarent.octopus.content.annotation;

import de.tarent.octopus.config.TcModuleConfig;
import de.tarent.octopus.server.OctopusContext;
import de.tarent.octopus.content.annotation.InOutParam;

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
        testCase.assertTrue("Map Übergabe", map.size()== 1);        
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
        testCase.assertEquals("Integer Übergabe", 42, (int)int1);
        testCase.assertEquals("int Übergabe", 0, int2);

        testCase.assertEquals("Long Übergabe", 42, (long)long1);
        testCase.assertEquals("long Übergabe", 0, long2);

        testCase.assertEquals("Float Übergabe", 42f, (float)float1);
        testCase.assertEquals("float Übergabe", 0f, float2);

        testCase.assertEquals("Double Übergabe", 42d, (double)double1);
        testCase.assertEquals("double Übergabe", 0d, double2);

        testCase.assertEquals("Boolean Übergabe", true, (boolean)boolean1);
        testCase.assertEquals("boolean Übergabe", false, boolean2);
        
        testCase.assertTrue("List Übergabe", list.size()== 1);
        
        testCase.assertEquals("MapBean Übergabe", "Frank", mapBan.getName());
        testCase.assertEquals("MapBean Übergabe", "Prüm", mapBan.getCity());
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