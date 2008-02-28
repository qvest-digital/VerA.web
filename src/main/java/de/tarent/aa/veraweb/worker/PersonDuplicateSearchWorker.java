/**
 * 
 */
package de.tarent.aa.veraweb.worker;

import java.io.IOException;
import java.util.List;

import de.tarent.aa.veraweb.beans.PersonSearch;
import de.tarent.aa.veraweb.beans.facade.PersonConstants;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.RawClause;
import de.tarent.dblayer.sql.clause.Where;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.custom.beans.BeanException;
import de.tarent.octopus.custom.beans.Database;
import de.tarent.octopus.server.OctopusContext;

/**
 * This worker implements change request 2.3 for version 1.2.0.
 * 
 * It enables the user to run a duplicate search over all persons
 * in the database. Duplicates are found by searching for equal
 * firstname and lastname names.
 * 
 * @author cklein
 */
public class PersonDuplicateSearchWorker extends PersonListWorker
{
	public PersonDuplicateSearchWorker()
	{
		super();
	}

	@Override
	public List showList(OctopusContext cntx) throws BeanException, IOException
	{
		List result = super.showList( cntx );
		cntx.setContent( "action", "duplicateSearch" );

		return result;
	}

	@Override
	protected Select getSelect( PersonSearch search, Database database ) throws BeanException, IOException
	{
		Select result = database.getSelect( this.BEANNAME );
		result.from( "veraweb.tperson person2" );

		return result;
	}

	@Override
	protected Integer getCount(OctopusContext cntx, Database database) throws BeanException, IOException
	{
		Select select = database.getCount( this.BEANNAME );
		select.from( "veraweb.tperson person2" );
		this.extendWhere( cntx, select );

		return database.getCount( select );
	}

	@Override
	protected void extendWhere( OctopusContext cntx, Select select )
	{
		select.whereAnd(
			Where.and(
				Where.and( 
					Where.and(
						Expr.equal( "tperson.fk_orgunit", ( ( PersonalConfigAA ) cntx.personalConfig() ).getOrgUnitId() ),
						Expr.equal( "tperson.deleted", PersonConstants.DELETED_FALSE )
					),
					new RawClause( "tperson.pk!=person2.pk" )
				),
				Where.or(
					Where.or(
						Where.or(
							Where.and(
								new RawClause( "tperson.firstname_a_e1=person2.firstname_a_e1" ),
								new RawClause( "tperson.lastname_a_e1=person2.lastname_a_e1" )
							),
							new RawClause( "tperson.lastname_a_e1=person2.lastname_a_e1" )
						),
						Where.or(
							Where.and(
								new RawClause( "tperson.firstname_a_e2=person2.firstname_a_e2" ),
								new RawClause( "tperson.lastname_a_e2=person2.lastname_a_e2" )
							),
							new RawClause( "tperson.lastname_a_e2=person2.lastname_a_e2" )
						)
					),
					Where.or(
						Where.and(
							new RawClause( "tperson.firstname_a_e3=person2.firstname_a_e3" ),
							new RawClause( "tperson.lastname_a_e3=person2.lastname_a_e3" )
						),
						new RawClause( "tperson.lastname_a_e3=person2.lastname_a_e3" )
					)
				)
			)
		);
	}
}
