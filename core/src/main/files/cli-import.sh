#!/bin/bash
#
# Veranstaltungsmanagement VerA.web, comprised of…
# VerA.web, platform-independent webservice-based event management
# tarent-commons, a set of common components and solutions
# tarent-contact, platform-independent webservice-based contact management
# tarent-database, jdbc database library
# tarent-doctor, Document Generation Platform
# tarent-octopus, Webservice Data Integrator and Application Server
# … is newly MIT-licenced and Copyright
#  © 2018 Атанас Александров (sirakov@gmail.com)
#  © 2014, 2015, 2016, 2017 Атанас Александров (a.alexandrov@tarent.de)
#  © 2013 Иванка Александрова (i.alexandrova@tarent.de)
#  © 2005, 2006, 2007 asteban (s.mancke@tarent.de)
#  © 2013 Patrick Apel (p.apel@tarent.de)
#  © 2016 Eugen Auschew (e.auschew@tarent.de)
#  © 2013 Andrei Boulgakov (a.boulgakov@tarent.de)
#  © 2013 Valentin But (v.but@tarent.de)
#  © 2016 Lukas Degener (l.degener@tarent.de)
#  © 2017 Axel Dirla (a.dirla@tarent.de)
#  © 2015 Julian Drawe (j.drawe@tarent.de)
#  © 2009 Sven Frommeyer (s.frommeyer@tarent.de)
#  © 2014, 2018 Dominik George (d.george@tarent.de)
#  © 2013 Martin Gernhardt (m.gernhardt@tarent.de)
#  © 2013 Sascha Girrulat (s.girrulat@tarent.de)
#  © 2007, 2008 David Goemans (d.goemans@tarent.de)
#  © 2018 Christian Gorski (c.gorski@tarent.de)
#  © 2015 Viktor Hamm (v.hamm@tarent.de)
#  © 2013 Katja Hapke (k.hapke@tarent.de)
#  © 2006, 2007, 2010, 2013 Hendrik Helwich (h.helwich@tarent.de)
#  © 2018 Thomas Hensel (t.hensel@tarent.de)
#  © 2018, 2019 Benedict Hoeger (b.hoeger@tarent.de)
#  © 2018, 2019 Titian Horvath (t.horvath@tarent.de)
#  © 2005, 2006, 2007, 2008 Christoph Jerolimov (jerolimov@gmx.de)
#  © 2018, 2019 Timo Kanera (t.kanera@tarent.de)
#  © 2006 Philipp Kirchner (p.kirchner@tarent.de)
#  © 2008, 2009, 2010 Carsten Klein (c.klein@tarent.de)
#  © 2006 Michael Kleinhenz (m.kleinhenz@tarent.de)
#  © 2006 Michael Klink (m.klink@tarent.de)
#  © 2007 Fabian Köster (f.koester@tarent.de)
#  © 2006, 2014 Martin Ley (m.ley@tarent.de)
#  © 2007 Alex Maier (a.maier@tarent.de)
#  © 2014, 2015 Max Marche (m.marche@tarent.de)
#  © 2007 Jan Meyer (jan@evolvis.org)
#  © 2007, 2013, 2014, 2015, 2016, 2017, 2018, 2019, 2020
#     mirabilos (t.glaser@tarent.de)
#  © 2016 Cristian Molina (c.molina@tarent.de)
#  © 2006, 2007 Jens Neumaier (j.neumaier@tarent.de)
#  © 2006 Nils Neumaier (n.neumaier@tarent.de)
#  © 2018 Yorka Neumann (y.neumann@tarent.de)
#  © 2017 Michael Nienhaus (m.nienhaus@tarent.de)
#  © 2013 Claudia Nuessle (c.nuessle@tarent.de)
#  © 2014, 2015 Jon Nuñez Alvarez (j.nunez-alvarez@tarent.de)
#  © 2016 Jens Oberender (j.oberender@tarent.de)
#  © 2016, 2017 Miluška Pech (m.pech@tarent.de)
#  © 2007, 2008, 2009 Martin Pelzer (m.pelzer@tarent.de)
#  © 2008, 2009 Christian Preilowski (c.thiel@tarent.de)
#  © 2013 Marc Radel (m.radel@tarent.de)
#  © 2013 Sebastian Reimers (s.reimers@tarent.de)
#  © 2015 Charbel Saliba (c.saliba@tarent.de)
#  © 2006, 2008, 2009, 2010 Thomas Schmitz (t.schmitz@tarent.de)
#  © 2013 Volker Schmitz (v.schmitz@tarent.de)
#  © 2014 Sven Schumann (s.schumann@tarent.de)
#  © 2007 Robert Schuster (r.schuster@tarent.de)
#  © 2014 Sevilay Temiz (s.temiz@tarent.de)
#  © 2013 Kevin Viola Schmitz (k.schmitz@tarent.de)
#  © 2008, 2015 Stefan Walenda (s.walenda@tarent.de)
#  © 2015, 2016, 2017 Max Weierstall (m.weierstall@tarent.de)
#  © 2013 Rebecca Weinz (r.weinz@tarent.de)
#  © 2015, 2016 Stefan Weiz (s.weiz@tarent.de)
#  © 2015, 2016 Tim Zimmer (t.zimmer@tarent.de)
# and older code, Copyright © 2001–2008 ⮡ tarent GmbH and contributors.
# Licensor is tarent solutions GmbH, http://www.tarent.de/
#
# Permission is hereby granted, free of charge, to any person obtaining a
# copy of this software and associated documentation files (the
# "Software"), to deal in the Software without restriction, including
# without limitation the rights to use, copy, modify, merge, publish,
# distribute, sublicense, and/or sell copies of the Software, and to
# permit persons to whom the Software is furnished to do so, subject to
# the following conditions:
#
# The above copyright notice and this permission notice shall be included
# in all copies or substantial portions of the Software.
#
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
# OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
# MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
# IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
# CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
# TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
# SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
#

# Import person script for VerA.web

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
	curl -s -F username=$1 -F password=$2 --form "importfile=@$3" --form importSource=$4 --form format=formatCSV --form filenc=UTF-8 --form targetOrgUnit=$5 $6do/ImportPersonsFile >"$TEMP_OUTPUT"
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
			    --form "$id-select=true" "$6do/ImportPersonsEditDuplicates" >/dev/null
			curl -s -F username=$1 -F password=$2 --form "importId=$IMPORT_ID" $6do/ImportPersonsFinalise >/dev/null
		done
	else
		echo "Keine Duplikate vorhanden, importiere die Datensätze..."
		curl -s -F username=$1 -F password=$2 --form "importId=$IMPORT_ID" $6do/ImportPersonsFinalise >/dev/null
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
