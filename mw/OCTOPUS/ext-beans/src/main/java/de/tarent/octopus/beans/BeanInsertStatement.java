package de.tarent.octopus.beans;
import de.tarent.dblayer.sql.statement.Insert;
import lombok.extern.log4j.Log4j2;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class encapsulates an INSERT {@link PreparedStatement}operating in
 * a given {@link ExecutionContext} and using data from {@link Bean} instances.<br>
 * TODO: implement ID pre-fetches and post-fetches using aggregated BeanSelectStatements.
 *
 * @author Michael Klink
 */
@Log4j2
class BeanInsertStatement extends BeanBaseStatement implements BeanStatement {
    //
    // constructor
    //

    /**
     * This constructor stores the given context and creates the prepared
     * statement.
     *
     * @param insert         the db layer INSERT statement using {@link BeanBaseStatement#PLACE_HOLDER}
     *                       for variables where bean field values are to be set.
     * @param fieldsInInsert fields to insert in the same order as their corresponding
     *                       {@link BeanBaseStatement#PLACE_HOLDER} variables appear inside the statement
     * @param context        execution context of the PreparedStatements
     */
    BeanInsertStatement(Insert insert, List fieldsInInsert, ExecutionContext context) throws BeanException {
        super(insert, context);
        this.fields = fieldsInInsert;
    }

    //
    // interface BeanStatement
    //

    /**
     * This method executes the insert using the given bean.
     *
     * @param bean this bean's attributes are fed into the prepared statement.
     * @return count of insert operations
     * @see de.tarent.octopus.beans.BeanStatement#execute(de.tarent.octopus.beans.Bean)
     */
    public int execute(Bean bean) throws BeanException {
        if (bean != null) {
            try {
                preparedStatement.clearParameters();
                List params = new ArrayList();
                for (int index = 0; index < fields.size(); index++) {
                    String field = fields.get(index).toString();
                    Object value = bean.getField(field);
                    preparedStatement.setObject(index + 1, value);
                    params.add(value);
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("PreparedStatement <" + sqlStatement + "> is called for parameters " + params + ".");
                }
                return preparedStatement.executeUpdate();
            } catch (SQLException se) {
                throw new BeanException("Error executing the PreparedInsert <" + sqlStatement + "> using bean <" + bean + ">",
                  se);
            }
        } else {
            logger.warn("execute method called without a bean instance.");
        }
        return 0;
    }

    //
    // protected variables
    //
    /**
     * Here the fields are listed corresponding to the variables to fill in the statement.
     */
    final List fields;
}
