package utils;

import com.postaplus.etrack.BagConsolidateActivity;
import com.postaplus.etrack.ManifestActivity;
import com.postaplus.etrack.WC_MaxActivity;

import org.apache.commons.lang3.StringUtils;

public class Utils {
    public static boolean checkValidWaybill(String s) {

        if (s.length() == 10 || s.length() == 12) {
            return StringUtils.isNumeric(s);
        } else if (s.length() == 18 || s.length() == 13) {
            return StringUtils.isAlphanumeric(s);
        }
        return false;
    }


    public static boolean checkValidAlphaWaybill(String s) {
        if (s.length() == 10 || s.length() == 12 || s.length() == 13 || s.length() == 18) {
            return StringUtils.isAlphanumeric(s);
        }
        return false;
    }
}
