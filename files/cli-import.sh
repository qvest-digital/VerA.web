#!/bin/bash
# Import person script for VerA.web

usage() {
	echo "Benutzung: $0 [-u <BENUTZERNAME>] [-p <PASSWORT>] [-f <IMPORT_DATEI>] [-n <IMPORT_NAME>] [-m <MANDANT_ID>] [-i <VERAWEB_INSTANZ>]" 1>&2; exit 1; 
}

start_import() {
	TEMP_OUTPUT=/tmp/veraweb-import-cli.log
	rm -f $TEMP_OUTPUT
    curl -s -F username=$1 -F password=$2 --form "importfile=@$3" --form importSource=$4 --form format=formatCSV --form targetOrgUnit=$5 $6do/ImportPersonsFile > $TEMP_OUTPUT  
	TOTAL_ENTRIES="$(cat $TEMP_OUTPUT | grep 'Zu importierende Datensätze insgesamt' | grep -Eo '[0-9]{1,10}<' | grep -Eo '[0-9]{1,10}')"
	IMPORT_ID="$(cat $TEMP_OUTPUT | grep 'Import-ID' | grep -Eo '[0-9]{1,10}')"
	DUPLICATE_ENTRIES="$(cat $TEMP_OUTPUT | grep 'Davon Dubletten' | grep -Eo '[0-9]{1,10}<' | grep -Eo '[0-9]{1,10}')"
	echo "Insgesamt $TOTAL_ENTRIES Datensatz/Datensätze vorhanden"
	echo "Starte Importvorgang Nummer $IMPORT_ID..."
	echo "Suche nach Duplicate gestartet..."
	echo "Es wurde $DUPLICATE_ENTRIES Duplikate gefunden..."
	if [ "$DUPLICATE_ENTRIES" -ne 0 ]; then
	  echo "Duplikate wurden gefunden, diese werden nun übernommen..."  
	  ids=($(curl -s -F username=$1 -F password=$2 --form "importfile=@$3" --form importSource=asds --form format=formatCSV --form importId=$IMPORT_ID --form limit=9999999 $6do/ImportPersonsEditDuplicates | grep '\-id" v' | grep -Eo '>[0-9]{1,10}<' | grep -Eo '[0-9]{1,10}'))
		
	  for id in "${ids[@]}"
	  do
		echo Importiere die Person mit ID ${id}
		POSTString="--form selectNone=true --form save=Als+Dublette+%C3%BCbernehmen --form toggleAllSelect=on --form list=$id --form $id-id=$id --form $id-select=true"
		curl -s -F username=$1 -F password=$2 --form importId=$IMPORT_ID $POSTString $6do/ImportPersonsEditDuplicates > /dev/null
		curl -s -F username=$1 -F password=$2 --form importId=$IMPORT_ID $6do/ImportPersonsFinalise > /dev/null
	  done
	else
	  echo "Keine Duplikate vorhanden, importiere die Datensätze..."
	  curl -s -F username=$1 -F password=$2 --form importId=$IMPORT_ID $6do/ImportPersonsFinalise > /dev/null
	fi
	echo "Fertig"
}

while getopts "u:p:f:n:m:i:" opt; do
  case "${opt}" in
        u)
            u=${OPTARG}
            ;;
        p)
            p=${OPTARG}
            ;;
        f)
            f=${OPTARG}
            ;;
        n)
            n=${OPTARG}
            ;;
        m)
            m=${OPTARG}
            ;;
        i)
            i=${OPTARG}
            ;;
        *)
            usage
            ;;
  esac
done
shift $((OPTIND-1))

if [ -z "${u}" ] || [ -z "${p}" ] || [ -z "${f}" ] || [ -z "${n}" ] || [ -z "${m}" ] || [ -z "${i}" ]; then
    usage
else
	start_import ${u} ${p} ${f} ${n} ${m} ${i}
fi
