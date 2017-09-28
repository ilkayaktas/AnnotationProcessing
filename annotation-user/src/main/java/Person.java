import annotations.BuilderProperty;

/**
 * Created by iaktas on 25.09.2017 at 16:59.
 */

public class Person {
    private int age;
    private String name;

    public int getAge() {
        return age;
    }

    @BuilderProperty
    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    @BuilderProperty
    public void setName(String name) {
        this.name = name;
    }
}