import annotations.Factory;

/**
 * Created by iaktas on 26.09.2017 at 18:18.
 */

@Factory(
        id = "Tiramisu",
        type = Meal.class
)
public class Tiramisu implements Meal {

    public float getPrice() {
        return 4.5f;
    }
}