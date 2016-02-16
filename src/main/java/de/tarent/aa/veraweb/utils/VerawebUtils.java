package de.tarent.aa.veraweb.utils;

import de.tarent.dblayer.helper.ResultList;
import de.tarent.dblayer.helper.ResultMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
public class VerawebUtils {

    public static List copyResultListToArrayList(ResultList allResults) {
        final ArrayList fullList = new ArrayList();
        final int size = allResults.size();
        for (int i = 0; i < size; i++) {
            final ResultMap resultMap = (ResultMap) allResults.get(i);
            final HashMap map = new HashMap();
            map.putAll(resultMap);
            fullList.add(map);
        }

        return fullList;
    }

}
