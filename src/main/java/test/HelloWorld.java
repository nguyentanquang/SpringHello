package test;

/**
 * Created by Admin on 30/05/2017.
 */
public class HelloWorld {

    private String name;

    public void setName(String name)
    {
        this.name = name;
    }

    public void print(){
        System.out.println("Hello: 1234" + this.name);
    }
}
