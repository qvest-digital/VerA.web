# this file is sourced, not executed!

perl -pi -e 's!^#(log_destination = 'stderr')!$1!' \
    /var/lib/postgresql/data/postgresql.conf
