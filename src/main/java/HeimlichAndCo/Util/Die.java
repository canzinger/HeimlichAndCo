package HeimlichAndCo.Util;

import java.util.Random;

/**
 * A class that provides methods to roll a die, specifically for Heimlich and Co
 *
 * the top face 1-3 is encoded as 13
 */
public class Die {
    private final int[] faces = {13, 2, 3, 4, 5, 6};
    private Random random;

    public Die() {
        this.random = new Random();
    }

    /**
     *
     * @return the resulting number (or encoded number) of a simulated roll
     */
    public int roll() {
        return faces[random.nextInt(faces.length)];
    }
}
