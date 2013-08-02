#!/bin/sh
#
# Setup the database for veraweb with postgesql
#
#
# Authors : Volker Schmitz <v.schmitz@tarent.de>
#           Sascha Girrulat <s.girrulat@tarent.de>
#
#
# TODO: disable/enable pgpass
# TODO: write own pgpass entry
# TODO: user other user than veraweb
# TODO: more functional programming
# TODO: more documentation of steps and functions
# TODO: simplify the conditionals

# TODO: maybe this variables be sourced from /etc/defaults/veraweb
DIRECTORY='/usr/share/libveraweb-java/'
STOP=0
USER='veraweb'
PSQLOPTS='-q'
SELF=$(basename $0)

usage() {
    cat <<EOF
Usage: $SELF -p <PASSWORD> [ -a <USER>, -d <DIRECTORY> ]

  [-p]          Set User Password for user veraweb

  Optional Parameters:

    [-a]          Set Admin User    (default: verawebadmin)
    [-d]          Install Directory (default: /usr/share/libveraweb-java/

EOF
}

# TODO: maybe we use getopt it is more robust and works with all shells
while getopts 'd:a:' OPTION; do
    case "$OPTION" in
        h|\?)           usage
                        exit 4
                        ;;
        d)              DIRECTORY=$OPTARG
                        ;;
        a)              ADMIN=$OPTARG
                        ;;
    esac
done

# TODO: could we do an other check to be shure we are configured?
if [ -e $DIRECTORY/.configured ]; then
    echo "PGSQL allready configured"
    exit 0
fi

if [ -z $ADMIN ]; then
    echo "[-a] missing!"
    STOP=1
fi

if [ $STOP -eq 1 ]; then
    echo; usage; exit 4
fi

#Checks if all files exist.
FILE="$DIRECTORY/sql/veraweb-schema.sql"
if [ ! -f $FILE ]; then
    echo "veraweb-schema.sql is missing"
    exit 1
fi

FILE="$DIRECTORY/sql/veraweb-stammdaten.sql"
if [ ! -f $FILE ]; then
    echo "veraweb-stammdaten.sql is missing"
    exit 1
fi

#Checks PG-Connection

PGPACKAGE=$(dpkg -l | grep -c postgresql-server)
PGPROZESS=$(service postgresql status | grep -ic running)
PGCONN=$(psql $PSQLOPTS -U veraweb -h localhost -c "\q" >/dev/null 2>&1; echo $?)
PGPASS=$(grep -c veraweb /root/.pgpass)

if [ -z $PGPACKAGE ]; then
    echo "No postgresqlserver found"
    exit 3
fi

if [ -z $PGPROZESS ]; then
    echo "Ensure that postgresql is running"
    exit 3
fi

if [ -z $PGCONN ]; then
    echo "Postgres is burning";
    exit 3
fi

if [ -z $PGPASS ]; then
	echo "No pgpass entry for user veraweb"
	exit 3
fi
if [ $PGCONN -ne 0 ]; then
	echo "User: veraweb can't connect to database"
fi

psql $PSQLOPTS -U veraweb -h localhost -f ${DIRECTORY}/veraweb-schema.sql
if [ $? -ne 0 ]; then
    echo "Could not load file '$DIRECTORY/veraweb-schema.sql' into PGSQL"
    exit 2
fi

psql $PSQLOPTS -U veraweb -h localhost -c "SELECT serv_verawebschema(1);"
if [ $? -ne 0 ]; then
    echo "Errors accured by executing serv_verawebscgema(1)"
    exit 2
fi

psql $PSQLOPTS -U veraweb -h localhost -f /opt/veraweb/install/veraweb-stammdaten.sql
if [ $? -ne 0 ]; then
    echo "Could not load file '$DIRECTORY/veraweb-stammdate.sql' into PGSQL"
    exit 2
fi

psql $PSQLOPTS -U veraweb -h localhost -c "INSERT INTO tuser (username, role) VALUES ('$ADMIN', 5)"
if [ $? -ne 0 ]; then
    echo 'Could not create verawebadmin "$ADMI"'
    exit 2
fi

psql $PSQLOPTS -U veraweb -h localhost -c "SELECT veraweb.serv_build_sequences();"
if [ $? -ne 0 ]; then
    echo 'Errors accured by executing veraweb.serv_build_sequences();'
    exit 2
fi

ADMINTEST=$(psql -U veraweb -h localhost -c "SELECT * FROM tuser WHERE username='$ADMIN'" | grep -c $ADMIN )
if [ -z $ADMINTEST ]; then
    echo "FATAL veraweb installation burned everything away"
else
    echo "Done"
    touch $DIRECTORY/.configured
fi
