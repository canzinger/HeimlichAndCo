package heimlich_and_co.util;

import java.util.Random;

/**
 * A class that provides methods to roll a die, specifically for Heimlich and Co
 *
 * the top face 1-3 is encoded as 13
 */
public class Die {
    private final int[] faces = {13, 2, 3, 4, 5, 6};
    private final Random random;

    /**
     * Creates a new Die instance.
     */
    public Die() {
        this.random = new Random();
    }

    /**
     * @return the resulting number (or encoded number) of a simulated roll
     */
    public int roll() {
        return faces[random.nextInt(faces.length)];
    }

    /**
     * Returns the possible outcomes of a die roll, i.e. all faces of the die
     *
     * @return faces of the die
     */
    public int[] getFaces() {
        return faces;
    }
}
