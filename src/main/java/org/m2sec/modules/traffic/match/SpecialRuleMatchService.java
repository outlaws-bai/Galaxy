package org.m2sec.modules.traffic.match;

import burp.api.montoya.core.Annotations;
import burp.api.montoya.core.HighlightColor;
import org.m2sec.common.Tuple;
import org.m2sec.common.config.HttpTrafficAutoModificationConfig;
import org.m2sec.common.models.FuzzDict;
import org.m2sec.common.models.Headers;
import org.m2sec.common.models.Request;
import org.m2sec.common.models.Response;

import java.util.*;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description:
 */
public class SpecialRuleMatchService {

    private static final Map<Integer, Tuple<Integer, Set<String>>> MATCHED = new LinkedHashMap<>();

    public static void matchRequest(Request request, int messageId,
                                    HttpTrafficAutoModificationConfig.MatchConfig matchConfig) {
        Set<String> matchedKeys = new HashSet<>();
        Set<String> params = FuzzDict.getParamsNames(new Tuple<>(request, null));
        int score = 0;
        for (Map.Entry<String, Integer> entry : matchConfig.getRequestParamMatches().entrySet())
            if (params.contains(entry.getKey())) {
                matchedKeys.add(entry.getKey());
                score += getScoreByLevel(entry.getValue());
            }
        MATCHED.put(messageId, new Tuple<>(score, matchedKeys));
    }

    public static Annotations matchResponse(Response response, int messageId,
                                            HttpTrafficAutoModificationConfig.MatchConfig matchConfig) {
        Tuple<Integer, Set<String>> temp = MATCHED.containsKey(messageId) ? MATCHED.get(messageId) : new Tuple<>(0,
            new HashSet<>());
        MATCHED.remove(messageId);
        Set<String> responseHeaderMatchedKeys = new HashSet<>();
        Set<String> responseContentMatchedKeys = new HashSet<>();
        Annotations annotations = Annotations.annotations();
        int score = temp.getFirst();
        Headers headers = response.getHeaders();
        for (Map.Entry<String, Integer> entry : matchConfig.getResponseHeaderMatches().entrySet()) {
            if (headers.containsKey(entry.getKey())) {
                responseHeaderMatchedKeys.add(entry.getKey());
                score += getScoreByLevel(entry.getValue());
            }
        }
        String body = new String(response.getContent());
        for (Map.Entry<String, Integer> entry : matchConfig.getResponseContentMatches().entrySet()) {
            if (body.contains(entry.getKey())) {
                responseContentMatchedKeys.add(entry.getKey());
                score += getScoreByLevel(entry.getValue());
            }
        }
        StringBuilder sb = new StringBuilder();
        annotations.setHighlightColor(getColorByScore(score));
        if (!temp.getSecond().isEmpty()) {
            sb.append("requestParamMatchedKeys: ").append(String.join(",", temp.getSecond())).append("\r\n");
        }
        if (!responseHeaderMatchedKeys.isEmpty()) {
            sb.append("responseHeaderMatchedKeys: ").append(String.join(",", responseHeaderMatchedKeys)).append("\r\n");
        }
        if (!responseContentMatchedKeys.isEmpty()) {
            sb.append("responseContentMatchedKeys: ").append(String.join(",", responseContentMatchedKeys)).append("\r" +
                "\n");
        }
        annotations.setNotes(sb.toString());
        return annotations;
    }

    private static int getScoreByLevel(int level) {
        if (level == 1) return 10;
        if (level == 2) return 20;
        if (level == 3) return 30;
        if (level == 4) return 40;
        return 50;
    }

    private static HighlightColor getColorByScore(int score) {
        if (score >= 100) return HighlightColor.RED;
        else if (score >= 90) return HighlightColor.MAGENTA;
        else if (score >= 80) return HighlightColor.BLUE;
        else if (score >= 70) return HighlightColor.ORANGE;
        else if (score >= 60) return HighlightColor.CYAN;
        else if (score >= 50) return HighlightColor.YELLOW;
        else if (score >= 40) return HighlightColor.GREEN;
        else if (score >= 30) return HighlightColor.PINK;
        else if (score >= 20) return HighlightColor.GRAY;
        else return HighlightColor.NONE;
    }
}
