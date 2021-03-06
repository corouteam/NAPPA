package nl.vu.cs.s2group.nappa.prefetch;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import nl.vu.cs.s2group.nappa.Nappa;
import nl.vu.cs.s2group.nappa.graph.ActivityNode;
import nl.vu.cs.s2group.nappa.room.dao.SessionDao;
import nl.vu.cs.s2group.nappa.util.NappaUtil;

/**
 * Utilizes the Prediction by Partial Match (PPM) based on a 0-order markov-model.
 * This strategy only considers the direct successors of the current node.
 * <br/><br/>
 *
 * <p> The PPM algorithm is defined in the paper
 * <a href="https://dl.acm.org/doi/10.1145/301453.301557">
 * Web prefetching between low-bandwidth clients and proxies: potential and
 * performance</a>
 */
@Deprecated
public class PPMPrefetchingStrategy implements PrefetchingStrategy {
    private static final String LOG_TAG = PPMPrefetchingStrategy.class.getSimpleName();

    private float threshold;
    private HashMap<Long, String> reversedHashMap = new HashMap<>();
    private static ScheduledThreadPoolExecutor poolExecutor = new ScheduledThreadPoolExecutor(1);
    public PPMPrefetchingStrategy(float threshold) {
        this.threshold = threshold;
    }
    public static int lastN = 2;

    @Override
    public boolean needVisitTime() {
        return false;
    }

    @Override
    public boolean needSuccessorsVisitTime() {
        return false;
    }

    @NonNull
    @Override
    public List<String> getTopNUrlToPrefetchForNode(ActivityNode node, Integer maxNumber) {

        Map<String, Long> activityMap = Nappa.activityMap;
        for (String key : activityMap.keySet()){
            reversedHashMap.put(activityMap.get(key), key);
        }
        List<ActivityNode> probableNodes = getMostProbableNodes(node);
        for (ActivityNode node1 : probableNodes) {
            for (int i=probableNodes.lastIndexOf(node1)+1;i<probableNodes.size();i++) {
                ActivityNode node2 = probableNodes.get(i);
                if(node1.prob<node2.prob){
                    ActivityNode temp=node1;
                    probableNodes.set(probableNodes.lastIndexOf(node1),node2);
                    probableNodes.set(probableNodes.lastIndexOf(node2),temp);
                    node1=node2;
                }
            }
        }
        List<String> listUrlToPrefetch = new LinkedList<>();
        maxNumber = (int) (threshold*probableNodes.size() +1);

        for (int i=0; i<maxNumber; i++) {
            listUrlToPrefetch.addAll(NappaUtil.getUrlsFromCandidateNode(node, probableNodes.get(i)));
            Log.d(LOG_TAG,"SELECTED --> " + probableNodes.get(i).activityName + " index: " + probableNodes.get(i).prob);

        }
        return listUrlToPrefetch;
    }

    /**
     * Will calculate the total probabilities of access for each individual successor for a {@code node}.
     * Will then recursively calculate the probability for the successor of each successor and will sum it if a successor is already been accessed from another path:
     *
     * node->successorA->...->successorN
     * if(successorN is known) -> sum probabilities (indipendent events)
     *
     * @param node Current activity to be considered for prefetching
     * total Total number of hits in the sessions taken in consideration
     * successorCountMap List containing all the probable nodes, corresponding to those nodes that
     *                      have a probability exceeding the prescribed threshold.
     * @return The set of probable nodes {@code List<ActivityNode>} with respect to the initial activity {@code node}
     */

    private List<ActivityNode> getMostProbableNodes(ActivityNode node) {
        HashMap<Long, Integer> successorCountMap = new HashMap<>();
        List<ActivityNode> probableNodes = new LinkedList<>();
        successorCountMap = zeroContextNodes(node,successorCountMap);
        int total = 0;
        for(Long candidate : successorCountMap.keySet()) {
            total+=successorCountMap.get(candidate);
            Log.d(LOG_TAG,"actName :"+reversedHashMap.get(candidate)+" hit: "+successorCountMap.get(candidate));
        }
        //////////////////////////// Will calculate the probability to access a node by partial match based on a 0-order markov-model
        //////////////////////////// https://pdfs.semanticscholar.org/f9dc/bf7b0c900335932d9a651b9c21d8a59c3679.pdf

        for (Long succ : successorCountMap.keySet()) {
            float prob = 0;
            if(total>0) prob= (float)successorCountMap.get(succ)/total; // * succ.pageRank or others
            ActivityNode node1 = Nappa.getActivityGraph().getByName(reversedHashMap.get(succ));
            node1.prob=prob;
            probableNodes.add(node1);
            Log.d(LOG_TAG, "Computed probability: " + prob + " for " + node1.activityName);
        }
        return probableNodes;
    }

    private HashMap<Long, Integer> zeroContextNodes(ActivityNode node, HashMap<Long, Integer> successorCountMap){
        List<SessionDao.SessionAggregate> sessionAggregate = node.getSessionAggregateList();
        for (SessionDao.SessionAggregate succ : sessionAggregate) {
            successorCountMap = zeroContextNodes(Nappa.getActivityGraph().getByName(reversedHashMap.get(succ.idActDest)),successorCountMap);
            if(successorCountMap.containsKey(succ.idActDest)){
                successorCountMap.put(succ.idActDest, succ.countSource2Dest.intValue()+successorCountMap.get(succ.idActDest));
            }
            else {successorCountMap.put(succ.idActDest, succ.countSource2Dest.intValue()); }//Log.d("PREFSTRAT4 insert count",succ.countSource2Dest+"");}
        }
        return  successorCountMap;
    }
}
