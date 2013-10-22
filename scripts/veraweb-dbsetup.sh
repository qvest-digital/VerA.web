#!/bin/bash -e
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
# TODO: more documentation of steps and functions

# TODO: maybe this variables be sourced from /etc/defaults/veraweb
DIRECTORY='/usr/share/veraweb-java'
STOP=0
USER='veraweb'
PSQLOPTS='-q'
SELF=$(basename $0)
LOGGER=/usr/bin/logger
ADMIN=administrator
SCHEMA_VERSION="2013-06-12"
HELPLINK="doc/Benutzerhandbuch.pdf"

usage() {
    cat <<EOF
Usage: $SELF [ -a <USER>, -d <DIRECTORY> ]

    [-a]          Set Admin User    (default: ${ADMIN})
    [-d]          Install Directory (default: /usr/share/libveraweb-java/

EOF
}

log(){
    TAG=$1
    MSG=$2
    ${LOGGER} -s -t "${SELF}: ${TAG}: " "${MSG}"
}

err() {
    TAG="ERROR"
    MSG=$1
    log ${TAG} ${MSG}
    exit 1
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
    err "[-a] missing, try --help!"
fi

check_sql_files() {
    FILES="1.4/alter_from_1.3.15_to_1.4 veraweb-schema veraweb-stammdaten"
    for file in ${FILES}; do
        if [ ! -f "${DIRECTORY}/sql/${file}.sql"  ]; then
            err "${file}.sql is missing."
        fi
    done
}

check_pg_conn(){
    # Checks PG-Connection
    if ! service postgresql status | grep -qi running; then
        err "ERROR:"  "Ensure that postgresql is running"
    elif ! grep -q veraweb /root/.pgpass; then
        err "No pgpass entry for user veraweb"
    elif ! psql $PSQLOPTS -U veraweb -h localhost -c "\q" >/dev/null 2>&1; then
        err "User 'veraweb' can't connect to database"
    fi

    return 0
}

check_admin_exists() {
    check_user_exists ${ADMIN}
    return $?
}

check_buildsequences() {
    psql $PSQLOPTS -U veraweb -h localhost -c "SELECT veraweb.serv_build_sequences();" >/dev/null
    return $?
}

check_user_exists() {
    USER=$1
    TABLE="tuser"

    if ! psql $PSQLOPTS -U veraweb -h localhost -c "SELECT * FROM ${TABLE} WHERE username='${USER}'" | grep -q ${USER}; then
        return 1
    else
        return 0
    fi
}

create_admin() {
    create_user ${ADMIN} 5
}

create_user(){
    USER=$1
    ROLE=$2

    if ! check_user_exists ${USER}; then
        if psql $PSQLOPTS -U veraweb -h localhost -c "INSERT INTO tuser (username, role) VALUES ('${USER}', $ROLE)"; then
            if ! check_user_exists ${USER}; then
                err "Error while creating user: $USER."
            else
                log "INFO" "User $USER sucessfull created."
            fi
        fi
    else
            log "INFO" "User $USER already exist, nothing to do."
    fi
}

get_helplink_destination() {
    psql $PSQLOPTS -t -U veraweb -h localhost -c "SELECT cvalue FROM tconfig WHERE cname='helplink';" | tr -d ' '
}

get_schema_version() {
    if psql $PSQLOPTS -t -U veraweb -h localhost -c "SELECT cvalue FROM tconfig WHERE cname = 'SCHEMA_VERSION';" > /dev/null 2>&1; then
        psql $PSQLOPTS -t -U veraweb -h localhost -c "SELECT cvalue FROM tconfig WHERE cname = 'SCHEMA_VERSION';" | tr -d ' '
    else
        echo "INITIAL SETUP"
    fi
}

setup_schema() {
    if ! psql $PSQLOPTS -U veraweb -h localhost -f ${DIRECTORY}/sql/veraweb-schema.sql > /dev/null; then
        err "Could not load file: ${DIRECTORY}/sql/veraweb-schema.sql into PGSQL"
    elif ! psql $PSQLOPTS -U veraweb -h localhost -c "SELECT serv_verawebschema(1);" >/dev/null; then
        err "Errors accured by executing serv_verawebschema(1)"
    fi
}

set_helplink_destination(){
    DEST=$(get_helplink_destination)

    if [ "${DEST}" != "${HELPLINK}" ]; then
        COUNT=$(psql $PSQLOPTS -t -U veraweb -h localhost -c "SELECT COUNT('cname') from tconfig WHERE cname='helplink';")
        if [ $COUNT -eq 0 ]; then
            psql $PSQLOPTS -t -U veraweb -h localhost -c "INSERT INTO tconfig (cname,cvalue) VALUES ('helplink', 'unset');" > /dev/null
        fi

        if psql $PSQLOPTS -t -U veraweb -h localhost -c "UPDATE tconfig SET cvalue='${HELPLINK}' WHERE cname='helplink';" > /dev/null; then
            log "INFO" "Set helplink destination to ${HELPLINK}."
        else
            log "WARN" "Can't set helplink destination, pls check database connection."
            return 2
        fi
    else
        log "INFO" "Helplink destination (${HELPLINK}) is already set."
    fi
}

setup_1_4() {
    VERSION=$(get_schema_version)

    if  [ "${VERSION}" != "2013-06-12" ]; then
            if ! psql $PSQLOPTS -U veraweb -h localhost -f ${DIRECTORY}/sql/1.4/alter_from_1.3.15_to_1.4.sql >/dev/null; then
                err "Could not load file: ${DIRECTORY}/sql/alter_from_1.3.15_to_1.4.sql into PGSQL"
            fi
    else
        log "INFO" "Database schema is already in the newest version, nothing to do."
    fi
}
setup_stammdaten() {
    if ! psql $PSQLOPTS -U veraweb -h localhost -f "${DIRECTORY}/sql/veraweb-stammdaten.sql" >/dev/null; then
        err "Could not load file '$DIRECTORY/sql/veraweb-stammdate.sql' into PGSQL"
    fi
}

main() {
    if check_sql_files && check_pg_conn; then

    VERSION="$(get_schema_version)"

    if  [ "${VERSION}" != "${SCHEMA_VERSION}" ]; then
            setup_schema && setup_stammdaten && setup_1_4
    else
        log "INFO" "The database setup is already finished, nothing to do."
    fi
    create_admin

        if check_buildsequences; then
            log "INFO" "We are finished now, have fun."
        else
            err "Error while setup database, we can't select build sequences from database."
        fi
    fi

    set_helplink_destination
}

main
