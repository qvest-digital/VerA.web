package de.tarent.commons.utils;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class VariableDateFormat extends SimpleDateFormat {
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -3591962069972843863L;

    public VariableDateFormat() {
        super();
    }

    private boolean isMonth(String input) {
        String[] monthNames = new DateFormatSymbols(Locale.getDefault()).getMonths();
        if (input != null && Arrays.asList(monthNames).contains(input)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isShortMonth(String input) {
        String[] monthNames = new DateFormatSymbols(Locale.getDefault()).getShortMonths();
        if (input != null && Arrays.asList(monthNames).contains(input)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isFormatA(String input) {
        // "Januar 2006"
        String[] tokens = input.split(" ");
        return isMonth(tokens[0]) && input.matches("[a-zA-Z]+ [0-9][0-9][0-9][0-9]");
    }

    private boolean isFormatB(String input) {
        // "Januar 06"
        String[] tokens = input.split(" ");
        return isMonth(tokens[0]) && input.matches("[a-zA-Z]+ [0-9][0-9]");
    }

    private boolean isFormatC(String input) {
        // "Jan 2006"
        String[] tokens = input.split(" ");
        return isShortMonth(tokens[0]) && input.matches("[a-zA-Z]+ [0-9][0-9][0-9][0-9]");
    }

    private boolean isFormatD(String input) {
        // "Jan 06"
        String[] tokens = input.split(" ");
        return isShortMonth(tokens[0]) && input.matches("[a-zA-Z]+ [0-9][0-9]");
    }

    private boolean isFormatE(String input) {
        // "1. Jan 2006"
        String[] tokens = input.split(" ");
        return tokens.length > 1 && isShortMonth(tokens[1]) && input.matches("[0-9]\\. [a-zA-Z]+ [0-9][0-9][0-9][0-9]");
    }

    private boolean isFormatF(String input) {
        // "1. Jan 06"
        String[] tokens = input.split(" ");
        return tokens.length > 1 && isShortMonth(tokens[1]) && input.matches("[0-9]\\. [a-zA-Z]+ [0-9][0-9]");
    }

    private boolean isFormatG(String input) {
        // "01. Jan 2006"
        String[] tokens = input.split(" ");
        return tokens.length > 1 && isShortMonth(tokens[1]) && input.matches("[0-9][0-9]\\. [a-zA-Z]+ [0-9][0-9][0-9][0-9]");
    }

    private boolean isFormatH(String input) {
        // "01. Jan 06"
        String[] tokens = input.split(" ");
        return tokens.length > 1 && isShortMonth(tokens[1]) && input.matches("[0-9][0-9]\\. [a-zA-Z]+ [0-9][0-9]");
    }

    private boolean isFormatI(String input) {
        // "1. Januar 2006"
        String[] tokens = input.split(" ");
        return tokens.length > 1 && isMonth(tokens[1]) && input.matches("[0-9]\\. [a-zA-Z]+ [0-9][0-9][0-9][0-9]");
    }

    private boolean isFormatJ(String input) {
        // "01. Januar 2006"
        String[] tokens = input.split(" ");
        return tokens.length > 1 && isMonth(tokens[1]) && input.matches("[0-9][0-9]\\. [a-zA-Z]+ [0-9][0-9][0-9][0-9]");
    }

    private boolean isFormatK(String input) {
        // "1. Januar 06"
        String[] tokens = input.split(" ");
        return tokens.length > 1 && isMonth(tokens[1]) && input.matches("[0-9]\\. [a-zA-Z]+ [0-9][0-9]");
    }

    private boolean isFormatL(String input) {
        // "01. Januar 06"
        String[] tokens = input.split(" ");
        return tokens.length > 1 && isMonth(tokens[1]) && input.matches("[0-9][0-9]\\. [a-zA-Z]+ [0-9][0-9]");
    }

    private boolean isFormatM(String input) {
        // "1.1.06"
        return input.matches("[0-9]\\.[0-9]\\.[0-9][0-9]");
    }

    private boolean isFormatN(String input) {
        // "1.1.2006"
        return input.matches("[0-9]\\.[0-9]\\.[0-9][0-9][0-9][0-9]");
    }

    private boolean isFormatO(String input) {
        // "01.01.2006"
        return input.matches("[0-9][0-9]\\.[0-9][0-9]\\.[0-9][0-9][0-9][0-9]");
    }

    private boolean isFormatP(String input) {
        // "01.01.06"
        return input.matches("[0-9][0-9]\\.[0-9][0-9]\\.[0-9][0-9]");
    }

    private boolean isFormatQ(String input) {
        // "2006-01-20 12:30:50.9"
        return input.matches("\\d\\d\\d\\d-\\d\\d-\\d\\d \\d\\d:\\d\\d:\\d\\d.\\d");
    }

    private boolean isFormatR(String input) {
        // "2006.20.01"
        return input.matches("\\d\\d\\d\\d.\\d\\d.\\d\\d");
    }

    public Date analyzeString(String input) throws ParseException {
        if (isFormatA(input)) {
            this.applyPattern("MMMMMMMMM yyyy");
            return parse(input);
        }
        if (isFormatB(input)) {
            this.applyPattern("MMMMMMMMM yy");
            return parse(input);
        }
        if (isFormatC(input)) {
            this.applyPattern("MMM yy");
            return parse(input);
        }
        if (isFormatD(input)) {
            this.applyPattern("MMM yyyy");
            return parse(input);
        }
        if (isFormatE(input)) {
            this.applyPattern("d. MMM yyyy");
            return parse(input);
        }
        if (isFormatF(input)) {
            this.applyPattern("d. MMM yy");
            return parse(input);
        }
        if (isFormatG(input)) {
            this.applyPattern("dd. MMM yyyy");
            return parse(input);
        }
        if (isFormatH(input)) {
            this.applyPattern("dd. MMM yy");
            return parse(input);
        }
        if (isFormatI(input)) {
            this.applyPattern("d. MMMMMMMMM yyyy");
            return parse(input);
        }
        if (isFormatJ(input)) {
            this.applyPattern("dd. MMMMMMMMM yyyy");
            return parse(input);
        }
        if (isFormatK(input)) {
            this.applyPattern("d. MMMMMMMMM yy");
            return parse(input);
        }
        if (isFormatL(input)) {
            this.applyPattern("dd. MMMMMMMMM yy");
            return parse(input);
        }
        if (isFormatM(input)) {
            this.applyPattern("d.M.yy");
            return parse(input);
        }
        if (isFormatN(input)) {
            this.applyPattern("d.M.yyyy");
            return parse(input);
        }
        if (isFormatO(input)) {
            this.applyPattern("dd.MM.yyyy");
            return parse(input);
        }
        if (isFormatP(input)) {
            this.applyPattern("dd.MM.yy");
            return parse(input);
        }
        if (isFormatQ(input)) {
            this.applyPattern("yyyy-MM-dd HH:mm:ss.S");
            return parse(input);
        }
        if (isFormatR(input)) {
            this.applyPattern("yyyy.dd.MM");
            return parse(input);
        } else {
            throw new ParseException("Unknow date string format. Can't parse.", 0);
        }
    }
}
