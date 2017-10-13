package pizza;

import annotations.Factory;

/**
 * Created by iaktas on 26.09.2017 at 18:17.
 */

@Factory(
        id = "Margherita",
        type = Meal.class
)
public class MargheritaPizza implements Meal {

    public float getPrice() {
        return 0;
    }
}