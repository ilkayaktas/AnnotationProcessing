import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by iaktas on 25.09.2017 at 17:29.
 */
class PersonTest {
    @org.junit.jupiter.api.Test
    void whenBuildPersonWithBuilder_thenObjectHasPropertyValues() {
        Person person = new PersonBuilder().setAge(25).setName("John").build();

        assertEquals(25, person.getAge());
        assertEquals("John", person.getName());
    }

}