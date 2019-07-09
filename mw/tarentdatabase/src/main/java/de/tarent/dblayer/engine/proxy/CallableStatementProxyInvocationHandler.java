package de.tarent.dblayer.engine.proxy;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.util.Calendar;

public class CallableStatementProxyInvocationHandler extends PreparedStatementProxyInvocationHandler {
    private ResultSet resultSet;

    public CallableStatementProxyInvocationHandler(CallableStatement callableStatement) {
        super(callableStatement);
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = null;

        String methodName = method.getName();
        if ("getObject".equals(methodName) && args.length == 1) {
            this.fetchResultSet();
            if (args[0] instanceof String) {
                result = this.resultSet.getObject((String) args[0]);
            } else {
                result = this.resultSet.getObject((Integer) args[0]);
            }
        } else if ("getTimestamp".equals(methodName)) {
            this.fetchResultSet();
            if (args[0] instanceof String) {
                result = this.resultSet.getTimestamp((String) args[0], args.length == 2 ? (Calendar) args[1] : null);
            } else {
                result = this.resultSet.getTimestamp((Integer) args[0], args.length == 2 ? (Calendar) args[1] : null);
            }
        } else {
            result = super.invoke(proxy, method, args);
        }

        return result;
    }

    private void fetchResultSet() throws Throwable {
        if (this.resultSet == null) {
            this.resultSet = (ResultSet) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[] { ResultSet.class },
              new ResultSetProxyInvocationHandler(this.statement.getResultSet()));
        }
    }
}
