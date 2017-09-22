// ** I18N

// Calendar AR language
// Author: Karim Ratib, <Karim.Ratib@open-craft.com>
// Encoding: UTF-8
// Distributed under the same terms as the calendar itself.

// For translators: please use UTF-8 if possible.  We strongly believe that
// Unicode is the answer to a real internationalized world.  Also please
// include your contact information in the header, as can be seen above.

// full day names
Calendar._DN = new Array
("الأحد",
 "الإثنين",
 "الثلاثاء",
 "الأربعاء",
 "الخميس",
 "الجمعة",
 "السبت",
 "الأحد");

// Please note that the following array of short day names (and the same goes
// for short month names, _SMN) isn't absolutely necessary.  We give it here
// for exemplification on how one can customize the short day names, but if
// they are simply the first N letters of the full name you can simply say:
//
//   Calendar._SDN_len = N; // short day name length
//   Calendar._SMN_len = N; // short month name length
//
// If N = 3 then this is not needed either since we assume a value of 3 if not
// present, to be compatible with translation files that were written before
// this feature.

// short day names
Calendar._SDN = new Array
("أحد",
 "إثنين",
 "ثلاثاء",
 "أربعاء",
 "خميس",
 "جمعة",
 "سبت",
 "أحد");

// full month names
Calendar._MN = new Array
("يناير",
 "فبراير",
 "مارس",
 "ابريل",
 "مايو",
 "يونيو",
 "يوليو",
 "اغسطس",
 "سبتمبر",
 "اكتوبر",
 "نوفمبر",
 "ديسمبر");

// short month names
Calendar._SMN = new Array
("يناير",
 "فبراير",
 "مارس",
 "ابريل",
 "مايو",
 "يونيو",
 "يوليو",
 "اغسطس",
 "سبتمبر",
 "اكتوبر",
 "نوفمبر",
 "ديسمبر");

// tooltips
Calendar._TT = {};
Calendar._TT["INFO"] = "حول التقويم";

Calendar._TT["ABOUT"] =
"DHTML Date/Time Selector\n" +
"(c) dynarch.com 2002-2003\n" + // don't translate this this ;-)
"For latest version visit: http://dynarch.com/mishoo/calendar.epl\n" +
"Distributed under GNU LGPL.  See http://gnu.org/licenses/lgpl.html for details." +
"\n\n" +
"كيفية تحديد التاريخ:\n" +
"- أنقر أزرار \xab و \xbb لإختيار السنة\n" +
"- أنقر أزرار " + String.fromCharCode(0x2039) + " و " + String.fromCharCode(0x203a) + " لإختيار الشهر\n" +
"- أنقر بإستمرار هذه الأزرار لعرض قائمة الإختيار السريع";
Calendar._TT["ABOUT_TIME"] = "\n\n" +
"كيفية إختيار التوقيت:\n" +
"- أنقر أجزاء التوقيت لزيادتها\n" +
"- أنقر+SHIFT أجزاء التوقيت لإنقاصها\n" +
"- أنقر و أسحب للإختيار السريع";

Calendar._TT["PREV_YEAR"] = "السنة السابقة - الضغط باستمرار لعرض القائمة";
Calendar._TT["PREV_MONTH"] = "الشهر السابق - الضغط باستمرار لعرض القائمة";
Calendar._TT["GO_TODAY"] = "اليوم الحالى";
Calendar._TT["NEXT_MONTH"] = "الشهر التالى - الضغط باستمرار لعرض القائمة";
Calendar._TT["NEXT_YEAR"] = "السنة التالية - الضغط باستمرار لعرض القائمة";
Calendar._TT["SEL_DATE"] = "أختر التاريخ";
Calendar._TT["DRAG_TO_MOVE"] = "أسحب للنقل";
Calendar._TT["PART_TODAY"] = " (اليوم الحالى)";

// the following is to inform that "%s" is to be the first day of week
// %s will be replaced with the day name.
Calendar._TT["DAY_FIRST"] = "عرض %s أولا";

// This may be locale-dependent.  It specifies the week-end days, as an array
// of comma-separated numbers.  The numbers are from 0 to 6: 0 means Sunday, 1
// means Monday, etc.
Calendar._TT["WEEKEND"] = "0,6";

Calendar._TT["CLOSE"] = "غلق";
Calendar._TT["TODAY"] = "اليوم الحالى";
Calendar._TT["TIME_PART"] = "أنقر (+SHIFT) أو أسحب لتغيير القيمة";

// date formats
Calendar._TT["DEF_DATE_FORMAT"] = "%Y-%m-%d";
Calendar._TT["TT_DATE_FORMAT"] = "%a, %b %e";

Calendar._TT["WK"] = "أسبوع";
Calendar._TT["TIME"] = "الوقت:";
