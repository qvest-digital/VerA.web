package de.tarent.aa.veraweb.beans;
/**
 * @author cklein
 */
public class Duration extends AbstractBean {
    public static String DEFAULT_FORMAT = "%Y%M%D";

    public Integer years;
    public Integer months;
    public Integer days;

    /**
     *
     */
    public Duration() {
        super();
        this.years = new Integer(0);
        this.months = new Integer(0);
        this.days = new Integer(0);
    }

    /**
     * @param value a string value representing a serialized period in the form P[y]Y[m]M[d]D, e.g. P5Y2M1D or P2M3D
     * @return FIXME
     */
    public static Duration fromString(String value) {
        String regex = "^P([0-9]+Y)?([0-9]+M)?([0-9]+D)?$";
        Duration result = new Duration();
        if (value == null) {
            value = "P0";
        }
        if (value.matches(regex)) {
            int amount = 0;
            for (int i = 1; i < value.length(); i++) {
                if (Character.isDigit(value.charAt(i))) {
                    amount *= 10;
                    amount += value.charAt(i) - 48;
                } else {
                    Integer nval = new Integer(amount);
                    amount = 0;
                    switch (value.charAt(i)) {
                    case 'Y': {
                        result.years = nval;
                        amount = 0;
                        break;
                    }
                    case 'M': {
                        result.months = nval;
                        break;
                    }
                    case 'D': {
                        result.days = nval;
                        break;
                    }
                    }
                }
            }
        }
        return result;
    }

    /**
     * The format string fmt is defined as follows (all output strings are in german, no localization is supported!):
     *
     * %d - the number of days plus the word Tag(e) in its either singular or plural form
     * %m - the number of months plus the word Monat(e) in its either singular or plural form
     * %y - the number of years plus the word Jahr(e) in its either singular or plural form
     * %D - same as %d but the resulting string will not contain the number of days if these are equal to 0
     * %M - same as %m but the resulting string will not contain the number of months if these are equal to 0
     * %Y - same as %y but the resulting string will not contain the number of years if these are equal to 0
     * %% - the percentage character
     *
     * @param fmt The format
     * @return String
     */
    public String toFormattedString(String fmt) {
        StringBuffer temp = new StringBuffer();
        char c = 0;
        String s = "";
        int val = 0;
        for (int i = 0; i < fmt.length(); i++) {
            c = fmt.charAt(i);
            switch (c) {
            case '%': {
                i++;
                c = fmt.charAt(i);
                switch (c) {
                case '%': {
                    temp.append('%');
                    break;
                }
                case 'y':
                case 'Y':
                case 'm':
                case 'M':
                case 'd':
                case 'D': {
                    if (c == 'y' || c == 'Y') {
                        val = this.years.intValue();
                        s = " Jahr";
                    } else if (c == 'm' || c == 'M') {
                        val = this.months.intValue();
                        s = " Monat";
                    } else {
                        val = this.days.intValue();
                        s = " Tag";
                    }
                    if (val != 0 || c == 'y' || c == 'm' || c == 'd') {
                        if (temp.length() > 1 && temp.charAt(temp.length() - 1) != ' ') {
                            temp.append(' ');
                        }
                        temp.append(val);
                        temp.append(s);
                        if (val > 1 || val == 0) {
                            temp.append("e");
                        }
                    }
                    break;
                }
                default: {
                    // unsupported
                    temp.append('%');
                    temp.append(fmt.charAt(i));
                    break;
                }
                }
                break;
            }
            default: {
                temp.append(fmt.charAt(i));
                break;
            }
            }
        }
        return temp.toString();
    }

    public String toFormattedString() {
        return this.toFormattedString(Duration.DEFAULT_FORMAT);
    }

    @Override
    public String toString() {
        StringBuffer temp = new StringBuffer();
        temp.append('P');
        if (this.years.intValue() != 0) {
            temp.append(this.years.intValue());
            temp.append('Y');
        }
        if (this.months.intValue() != 0) {
            temp.append(this.months.intValue());
            temp.append('M');
        }
        if (this.days.intValue() != 0) {
            temp.append(this.days.intValue());
            temp.append('D');
        }
        // is this a zero length duration?
        if (temp.length() == 1) {
            temp.append('0');
        }
        return temp.toString();
    }
}
