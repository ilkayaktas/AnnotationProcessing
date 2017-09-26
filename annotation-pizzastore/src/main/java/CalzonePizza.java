/**
 * Created by iaktas on 26.09.2017 at 18:16.
 */

@Factory(
        id = "Calzone",
        type = Meal.class
)
public class CalzonePizza implements Meal {

    public float getPrice() {
        return 8.5f;
    }
}