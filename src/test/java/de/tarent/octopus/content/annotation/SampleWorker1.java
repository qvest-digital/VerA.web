
package de.tarent.octopus.content.annotation;

import de.tarent.octopus.config.TcModuleConfig;
import de.tarent.octopus.content.annotation.InOutParam;

import junit.framework.Assert;
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
    	Assert.assertEquals("Integer �bergabe", 42, (int)int1);
    	Assert.assertEquals("int �bergabe", 42, int2);

    	Assert.assertEquals("Long �bergabe", 42, (long)long1);
    	Assert.assertEquals("long �bergabe", 42, long2);

    	Assert.assertEquals("Float �bergabe", 42f, (float)float1);
    	Assert.assertEquals("float �bergabe", 42f, float2);

        Assert.assertEquals("Double �bergabe", 42d, (double)double1);
        Assert.assertEquals("double �bergabe", 42d, double2);

        Assert.assertEquals("Boolean �bergabe", true, (boolean)boolean1);
        Assert.assertEquals("boolean �bergabe", true, boolean2);
        
        Assert.assertTrue("List �bergabe", list.size()== 1);
        Assert.assertTrue("Map �bergabe", map.size()== 1);        
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
    	Assert.assertEquals("Integer �bergabe", 42, (int)int1);
        Assert.assertEquals("int �bergabe", 0, int2);

        Assert.assertEquals("Long �bergabe", 42, (long)long1);
        Assert.assertEquals("long �bergabe", 0, long2);

        Assert.assertEquals("Float �bergabe", 42f, (float)float1);
        Assert.assertEquals("float �bergabe", 0f, float2);

        Assert.assertEquals("Double �bergabe", 42d, (double)double1);
        Assert.assertEquals("double �bergabe", 0d, double2);

        Assert.assertEquals("Boolean �bergabe", true, (boolean)boolean1);
        Assert.assertEquals("boolean �bergabe", false, boolean2);
        
        Assert.assertTrue("List �bergabe", list.size()== 1);
        
        Assert.assertEquals("MapBean �bergabe", "Frank", mapBan.getName());
        Assert.assertEquals("MapBean �bergabe", "Pr�m", mapBan.getCity());
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