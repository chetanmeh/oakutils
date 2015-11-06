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

import com.google.appengine.repackaged.com.google.api.client.util.Lists;
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
        Set<FormattedRev> revsSet = new HashSet<>();
        while (m.find()) {
            FormattedRev rev = new FormattedRev(m.group(0));
            m.appendReplacement(result, rev.toString());
            revsSet.add(rev);
        }
        m.appendTail(result);

        List<FormattedRev> revs = new ArrayList<>(revsSet);
        Collections.sort(revs, Collections.reverseOrder());
        List<String> formattedRevStrs = Lists.newArrayListWithCapacity(revs.size());
        for (FormattedRev r : revs) {
            formattedRevStrs.add(r.toString());
        }

        return new Result(result.toString(), formattedRevStrs);
    }

    private String formatRev(Revision r) {
        Date d = new Date(r.getTimestamp());
        synchronized (sdf) {
            return sdf.format(d);
        }
    }
    
    public static class Result {
        final String formattedText;
        final List<String> extractedRevisions;

        public Result(String formattedText, List<String> extractedRevisions) {
            this.formattedText = formattedText;
            this.extractedRevisions = extractedRevisions;
        }
    }

    private class FormattedRev implements Comparable<FormattedRev> {
        private final Revision r;

        public FormattedRev(String r) {
            this(Revision.fromString(r));
        }

        public FormattedRev(Revision r) {
            this.r = r;
        }

        @Override
        public int compareTo(FormattedRev o) {
            return StableRevisionComparator.INSTANCE.compare(r, o.r);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            FormattedRev that = (FormattedRev) o;

            return !(r != null ? !r.equals(that.r) : that.r != null);

        }

        @Override
        public int hashCode() {
            return r != null ? r.hashCode() : 0;
        }

        @Override
        public String toString() {
            return r + ":" + formatRev(r);
        }
    }

    private static SimpleDateFormat createDateFormat(String timeZone) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        TimeZone utc = TimeZone.getTimeZone(timeZone);
        sdf.setTimeZone(utc);
        return sdf;
    }
}
