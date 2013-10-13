package com.uva.aa;

/**
 * A central class for storing constants to allow easy test configuration.
 */
public class Config {

    /** The threshold that determines at what point we stop our evaluation */
    public static final double ERROR_THRESHOLD_THETA = 0.00001;

    /** The step size for updating algorithms */
    public final static double STEP_SIZE_ALPHA = 0.1;

    /** The step size for updating algorithms */
    public final static double STEP_SIZE_BETA = 0.1;

    /** The step size for the prey's updating algorithms */
    public final static double PREY_STEP_SIZE_ALPHA = 0.1;
    
    /** The discount factor for updating algorithms */
    public static final double DISCOUNT_FACTOR_GAMMA = 0.3;

    /** The discount factor for the prey's updating algorithms */
    public static final double PREY_DISCOUNT_FACTOR_GAMMA = 0.3;
    
    /** The epsilon for epsilon-greedy selection */
    public final static double EPSILON = 0.1;
    
    /** The epsilon for the prey with epsilon-greedy selection */
    public final static double PREY_EPSILON = 0.1;

    /** The temperature for the softmax selection */
    public final static double TEMPERATURE = 5;

    /** The default value for any Q(s,a) in an agent's policy */
    public final static double DEFAULT_ACTION_VALUE = 0;

    /** The default value for any Q(s,a) in an prey's policy */
    public final static double PREY_DEFAULT_ACTION_VALUE = 0;
    
    /** The reward for killing a prey */
    public static final double KILL_REWARD = 10.0;
    
    /** The punishment for colliding with another predator - treated as a reward so should be negative */
    public final static double COLLISION_REWARD = -10.0;

    /** The punishment for dying as a prey - treated as a reward so should be negative */
    public static final double PREY_DIE_REWARD = -10.0;

    /** The reward for escaping the predators as a prey */
    public final static double PREY_ESCAPE_REWARD = 10.0;
}
