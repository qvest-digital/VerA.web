#GENERAL MESSAGE CREATOR
#GENERATES GM Placeholders automatically
#To the entities which also could be added as well as created (i.e: Categories), please add in (#ADDED SECTION) section
#This will created an additional GM_ADD message for these
#To execute the script, simply type "sh script.sh", and to store in a file "sh script.sh > file.resource"
#The messages come in "#GENERAL MESSAGES" Section in the .resource file

kwS=""
kwP=""
kwN=""

kw_MODIFY="modifié"
kw_DELETE="supprimé"
kw_INSERT="créé"
kw_ADD="ajouté"

added() {
	keyword=$1
	alladdedwords="$alladdedwords $keyword"
}

word() {
	gender=$1
	keyword=$2
	singular=$3
	plural=${4:-${singular}s}

	allwords="$allwords $keyword"
	eval g_$2=\$gender kwS$2=\$singular kwP$2=\$plural kwN$2=\$singular

}

#ADDED SECTION
. ./added.sh

#ENTITIES
word f CATEGORY catégorie
word m CLIENT client
word f COLOR couleur
word m DELEGATION_FIELD "champ supplémentaire" "champs supplémentaires"
word m DISTRIBUTOR distributeur
word m DOCUMENT_TYPE "type de documents" "types de documents"
word m EMAIL email
word m EVENT événement
word m EVENT_LOCATION "lieu de l'événement" "lieux d'événements"
word m GUEST invité
word m MAIL_DRAFT "modèle de courrier" "modèles de courrier"
word m OFFICIAL_TITLE "titre officiel" "titres officiels"
word f PERSON personne
word m REPRESENTATIVE représentant
word m SALUTATION "formule de politesse" "formules de politesse"
word f TASK tâche
word m USER utilisateur
word m WORKAREA "espace de travail" "espaces de travail"


for x in $allwords; do
	eval g=\$g_$x
	if test $g = m; then
		veS="a été" veP="ont été" veN="a été"
		arS=Un   arP=%s   arN=Aucun
		teS=	teP=s	teN=
	else
		veS="a été" veP="ont été" veN="a été"
		arS=Une   arP=%s   arN=Aucune
		teS=e	teS=es	teN=
	fi
	for y in MODIFY DELETE INSERT ADD; do
		for z in S P N; do
			if test $y = ADD; then
				case $alladdedwords in *"$x"*)
					eval "echo GM_${x}_${y}_${z}=\$ar$z \$kw$z$x \$ve$z \$kw_$y.";;
				esac
			else
				eval "echo GM_${x}_${y}_${z}=\$ar$z \$kw$z$x \$ve$z \$kw_$y."
				#eval "echo GM_${x}_${y}_${z}=\$kw$z \$kw_$y \$ar$z \$kw$z$x"
			fi
		done
	done
done
