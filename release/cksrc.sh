# configuration for ckdep.sh, depsrc.sh and mksrc.sh
# © 2016, 2017, 2018, 2019, 2020 mirabilos <t.glaser@tarent.de> Ⓕ MirBSD

# location of parent POM relative to this directory
#parentpompath=../../..	# src/main/ancillary/
parentpompath=..	# release/

# Maven profiles to use when ckdep’ing
#mvnprofiles='-Pbuild-mvnparent'
mvnprofiles=''

# groupId:artifactId vectors to include (from scopes provided, test, etc.)
# or exclude (from scopes compile/runtime by ckdep, so depsrc has them;
# exclamation mark-separated
ckdep_excludes=
ckdep_includes=

# dependencies to exclude, other than $pgID:$paID: (sorry can’t use them here)
# may be groupId:artifactId: or just groupId: to catch all
set -A depexcludes -- \
	org.evolvis.veraweb: \
	org.evolvis.veraweb.middleware: \

# path to extra sources for dependencies
# whose Maven Central presence is deficient
#depsrcpath=src/dist/extra-depsrc
depsrcpath=release/depsrc
require_depsrcpath_present=1 # or 0
drop_depsrc_from_mksrc=0 # or 1

# exclusions for dependencyManagement ($g:$a:$v)
depsrc_exclusions() {
#	# this is straight from com.querydsl:querydsl-root:4.1.1
#	[[ $g:$a = com.infradna.tool:bridge-method-annotation ]] && cat <<\EOF
#	<exclusions>
#		<exclusion>
#			<groupId>org.jenkins-ci</groupId>
#			<artifactId>annotation-indexer</artifactId>
#		</exclusion>
#	</exclusions>
#EOF
	: add one or more stanzas like this
}

# skip dependencies without a meaningful source JAR ($g:$a:$v)
depsrc_nosrc() {
#	if [[ $g = org.webjars.@(bower|bowergithub|npm)?(.*) ]]; then
#		# comment here on where to find the source
#		# and where necessary add to depsrcpath
#		return 0
#	fi
	# anything else passes
	return 1
}

# add from depsrcpath; the first argument is the filename under $depsrcpath
depsrc_add() {
	doit antlr-2.7.7.tar.gz \
	    antlr antlr 2.7.7
	doit xmlrpc-1.2-b1-src.tar.gz \
	    xmlrpc xmlrpc 1.2-b1
}

# inclusions and exclusions from validation
set -A depsrc_grep_exclusions
set -A depsrc_grep_inclusions
#depsrc_grep_inclusions+=(-e '^# dummy, only needed if this array is empty otherwise$')
depsrc_grep_exclusions+=(-e '^# dummy$')
# shipped in axis:axis source JAR
depsrc_grep_inclusions+=(-e '^org\.apache\.axis axis-jaxrpc ')
depsrc_grep_inclusions+=(-e '^org\.apache\.axis axis-saaj ')
