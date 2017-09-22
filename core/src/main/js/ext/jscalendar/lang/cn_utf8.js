// ** I18N

// Calendar EN language
// Author: Mihai Bazon, <mishoo@infoiasi.ro>
// Encoding: any
// Translator : Niko <nikoused@gmail.com>
// Distributed under the same terms as the calendar itself.

// For translators: please use UTF-8 if possible.  We strongly believe that
// Unicode is the answer to a real internationalized world.  Also please
// include your contact information in the header, as can be seen above.

// full day names
Calendar._DN = new Array
("周日",//周日
 "周一",//周一
 "周二",//周二
 "周三",//周三
 "周四",//周四
 "周五",//周五
 "周六",//周六
 "周日");//周日

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
("周日",
 "周一",
 "周二",
 "周三",
 "周四",
 "周五",
 "周六",
 "周日");

// full month names
Calendar._MN = new Array
("一月",
 "二月",
 "三月",
 "四月",
 "五月",
 "六月",
 "七月",
 "八月",
 "九月",
 "十月",
 "十一月",
 "十二月");

// short month names
Calendar._SMN = new Array
("一月",
 "二月",
 "三月",
 "四月",
 "五月",
 "六月",
 "七月",
 "八月",
 "九月",
 "十月",
 "十一月",
 "十二月");

// tooltips
Calendar._TT = {};
Calendar._TT["INFO"] = "关于";

Calendar._TT["ABOUT"] =
"   DHTML 日起/时间选择控件\n" +
"(c) dynarch.com 2002-2005 / Author: Mihai Bazon\n" + // don't translate this this ;-)
"For latest version visit: 最新版本请登陆http://www.dynarch.com/projects/calendar/察看\n" +
"遵循GNU LGPL.  细节参阅 http://gnu.org/licenses/lgpl.html" +
"\n\n" +
"日期选择:\n" +
"- 点击\xab(\xbb)按钮选择上(下)一年度.\n" +
"- 点击" + String.fromCharCode(0x2039) + "(" + String.fromCharCode(0x203a) + ")按钮选择上(下)个月份.\n" +
"- 长时间按着按钮将出现更多选择项.";
Calendar._TT["ABOUT_TIME"] = "\n\n" +
"时间选择:\n" +
"-在时间部分(分或者秒)上单击鼠标左键来增加当前时间部分(分或者秒)\n" +
"-在时间部分(分或者秒)上按住Shift键后单击鼠标左键来减少当前时间部分(分或者秒).";

Calendar._TT["PREV_YEAR"] = "上一年";
Calendar._TT["PREV_MONTH"] = "上个月";
Calendar._TT["GO_TODAY"] = "到今天";
Calendar._TT["NEXT_MONTH"] = "下个月";
Calendar._TT["NEXT_YEAR"] = "下一年";
Calendar._TT["SEL_DATE"] = "选择日期";
Calendar._TT["DRAG_TO_MOVE"] = "拖动";
Calendar._TT["PART_TODAY"] = " (今天)";

// the following is to inform that "%s" is to be the first day of week
// %s will be replaced with the day name.
Calendar._TT["DAY_FIRST"] = "%s为这周的第一天";

// This may be locale-dependent.  It specifies the week-end days, as an array
// of comma-separated numbers.  The numbers are from 0 to 6: 0 means Sunday, 1
// means Monday, etc.
Calendar._TT["WEEKEND"] = "0,6";

Calendar._TT["CLOSE"] = "关闭";
Calendar._TT["TODAY"] = "今天";
Calendar._TT["TIME_PART"] = "(按着Shift键)单击或拖动改变值";

// date formats
Calendar._TT["DEF_DATE_FORMAT"] = "%Y-%m-%d";
Calendar._TT["TT_DATE_FORMAT"] = "%a, %b %e日";

Calendar._TT["WK"] = "周";
Calendar._TT["TIME"] = "时间:";
