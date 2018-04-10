package de.tarent.commons.datahandling;

/**
 * used by BeanMapTest
 */
public interface PersonI {

    public String getName();
    public void setName(String newName);

    public int getAge();
    public void setAge(int newAge);

    public int getA();
    public void setA(int newAge);

    public boolean isCondition();
    public void setCondition(boolean newCondition);

    public String echoMethod(String inMsg);
}
