#GENERAL MESSAGE CREATOR
#GENERATES GM Placeholders automatically
#To the entities which also could be added as well as created (i.e: Categories), please add in (#ADDED SECTION) section
#This will created an additional GM_ADD message for these
#To execute the script, simply type "sh script.sh", and to store in a file "sh script.sh > file.resource"
#The messages come in "#GENERAL MESSAGES" Section in the .resource file

kwS="One"
kwP="%s"
kwN="No"

veS="successfully"
veP="successfully"
veN=""

kw_MODIFY="modified"
kw_DELETE="deleted"
kw_INSERT="created"
kw_ADD="added"
new_INSERT="new"

added() {
	keyword=$1
	addedwords="$addedwords $keyword"
}

word() {
	keyword=$1
	singular=$2
	plural=${3:-$singular}s

	allwords="$allwords $keyword"
	eval g_$1=\$gender kwS$1=\$singular kwP$1=\$plural kwN$1=\$plural
}


#ADDED SECTION
. ./added.sh

#ENTITIES
word CATEGORY category categorie
word CLIENT client
word COLOR color
word DELEGATION_FIELD "delegation field"
word DISTRIBUTOR distributor
word DOCUMENT_TYPE "document type"
word EMAIL e-mail
word EVENT event
word EVENT_LOCATION Venue
word GUEST guest
word MAIL_DRAFT "mail draft"
word OFFICIAL_TITLE "official title"
word PERSON person
word REPRESENTATIVE representative
word SALUTATION salutation
word TASK task
word USER user
word WORKAREA "work area"


for x in $allwords; do
	for y in MODIFY DELETE INSERT ADD; do
		for z in S P N; do
			if test $y = ADD; then
				case "$addedwords" in *"$x"*)
					eval "echo GM_${x}_${y}_${z}=\$kw$z \$new_$y \$kw$z$x \$ve$z \$kw_$y."
				esac
			else
				eval "echo GM_${x}_${y}_${z}=\$kw$z \$new_$y \$kw$z$x \$ve$z \$kw_$y."
			fi
		done
	done
done
