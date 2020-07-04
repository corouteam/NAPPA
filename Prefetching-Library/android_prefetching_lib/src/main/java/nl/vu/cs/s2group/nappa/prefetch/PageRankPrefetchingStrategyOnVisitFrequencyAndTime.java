package nl.vu.cs.s2group.nappa.prefetch;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nl.vu.cs.s2group.nappa.graph.ActivityNode;

/**
 * This strategy employs a Link Analysis approach implementing a PageRank-based algorithm
 * using the time a user spends in the activities and how frequent the user access the
 * activities to decide which nodes to select.
 * <p>
 * Only a subgraph of the ENG is considered in the calculations. The score calculations are
 * performed at runtime and are not persisted in the database.
 * <p>
 * Strategy inspired on the paper Personalized PageRank for Web Page Prediction Based
 * on Access Time-Length and Frequency from 2007.
 * <p>
 * This strategy accepts the following configurations:
 * <ul>
 *     <li>{@link PrefetchingStrategyConfigKeys#WEIGHT_FREQUENCY_SCORE}</li>
 *     <li>{@link PrefetchingStrategyConfigKeys#WEIGHT_TIME_SCORE}</li>
 * </ul>
 *
 * @see <a href="https://dl.acm.org/doi/10.1109/WI.2007.145">Personalized PageRank paper</a>
 */
public class PageRankPrefetchingStrategyOnVisitFrequencyAndTime extends AbstractPrefetchingStrategy {
    private static final String LOG_TAG = PageRankPrefetchingStrategyOnVisitFrequencyAndTime.class.getSimpleName();

    private static final float DEFAULT_WEIGHT_FREQUENCY_SCORE = 0.5f;
    private static final float DEFAULT_WEIGHT_TIME_SCORE = 0.5f;

    protected final float weightFrequencyScore;
    protected final float weightTimeScore;

    public PageRankPrefetchingStrategyOnVisitFrequencyAndTime(@NonNull Map<PrefetchingStrategyConfigKeys, Object> config) {
        super(config);

        weightFrequencyScore = getConfig(
                PrefetchingStrategyConfigKeys.WEIGHT_FREQUENCY_SCORE,
                DEFAULT_WEIGHT_FREQUENCY_SCORE);

        weightTimeScore = getConfig(
                PrefetchingStrategyConfigKeys.WEIGHT_TIME_SCORE,
                DEFAULT_WEIGHT_TIME_SCORE);

        if ((weightFrequencyScore + weightTimeScore) != 1.0)
            throw new IllegalArgumentException("The sum of the time and frequency weight must be 1!");
    }

    @NonNull
    @Override
    public List<String> getTopNUrlToPrefetchForNode(ActivityNode node, Integer maxNumber) {
        return new ArrayList<>();
    }
}
