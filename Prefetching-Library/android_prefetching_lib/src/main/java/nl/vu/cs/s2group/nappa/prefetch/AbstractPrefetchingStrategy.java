package nl.vu.cs.s2group.nappa.prefetch;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * This class defines common configuration shared among all prefetching strategies.
 * The accepted configurations are:
 *
 * <ul>
 *     <li> {@link PrefetchingStrategyConfigKeys#MAX_URL_TO_PREFETCH} </li>
 * </ul>
 */
public abstract class AbstractPrefetchingStrategy {
    public static final float DEFAULT_SCORE_LOWER_THRESHOLD = 0.6f;
    public static final int DEFAULT_LAST_N_SESSIONS = 5;
    public static final int DEFAULT_MAX_URL_TO_PREFETCH = 2;

    protected final int maxNumberOfUrlToPrefetch;

    public AbstractPrefetchingStrategy(@NotNull Map<PrefetchingStrategyConfigKeys, Object> config) {
        Object data;

        data = config.get(PrefetchingStrategyConfigKeys.MAX_URL_TO_PREFETCH);
        maxNumberOfUrlToPrefetch = data != null ? Integer.parseInt(data.toString()) : DEFAULT_MAX_URL_TO_PREFETCH;
    }
}
