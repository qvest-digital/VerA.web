#GENERAL MESSAGE CREATOR
#GENERATES GM Placeholders automatically
#To the entities which also could be added as well as created (i.e: Categories), please add in (#ADDED SECTION) section
#This will created an additional GM_ADD message for these
#To execute the script, simply type "sh script.sh", and to store in a file "sh script.sh > file.resource"
#The messages come in "#GENERAL MESSAGES" Section in the .resource file

kwS="Es wurde ein"
kwP="Es wurden %s"
kwN="Es wurde kein"

add_MODIFY=""
add_INSERT=""
add_DELETE=""

kw_MODIFY="geändert"
kw_DELETE="gelöscht"
kw_INSERT="angelegt"
kw_ADD="hinzugefügt"

#Added entitites
added() {
	keyword=$1
	addedwords="$addedwords $keyword"
}

# word gender keyword singular plural [none=singular]
word() {
	gender=$1
	keyword=$2
	singular=$3
	plural=${4:-${singular%e}en}
	none=${5:-$singular}

	alltypes="$alltypes $keyword"
	eval g_$2=\$gender kwS$2=\$singular kwP$2=\$plural kwN$2=\$none
}

#ADDED SECTION
. ./added.sh

#ENTITIES
word F CATEGORY Kategorie
word M CLIENT Mandant Mandanten
word F COLOR Farbe
word N DELEGATION_FIELD Zusatzfeld Zusatzfelder
word M DISTRIBUTOR Verteiler Verteiler
word M DOCUMENT_TYPE Dokumenttyp Dokumenttypen
word F EMAIL "E-mail" "E-mails"
word F EVENT Veranstaltung Veranstaltungen
word M EVENT_LOCATION Veranstaltungsort Veranstaltungsorte
word M GUEST Gast Gäste
word F MAIL_DRAFT Vorlage
word F OFFICIAL_TITLE Amtsbezeichnung
word F PERSON Person
word M REPRESENTATIVE Vertreter Vertreter
word F SALUTATION Anrede
word F TASK Aufgabe
word M USER Benutzer Benutzer
word M WORKAREA Arbeitsbereich Arbeitsbereiche

	neuMS=neuer neuMP=neue neuMN=neuer
	neuFS=neue neuFP=neue neuFN=neue
	neuNS=neues neuNP=neue neuNN=neues

for x in $alltypes; do
	eval g=\$g_$x
	if test $g = F; then
		geS=e geP= geN=e
	else
		geS= geP= geN=
	fi
	for y in MODIFY DELETE INSERT ADD; do
		for z in S P N; do
			if test $y = ADD; then
				case "$addedwords" in *"$x"*)
					eval "echo GM_${x}_${y}_${z}=\$kw$z\$ge$z \$neu$g$z \$kw$z$x \$kw_$y."
				esac
			else
				eval "echo GM_${x}_${y}_${z}=\$kw$z\$ge$z \$neu$g$z \$kw$z$x \$kw_$y."
			fi
		done
	done
done
