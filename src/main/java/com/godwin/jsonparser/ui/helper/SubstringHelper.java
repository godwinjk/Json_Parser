package com.godwin.jsonparser.ui.helper;

import org.apache.http.util.TextUtils;

/**
 * Created by Godwin on 11/7/2018 11:25 AM for json.
 *
 * @author : Godwin Joseph Kurinjikattu
 */
public class SubstringHelper {

    public static LineData process(String rawData, String errorData) {
        if (!TextUtils.isEmpty(rawData)) {
            return new LineData(0, 15);
        }
        return null;
    }

    private int getLine(String errorData) {
        if (TextUtils.isEmpty(errorData))
            return 0;
        String[] arr = errorData.split(" ");
        for (int i = 0; i < arr.length; i++) {
            String s = arr[i];
            if (!TextUtils.isEmpty(s) && s.equalsIgnoreCase("line")) {
                if ((i + 1) < arr.length) {
                    String s1 = arr[i + 1];
                    if (s1.matches("\\d+"))
                        return Integer.parseInt(s1);
                }
            }
        }
        return 0;
    }

    private int getLineOffset(String errorData) {
        if (TextUtils.isEmpty(errorData))
            return 0;
        String[] arr = errorData.split(" ");
        for (int i = 0; i < arr.length; i++) {
            String s = arr[i];
            if (!TextUtils.isEmpty(s) && s.equalsIgnoreCase("column")) {
                if ((i + 1) < arr.length) {
                    String s1 = arr[i + 1];
                    if (s1.matches("\\d+"))
                        return Integer.parseInt(s1);
                }
            }
        }
        return 0;
    }

    public static class LineData {
        private int lineNumber;
        private int lineOffset;

        public LineData(int lineNumber, int lineOffset) {
            this.lineNumber = lineNumber;
            this.lineOffset = lineOffset;
        }

        public int getLineNumber() {
            return lineNumber;
        }

        public void setLineNumber(int lineNumber) {
            this.lineNumber = lineNumber;
        }

        public int getLineOffset() {
            return lineOffset;
        }

        public void setLineOffset(int lineOffset) {
            this.lineOffset = lineOffset;
        }
    }
}
