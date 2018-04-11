#GENERAL MESSAGE CREATOR
#GENERATES GM Placeholders automatically
#To the entities which also could be added as well as created (i.e: Categories), please add in (#ADDED SECTION) section
#This will created an additional GM_ADD message for these
#To execute the script, simply type "sh script.sh", and to store in a file "sh script.sh > file.resource"
#The messages come in "#GENERAL MESSAGES" Section in the .resource file

kwS="Se ha"
kwP="Se han"
kwN="No se han"
kwaN="Por favor, comience por seleccionar"
kwbN="o más"

kw_MODIFY="modificado"
kw_DELETE="eliminado"
kw_INSERT="creado"
kw_ADD="añadido"

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
	eval g_$2=\$gender kwS$2=\$singular kwP$2=\$plural kwN$2=\$plural

}

#ADDED SECTION
. ./added.sh

#ENTITIES
word f CATEGORY categoría
word m CLIENT mandante
word m COLOR color colores
word m DELEGATION_FIELD "campo adicional" "campos adicionales"
word m DISTRIBUTOR distribuidor distribuidores
word m DOCUMENT_TYPE "tipo de documento" "tipos de documento"
word m EMAIL e-mail
word m EVENT evento
word f EVENT_LOCATION "localización de evento" "localizaciones de evento"
word m GUEST invitado
word m MAIL_DRAFT asunto
word m OFFICIAL_TITLE título
word f PERSON persona
word m REPRESENTATIVE representante
word f SALUTATION denominación denominaciones
word f TASK tarea
word m USER usuario
word f WORKAREA "área de trabajo" "áreas de trabajo"

for x in $allwords; do
	eval g=\$g_$x
	if test $g = m; then
		arS=un arP=%s arN=
	else
		arS=una arP=%s arN=
	fi
	for y in MODIFY DELETE INSERT ADD; do
		for z in S P N; do
			if test $y = ADD; then
				case "$alladdedwords" in *"$x"*)
					eval "echo GM_${x}_${y}_${z}=\$kw$z \$kw_$y \$ar$z \$kw$z$x.";;
				esac
			else
				eval "echo GM_${x}_${y}_${z}=\$kw$z \$kw_$y \$ar$z \$kw$z$x."
			fi
		done
	done
done
