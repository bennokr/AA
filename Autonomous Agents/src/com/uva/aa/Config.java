package com.uva.aa;

/**
 * A central class for storing constants to allow easy test configuration.
 */
public class Config {

    /** The threshold that determines at what point we stop our evaluation */
    public static final double ERROR_THRESHOLD_THETA = 0.00001;

    /** The step size for updating algorithms */
    public final static double STEP_SIZE_ALPHA = 0.1;

    /** The discount factor for updating algorithms */
    public static final double DISCOUNT_FACTOR_GAMMA = 0.7;

    /** The epsilon for epsilon-greedy selection */
    public final static double EPSILON = 0.1;

    /** The temperature for the softmax selection */
    public final static double TEMPERATURE = 5;

    /** The default value for any Q(s,a) in an agent's policy */
    public final static double DEFAULT_ACTION_VALUE = 15;

    /** The reward for killing a prey */
    public static final double KILL_REWARD = 10.0;
}
