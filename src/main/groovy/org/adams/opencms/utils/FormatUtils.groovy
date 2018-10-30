package org.adams.opencms.utils

import java.text.SimpleDateFormat

class FormatUtils {

    private static final String DATE_FORMAT = 'EEE, dd MMM yyyy HH:mm:ss z'
    public static SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT)

    private static final String SIMPLE_DATE_FORMAT = 'yyyy-mm-dd HH:mm:ss'
    public static SimpleDateFormat simpleFormatter = new SimpleDateFormat(SIMPLE_DATE_FORMAT)




}
