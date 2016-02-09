#!/bin/bash
# Import person script for VerA.web

usage() {
	echo "Benutzung: $0 [-u <BENUTZERNAME>] [-p <PASSWORT>] [-f <IMPORT_DATEI>] [-n <IMPORT_NAME>] [-m <MANDANT_ID>] [-i <VERAWEB_INSTANZ>]" 1>&2; exit 1; 
}

start_import() {
	ENTRIES="$(curl -s -F username=$1 -F password=$2 --form "importfile=@$3" --form importSource=$4 --form format=formatCSV --form targetOrgUnit=$5 $6do/ImportPersonsFile | grep 'Zu importierende Datensätze insgesamt' | grep -Eo '[0-9]{1,10}<' | grep -Eo '[0-9]{1,10}')"
	echo "$ENTRIES Datensatz/Datensätze vorhanden"
	echo "Starte Importvorgang Nummer $7"
	echo "Suche nach Duplicate gestartet..."
	
	duplicates="$(curl -s -F username=$1 -F password=$2 --form "importfile=@$3" --form importSource=$4 --form format=formatCSV --form orgUnit=$5 $6do/ImportPersonsFile | grep 'Davon Dubletten' | grep -Eo '[0-9]{1,10}<' | grep -Eo '[0-9]{1,10}')"
	if [ "$duplicates" -ne 0 ]; then
	  echo "Duplikate wurden gefunden, diese werden nun übernommen..."  
	  ids=($(curl -s -F username=$1 -F password=$2 --form "importfile=@$3" --form importSource=asds --form format=formatCSV --form importId=$7 --form limit=9999999 $6do/ImportPersonsEditDuplicates | grep '\-id" v' | grep -Eo '>[0-9]{1,10}<' | grep -Eo '[0-9]{1,10}'))
		
	  for id in "${ids[@]}"
	  do
		echo Importiere die Person mit ID ${id}
		POSTString="--form selectNone=true --form save=Als+Dublette+%C3%BCbernehmen --form toggleAllSelect=on --form list=$id --form $id-id=$id --form $id-select=true"
		curl -s -F username=$1 -F password=$2 --form importId=$7 $POSTString $6do/ImportPersonsEditDuplicates > /dev/null
		curl -s -F username=$1 -F password=$2 --form importId=$7 $6do/ImportPersonsFinalise > /dev/null
	  done
	else
	  echo "Keine Duplikate vorhanden, importiere die Datensätze..."
	  curl -s -F username=$1 -F password=$2 --form importId=$7 $6do/ImportPersonsFinalise > /dev/null
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
	IMPORT_ID="$(curl -s -F username=${u} -F password=${p} --form "importfile=@${f}" --form importSource=${n} --form targetOrgUnit=${m} --form format=formatCSV ${i}do/ImportPersonsFile | grep 'Import-ID' | grep -Eo '[0-9]{1,10}')"
	start_import ${u} ${p} ${f} ${n} ${m} ${i} $IMPORT_ID
fi
