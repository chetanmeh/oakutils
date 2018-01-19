/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.chetanmeh.oak;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.jackrabbit.oak.plugins.document.Revision;
import org.apache.jackrabbit.oak.plugins.document.StableRevisionComparator;

public class RevisionFormatter {
    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS zzz";

    /**
     * Regex to match revision string like r15071f47255-0-3.
     * TODO - ClusterId can be more that 1 digit. So need to handle that
     */
    private static final Pattern REV_REG_EX = Pattern.compile("(r[\\da-f]{11}-\\d-\\d)");

    private final SimpleDateFormat sdf;

    public RevisionFormatter(String timeZone) {
        this.sdf = createDateFormat(timeZone);
    }

    public Result format(String text){
        StringBuffer result = new StringBuffer();
        Matcher m = REV_REG_EX.matcher(text);
        Set<Revision> revsSet = new HashSet<>();
        while (m.find()) {
            Revision rev = Revision.fromString(m.group(0));
            m.appendReplacement(result, formatRev(rev));
            revsSet.add(rev);
        }
        m.appendTail(result);

        List<Revision> revs = new ArrayList<>(revsSet);
        Collections.sort(revs, Collections.reverseOrder(StableRevisionComparator.INSTANCE));
        List<String> formattedRevStrs = new ArrayList<>();
        for (Revision r : revs) {
            formattedRevStrs.add(formatRev(r));
        }

        return new Result(result.toString(), formattedRevStrs);
    }

    private String formatRev(Revision r) {
        Date d = new Date(r.getTimestamp());
        String revStr;
        synchronized (sdf) {
            revStr = sdf.format(d);
        }
        return r + ":" + revStr;
    }

    public static class Result {
        final String formattedText;
        final List<String> extractedRevisions;

        public Result(String formattedText, List<String> extractedRevisions) {
            this.formattedText = formattedText;
            this.extractedRevisions = extractedRevisions;
        }
    }

    private static SimpleDateFormat createDateFormat(String timeZone) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        TimeZone utc = TimeZone.getTimeZone(timeZone);
        sdf.setTimeZone(utc);
        return sdf;
    }
}
