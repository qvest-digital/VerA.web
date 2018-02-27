#!/bin/bash
# Import person script for VerA.web
#
# Copyright © 2016
#	Атанас Александров <a.alexandrov@tarent.de>
#	Max Weierstall <m.weierstall@tarent.de>
#
# This file is part of VerA.web and published under the same licence.

usage() {
	echo "================================================================================================"
	echo "|         Importscript für VerA.web                                                             |"
	echo "|                                                                                               |"
	echo "|         WICHTIG:                                                                              |"
	echo "|          - Im Dateinamen der Importdatei darf kein Leerzeichen vorkommen                      |"
    echo "================================================================================================"
    echo
    echo "Benutzung: $0 [-u <BENUTZERNAME>] [-p <PASSWORT>] [-f <IMPORT_DATEI>] [-n <IMPORT_NAME>] [-m <MANDANT_ID>] [-i <VERAWEB_INSTANZ>]"
	echo
	echo "Beispiel: $0 -u admin -p geheim -f datei.csv -n testname -m 2 -i https://host/veraweb/"
    echo
    exit 1
}

start_import() {
	if ! TEMP_OUTPUT=$(mktemp); then
		echo >&2 "Konnte temporäre Datei nicht anlegen"
		exit 1
	fi
    curl -s -F username=$1 -F password=$2 --form "importfile=@$3" --form importSource=$4 --form format=formatCSV --form filenc=UTF-8 --form targetOrgUnit=$5 $6do/ImportPersonsFile > "$TEMP_OUTPUT"
	TOTAL_ENTRIES="$(<"$TEMP_OUTPUT" grep 'Zu importierende Datensätze insgesamt' | grep -Eo '[0-9]{1,10}<' | grep -Eo '[0-9]{1,10}')"
	IMPORT_ID="$(<"$TEMP_OUTPUT" grep 'Import-ID' | grep -Eo '[0-9]{1,10}')"
	DUPLICATE_ENTRIES="$(<"$TEMP_OUTPUT" grep 'Davon Dubletten' | grep -Eo '[0-9]{1,10}<' | grep -Eo '[0-9]{1,10}')"
	echo "Insgesamt $TOTAL_ENTRIES Datensatz/Datensätze vorhanden"
	echo "Starte Importvorgang Nummer $IMPORT_ID..."
	echo "Suche nach Duplicate gestartet..."
	echo "Es wurde $DUPLICATE_ENTRIES Duplikate gefunden..."
	if [[ $DUPLICATE_ENTRIES -ne 0 ]]; then
	  echo "Duplikate wurden gefunden, diese werden nun übernommen..."
	  ids=($(curl -s -F username=$1 -F password=$2 --form "importfile=@$3" \
		--form importSource=asds --form format=formatCSV \
		--form "importId=$IMPORT_ID" --form limit=9999999 \
		$6do/ImportPersonsEditDuplicates | \
		grep '\-id" v' | \
		grep -Eo '>[0-9]{1,10}<' | \
		grep -Eo '[0-9]{1,10}'))

	  for id in "${ids[@]}"
	  do
		echo Importiere die Person mit ID ${id}
		curl -s -F "username=$1" -F "password=$2" --form "importId=$IMPORT_ID" \
			--form selectNone=true --form save=Als+Dublette+%C3%BCbernehmen \
			--form toggleAllSelect=on --form "list=$id" --form "$id-id=$id" \
			--form "$id-select=true" "$6do/ImportPersonsEditDuplicates" > /dev/null
		curl -s -F username=$1 -F password=$2 --form "importId=$IMPORT_ID" $6do/ImportPersonsFinalise > /dev/null
	  done
	else
	  echo "Keine Duplikate vorhanden, importiere die Datensätze..."
	  curl -s -F username=$1 -F password=$2 --form "importId=$IMPORT_ID" $6do/ImportPersonsFinalise > /dev/null
	fi
	echo "Fertig"
	rm -f "$TEMP_OUTPUT"
}

while getopts "u:p:f:n:m:i:" opt; do
  case $opt in
  (u)
  	u=$OPTARG
  	;;
  (p)
  	p=$OPTARG
  	;;
  (f)
  	f=$OPTARG
  	;;
  (n)
  	n=$OPTARG
  	;;
  (m)
  	m=$OPTARG
  	;;
  (i)
  	i=$OPTARG
    case $i in
	(*/) ;;
	(*) i=$i/ ;;
	esac
  	;;
  (*)
  	usage
  	;;
  esac
done
shift $((OPTIND-1))

if [[ -z $u || -z $p || -z $f || -z $n || -z $m || -z $i ]]; then
    usage
fi
start_import "$u" "$p" "$f" "$n" "$m" "$i"
