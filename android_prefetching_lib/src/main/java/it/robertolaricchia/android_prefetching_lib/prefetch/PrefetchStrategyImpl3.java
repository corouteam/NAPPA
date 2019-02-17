package it.robertolaricchia.android_prefetching_lib.prefetch;

import android.support.annotation.NonNull;
import android.util.Log;

import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import it.robertolaricchia.android_prefetching_lib.PrefetchingLib;
import it.robertolaricchia.android_prefetching_lib.graph.ActivityNode;
import it.robertolaricchia.android_prefetching_lib.prefetchurl.ParameteredUrl;
import it.robertolaricchia.android_prefetching_lib.room.AggregateUrlDao;
import it.robertolaricchia.android_prefetching_lib.room.PrefetchingDatabase;
import it.robertolaricchia.android_prefetching_lib.room.dao.SessionDao;
import it.robertolaricchia.android_prefetching_lib.room.data.ActivityExtraData;

public class PrefetchStrategyImpl3 implements PrefetchStrategy {

    private float threshold;
    private HashMap<Long, String> reversedHashMap = new HashMap<>();

    public PrefetchStrategyImpl3(float threshold) {
        this.threshold = threshold;
    }



    @NonNull
    @Override
    public List<String> getTopNUrlToPrefetchForNode(ActivityNode node, Integer maxNumber) {

        Map<String, Long> activityMap = PrefetchingLib.activityMap;
        for (String key : activityMap.keySet()){
            reversedHashMap.put(activityMap.get(key), key);
        }

        List<ActivityNode> probableNodes = getMostProbableNodes(node, 1, new LinkedList<>());

        List<String> listUrlToPrefetch = new LinkedList<>();

        for (ActivityNode node1 : probableNodes) {
            listUrlToPrefetch.addAll(computeCandidateUrl2(node1, node));
        }

        //return computeCandidateUrl(node);
        return listUrlToPrefetch;
    }

    private List<ActivityNode> getMostProbableNodes(ActivityNode node, float initialProbability, List<ActivityNode> probableNodes) {
        List<SessionDao.SessionAggregate> sessionAggregate = node.getSessionAggregateList();

        HashMap<Long, Integer> successorCountMap = new HashMap<>();

        int total = 0;
        for (SessionDao.SessionAggregate succ : sessionAggregate) {
            total += succ.countSource2Dest;
            successorCountMap.put(succ.idActDest, succ.countSource2Dest.intValue());
        }

        for (Long succ : successorCountMap.keySet()) {
            float prob = initialProbability * (successorCountMap.get(succ)/total);
            ActivityNode node1 = PrefetchingLib.getActivityGraph().getByName(reversedHashMap.get(succ));
            if (prob >= threshold) {
                if (!probableNodes.contains(succ)) {

                    probableNodes.add(node1);
                    getMostProbableNodes(node1, prob, probableNodes);
                }
            }
            Log.e("PREFSTRAT3", "Computed probability: " + prob + " for " + node1.activityName);
        }


        /*
        //Map<ActivityNode, Integer> successorCountMap = node.successors;
        for (ActivityNode succ : successorCountMap.keySet()) {
            total += successorCountMap.get(succ);
        }

        for (ActivityNode succ : successorCountMap.keySet()) {
            float prob = initialProbability * (successorCountMap.get(succ)/total);
            Log.e("PREFSTRAT3", "Computed probability: " + prob);
            if (prob >= threshold) {
                if (!probableNodes.contains(succ)) {
                    probableNodes.add(succ);
                    getMostProbableNodes(succ, prob, probableNodes);
                }
            }
        }
        */
        return probableNodes;
    }
    /*
    private List<String> computeCandidateUrl(ActivityNode node) {
        node.parameteredUrlMap.keySet();
        List<ActivityNode> successors = ActivityNode.getAllSuccessors(node, new LinkedList<>());

        List<String> candidates = new LinkedList<>();

        Map<String, String> extrasMap = PrefetchingLib.getExtrasMap().get(PrefetchingLib.getActivityIdFromName(node.activityName));

        for (ActivityNode successor : successors) {

            //for (String key : extrasMap.keySet()) {
            //    ParameteredUrl parameteredUrl = successor.parameteredUrlMap.get(key);
            //    if (parameteredUrl != null) {
            //        candidates.add(
            //                parameteredUrl.fillParams(extrasMap)
            //        );
            //    }
            //}
            for (ParameteredUrl parameteredUrl : successor.parameteredUrlList) {
                if (extrasMap.keySet().containsAll(parameteredUrl.getParamKeys())) {
                    candidates.add(
                            parameteredUrl.fillParams(extrasMap)
                    );
                }
            }
        }

        for (String candidate: candidates) {
            Log.e("PREFSTRAT3", candidate);
        }

        return candidates;
    }
    */

    private List<String> computeCandidateUrl2(ActivityNode toBeChecked, ActivityNode node) {
        node.parameteredUrlMap.keySet();
        //List<ActivityNode> successors = ActivityNode.getAllSuccessors(node, new LinkedList<>());

        List<String> candidates = new LinkedList<>();

        Map<String, String> extrasMap = PrefetchingLib.getExtrasMap().get(PrefetchingLib.getActivityIdFromName(node.activityName));

        //for (ActivityNode successor : successors) {

            /*for (String key : extrasMap.keySet()) {
                ParameteredUrl parameteredUrl = successor.parameteredUrlMap.get(key);
                if (parameteredUrl != null) {
                    candidates.add(
                            parameteredUrl.fillParams(extrasMap)
                    );
                }
            }*/
            for (ParameteredUrl parameteredUrl : toBeChecked.parameteredUrlList) {
                if (extrasMap.keySet().containsAll(parameteredUrl.getParamKeys())) {
                    candidates.add(
                            parameteredUrl.fillParams(extrasMap)
                    );
                }
            }
        //}

        for (String candidate: candidates) {
            Log.e("PREFSTRAT3", candidate + " for: " + toBeChecked.activityName);
        }

        return candidates;
    }
}