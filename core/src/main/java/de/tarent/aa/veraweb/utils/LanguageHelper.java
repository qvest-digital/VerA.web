package de.tarent.aa.veraweb.utils;
import java.util.Locale;
import java.util.Map;
/*
 * Language Helper was implemented to create a general message when deleting, creating or updating any entity
 * This utility is called from velocity through sending it to the OctopusContext
 */

public class LanguageHelper {

    final Map<String, String> placeholderWithTranslation;

    public LanguageHelper(Map<String, String> placeholderWithTranslation) {
        this.placeholderWithTranslation = placeholderWithTranslation;
    }

    @Deprecated
    public String createMessage(String entity,
      String action,
      String count,
      Map<String, String> placeholderWithTranslation) {
        return createMessage(entity, action, count);
    }

    public String createMessage(String entity,
      String action,
      String count) {

        String message;
        // singular or plural
        String singularOrPluralOrNone;
        if (count.equals("0")) {
            singularOrPluralOrNone = "N";
        } else if (count.equals("1")) {
            singularOrPluralOrNone = "S";
        } else {
            singularOrPluralOrNone = "P";
        }

        String placeholdername = "GM_" + entity + "_" + action + "_" + singularOrPluralOrNone;
        if (singularOrPluralOrNone.equals("P")) {
            message = String.format(placeholderWithTranslation.get(placeholdername), count);
        } else {
            message = placeholderWithTranslation.get(placeholdername);
        }

        return message;
    }

    public String makeFirstLetterLowerCase(String input) {
        Locale.setDefault(new Locale("en"));
        char c[] = input.toCharArray();
        c[0] = Character.toLowerCase(c[0]);
        String LowerCase = new String(c);

        return LowerCase;
    }

    // velocity 1.4 does not support varargs...
    public String l10n(String code) {
        return translate(code);
    }

    // velocity 1.4 does not support varargs...
    public String l10n(String code, Object arg0) {
        return translate(code, arg0);
    }

    // velocity 1.4 does not support varargs...
    public String l10n(String code, Object arg0, Object arg1) {
        return translate(code, arg0, arg1);
    }

    // velocity 1.4 does not support varargs...
    public String l10n(String code, Object arg0, Object arg1, Object arg2) {
        return translate(code, arg0, arg1, arg2);
    }

    // velocity 1.4 does not support varargs...
    public String l10n(String code, Object arg0, Object arg1, Object arg2, Object arg3) {
        return translate(code, arg0, arg1, arg2, arg3);
    }

    public String translate(String code, Object... args) {
        String format = placeholderWithTranslation.containsKey(code) ? placeholderWithTranslation.get(code) : code;
        return String.format(format, args);
    }
}
