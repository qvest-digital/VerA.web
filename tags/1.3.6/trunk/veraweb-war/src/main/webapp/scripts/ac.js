var y="";var Pb=false;var fa=false;var g="";var H="";var n="";var k=-1;var i=null;var Z=-1;var Ia=null;var Na=5;var r="";var Tb="div";var Kb="span";var sa=null;var a=null;var b=null;var jb=null;var xb=null;var W=null;var la=null;var za=false;var fb=new Object();var ea=1;var Ma=1;var va=false;var aa=false;var ua=-1;var gb=(new Date()).getTime();var P=false;var l=null;var Aa=null;var F=null;var N=null;var ca=null;var ka=false;var Ua=false;var q=60;var na=null;var Ha=null;var V=0;var hb=null;var ma=null;var oa=null;var Ea=false;var X=false;InstallAC=function(frm,fld,sb,pn,rl,hd,sm,ufn){sa=frm;a=fld;jb=sb;if(!pn)pn="search";na=pn;var Sb="zh|zh-CN|zh-TW|ja|ko|";if(!rl||rl.length<1)rl="en";la=yb(rl);if(Sb.indexOf(la+"|")==-1){W=true;aa=false;va=false;ka=false}else{W=false;aa=true;if(la.indexOf("zh")==0)va=false;ka=true}if(!hd)hd=false;Ha=hd;if(!sm)sm="query";y=sm;xb=ufn;kc()}
;function ub(){za=true;a.blur();setTimeout("sfi();",10);return}
function Ob(){if(document.createEventObject){var Ka=document.createEventObject();Ka.ctrlKey=true;Ka.keyCode=70;document.fireEvent("onkeydown",Ka)}}
function Xb(h){if(!h&&window.event)h=window.event;if(h)ua=h.keyCode;if(h&&h.keyCode==8){if(W&&(a.createTextRange&&(h.srcElement==a&&(ja(a)==0&&qa(a)==0)))){nc(a);h.cancelBubble=true;h.returnValue=false;return false}}}
function Mb(){if(y=="url"){Sa()}da()}
function da(){if(b){b.style.left=zb(a)+"px";b.style.top=Yb(a)+a.offsetHeight-1+"px";b.style.width=Ta()+"px"}}
function Ta(){if(navigator&&navigator.userAgent.toLowerCase().indexOf("msie")==-1){return a.offsetWidth-ea*2}else{return a.offsetWidth}}
function kc(){if(vb()){P=true}else{P=false}if(Pb)F="complete";else F="/complete/"+na;Aa=F+"?hl="+la;if(!P){ya("qu","",0,F,null,null)}sa.onsubmit=Qa;a.autocomplete="off";a.onblur=Wb;a.onfocus=lc;if(a.createTextRange)a.onkeyup=new Function("return okuh(event); ");else a.onkeyup=okuh;a.onsubmit=Qa;g=a.value;Ba=g;b=document.createElement("DIV");b.id="completeDiv";ea=1;Ma=1;b.style.borderRight="black "+ea+"px solid";b.style.borderLeft="black "+ea+"px solid";b.style.borderTop="black "+Ma+"px solid";b.style.borderBottom="black "+Ma+"px solid";b.style.zIndex="1";b.style.paddingRight="0";b.style.paddingLeft="0";b.style.paddingTop="0";b.style.paddingBottom="0";da();b.style.visibility="hidden";b.style.position="absolute";b.style.backgroundColor="white";document.body.appendChild(b);Wa("",new Array(),new Array());Qb(b);var t=document.createElement("DIV");t.style.visibility="hidden";t.style.position="absolute";t.style.left="-10000";t.style.top="-10000";t.style.width="0";t.style.height="0";var L=document.createElement("IFRAME");L.completeDiv=b;L.name="completionFrame";L.id="completionFrame";L.src=Aa;t.appendChild(L);document.body.appendChild(t);if(frames&&(frames["completionFrame"]&&frames["completionFrame"].frameElement))N=frames["completionFrame"].frameElement;else N=document.getElementById("completionFrame");if(y=="url"){Sa();da()}window.onresize=Mb;document.onkeydown=Xb;Ob();if(ka){setTimeout("idkc()",10);if(a.attachEvent){a.attachEvent("onpropertychange",Zb)}}}
function lc(h){X=true}
function Wb(h){X=false;if(!h&&window.event)h=window.event;if(!za){G();if(ua==9){jc();ua=-1}}za=false}
okuh=function(e){if(!Ea){Ea=true}n=e.keyCode;ca=a.value;Xa()}
;function jc(){jb.focus()}
sfi=function(){a.focus()}
;function hc(Da){for(var c=0,wa="",Ib="\n\r";c<Da.length;c++)if(Ib.indexOf(Da.charAt(c))==-1)wa+=Da.charAt(c);else wa+=" ";return wa}
function Za(j,oc){var ia=j.getElementsByTagName(Kb);if(ia){for(var c=0;c<ia.length;++c){if(ia[c].className==oc){var Y=ia[c].innerHTML;if(Y=="&nbsp;")return"";else{var A=hc(Y);return A}}}}else{return""}}
function T(j){if(!j)return null;return Za(j,"cAutoComplete")}
function Fa(j){if(!j)return null;return Za(j,"dAutoComplete")}
function G(){document.getElementById("completeDiv").style.visibility="hidden"}
function nb(){document.getElementById("completeDiv").style.visibility="visible";da()}
function Wa(is,cs,ds){fb[is]=new Array(cs,ds)}
sendRPCDone=function(fr,is,cs,ds,pr){if(V>0)V--;var rc=(new Date()).getTime();if(!fr)fr=N;Wa(is,cs,ds);var b=fr.completeDiv;b.completeStrings=cs;b.displayStrings=ds;b.prefixStrings=pr;Cb(b,b.completeStrings,b.displayStrings);Ya(b,T);if(Na>0)b.height=16*Na+4;else G()}
;function Xa(){if(n==40||n==38)ub();var M=qa(a);var w=ja(a);var U=a.value;if(W&&n!=0){if(M>0&&w!=-1)U=U.substring(0,w);if(n==13||n==3){var f=a;if(f.createTextRange){var u=f.createTextRange();u.moveStart("character",f.value.length);u.select()}else if(f.setSelectionRange){f.setSelectionRange(f.value.length,f.value.length)}}else{if(a.value!=U)R(U)}}g=U;if(Nb(n)&&n!=0)Ya(b,T)}
function Qa(){return Gb(y)}
function Gb(pb){fa=true;if(!P){ya("qu","",0,F,null,null)}G();if(pb=="url"){var Q="";if(k!=-1&&i)Q=T(i);if(Q=="")Q=a.value;if(r=="")document.title=Q;else document.title=r;var dc="window.frames['"+xb+"'].location = \""+Q+'";';setTimeout(dc,10);return false}else if(pb=="query"){sa.submit();return true}}
newwin=function(){window.open(a.value);G();return false}
;idkc=function(e){if(ka){if(X){eb()}var db=a.value;if(db!=ca){n=0;Xa()}ca=db;setTimeout("idkc()",10)}}
;function yb(Va){if(encodeURIComponent)return encodeURIComponent(Va);if(escape)return escape(Va)}
function Hb(Ub){var I=100;for(var p=1;p<=(Ub-2)/2;p++){I=I*2}I=I+50;return I}
idfn=function(){if(Ba!=g){if(!fa){var lb=yb(g);var ta=fb[g];if(ta){gb=-1;sendRPCDone(N,g,ta[0],ta[1],N.completeDiv.prefixStrings)}else{V++;gb=(new Date()).getTime();if(P){qc(lb)}else{ya("qu",lb,null,F,null,null);frames["completionFrame"].document.location.reload(true)}}a.focus()}fa=false}Ba=g;setTimeout("idfn()",Hb(V));return true}
;setTimeout("idfn()",10);var Lb=function(){R(T(this));r=Fa(this);fa=true;Qa()}
;var Ab=function(){if(i)m(i,"aAutoComplete");m(this,"bAutoComplete")}
;var pc=function(){m(this,"aAutoComplete")}
;function xa(D){g=H;R(H);r=H;if(!Ia||Z<=0)return;nb();if(D>=Z){D=Z-1}if(k!=-1&&D!=k){m(i,"aAutoComplete");k=-1}if(D<0){k=-1;a.focus();return}k=D;i=Ia.item(D);m(i,"bAutoComplete");g=H;r=Fa(i);R(T(i))}
function Nb(pa){if(pa==40){xa(k+1);return false}else if(pa==38){xa(k-1);return false}else if(pa==13||pa==3){return false}return true}
function Ya(K,Pa){var f=a;var S=false;k=-1;var B=K.getElementsByTagName(Tb);var O=B.length;Z=O;Ia=B;Na=O;H=g;if(g==""||O==0){G()}else{nb()}var Jb="";if(g.length>0){var c;var p;for(var c=0;c<O;c++){for(p=0;p<K.prefixStrings.length;p++){var cb=K.prefixStrings[p]+g;if(va||(!aa&&Pa(B.item(c)).toUpperCase().indexOf(cb.toUpperCase())==0||aa&&(c==0&&Pa(B.item(c)).toUpperCase()==cb.toUpperCase()))){Jb=K.prefixStrings[p];S=true;break}}if(S){break}}}if(S)k=c;for(var c=0;c<O;c++)m(B.item(c),"aAutoComplete");if(S){i=B.item(k);r=Fa(i)}else{r=g;k=-1;i=null}var mb=false;switch(n){case 8:case 33:case 34:case 35:case 35:case 36:case 37:case 39:case 45:case 46:mb=true;break;default:break}if(!mb&&i){var Oa=g;m(i,"bAutoComplete");var A;if(S)A=Pa(i).substr(K.prefixStrings[p].length);else A=Oa;if(A!=f.value){if(f.value!=g)return;if(W){if(f.createTextRange||f.setSelectionRange)R(A);if(f.createTextRange){var u=f.createTextRange();u.moveStart("character",Oa.length);u.select()}else if(f.setSelectionRange){f.setSelectionRange(Oa.length,f.value.length)}}}}else{k=-1;r=g}}
function zb(s){return kb(s,"offsetLeft")}
function Yb(s){return kb(s,"offsetTop")}
function kb(s,na){var wb=0;while(s){wb+=s[na];s=s.offsetParent}return wb}
function ya(z,Y,ab,tb,qb,cc){var Vb=z+"="+Y+(ab?"; expires="+ab.toGMTString():"")+(tb?"; path="+tb:"")+(qb?"; domain="+qb:"")+(cc?"; secure":"");document.cookie=Vb}
function Sa(){var Ga=document.body.scrollWidth-220;Ga=0.73*Ga;a.size=Math.floor(Ga/6.18)}
function qa(o){var M=-1;if(o.createTextRange){var ha=document.selection.createRange().duplicate();M=ha.text.length}else if(o.setSelectionRange){M=o.selectionEnd-o.selectionStart}return M}
function ja(o){var w=0;if(o.createTextRange){var ha=document.selection.createRange().duplicate();ha.moveEnd("textedit",1);w=o.value.length-ha.text.length}else if(o.setSelectionRange){w=o.selectionStart}else{w=-1}return w}
function nc(f){if(f.createTextRange){var u=f.createTextRange();u.moveStart("character",f.value.length);u.select()}else if(f.setSelectionRange){f.setSelectionRange(f.value.length,f.value.length)}}
function m(d,z){ob();d.className=z;if(Ua){return}switch(z.charAt(0)){case "m":d.style.fontSize="13px";d.style.fontFamily="arial,sans-serif";d.style.wordWrap="break-word";break;case "l":d.style.display="block";d.style.paddingLeft="3";d.style.paddingRight="3";d.style.height="16px";d.style.overflow="hidden";break;case "a":d.style.backgroundColor="white";d.style.color="black";if(d.displaySpan){d.displaySpan.style.color="green"}break;case "b":d.style.backgroundColor="#3366cc";d.style.color="white";if(d.displaySpan){d.displaySpan.style.color="white"}break;case "c":d.style.width=q+"%";d.style.cssFloat="left";break;case "d":d.style.cssFloat="right";d.style.width=100-q+"%";if(y=="query"){d.style.fontSize="10px";d.style.textAlign="right";d.style.color="green";d.style.paddingTop="3px"}else{d.style.color="#696969"}break}}
function ob(){q=65;if(y=="query"){var Fb=110;var bb=Ta();var Db=(bb-Fb)/bb*100;q=Db}else{q=65}if(Ha){q=99.99}}
function Qb(j){ob();var ec="font-size: 13px; font-family: arial,sans-serif; word-wrap:break-word;";var gc="display: block; padding-left: 3; padding-right: 3; height: 16px; overflow: hidden;";var mc="background-color: white;";var Bb="background-color: #3366cc; color: white ! important;";var Eb="display: block; margin-left: 0%; width: "+q+"%; float: left;";var Ra="display: block; margin-left: "+q+"%;";if(y=="query"){Ra+="font-size: 10px; text-align: right; color: green; padding-top: 3px;"}else{Ra+="color: #696969;"}E(".mAutoComplete",ec);E(".lAutoComplete",gc);E(".aAutoComplete *",mc);E(".bAutoComplete *",Bb);E(".cAutoComplete",Eb);E(".dAutoComplete",Ra);m(j,"mAutoComplete")}
function Cb(j,cs,Rb){while(j.childNodes.length>0)j.removeChild(j.childNodes[0]);for(var c=0;c<cs.length;++c){var v=document.createElement("DIV");m(v,"aAutoComplete");v.onmousedown=Lb;v.onmouseover=Ab;v.onmouseout=pc;var ra=document.createElement("SPAN");m(ra,"lAutoComplete");var Ca=document.createElement("SPAN");Ca.innerHTML=cs[c];var ga=document.createElement("SPAN");m(ga,"dAutoComplete");m(Ca,"cAutoComplete");v.displaySpan=ga;if(!Ha)ga.innerHTML=Rb[c];ra.appendChild(Ca);ra.appendChild(ga);v.appendChild(ra);j.appendChild(v)}}
function E(z,rb){if(Ua){var J=document.styleSheets[0];if(J.addRule){J.addRule(z,rb)}else if(J.insertRule){J.insertRule(z+" { "+rb+" }",J.cssRules.length)}}}
function vb(){var C=null;try{C=new ActiveXObject("Msxml2.XMLHTTP")}catch(e){try{C=new ActiveXObject("Microsoft.XMLHTTP")}catch(sc){C=null}}if(!C&&typeof XMLHttpRequest!="undefined"){C=new XMLHttpRequest()}return C}
function qc(ac){if(l&&l.readyState!=0){l.abort()}l=vb();if(l){l.open("GET","/veraweb/do/CompleteLocation?query="+ac,true);l.onreadystatechange=function(){if(l.readyState==4&&l.responseText){if(l.responseText.charAt(0)=="<"){V--}else{eval(l.responseText)}}}
;l.send(null)}}
function R(ib){a.value=ib;ca=ib}
function Zb(h){if(!h&&window.event)h=window.event;if(!Ea&&(X&&h.propertyName=="value")){if(fc()){eb();setTimeout("ba("+ma+", "+oa+");",30)}}}
function fc(){var ic=a.value;var La=ja(a);var Ja=qa(a);return La==ma&&(Ja==oa&&ic==hb)}
function eb(){hb=a.value;ma=ja(a);oa=qa(a)}
ba=function(La,Ja){if(La==ma&&Ja==oa){bc()}}
;function bc(){ub();xa(k+1)}


