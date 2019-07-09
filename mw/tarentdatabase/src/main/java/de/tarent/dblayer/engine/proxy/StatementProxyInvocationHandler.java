package de.tarent.dblayer.engine.proxy;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class StatementProxyInvocationHandler implements InvocationHandler {
    protected Statement statement;

    public StatementProxyInvocationHandler(Statement statement) {
        this.statement = statement;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = null;

        String methodName = method.getName();
        if ("getConnection".equals(methodName)) {
            result = Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[] { Connection.class },
              new ConnectionProxyInvocationHandler(this.statement.getConnection()));
        } else if ("getResultSet".equals(methodName)) {
            result = Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[] { ResultSet.class },
              new ResultSetProxyInvocationHandler(this.statement.getResultSet()));
        } else if ("executeQuery".equals(methodName)) {
            result = Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[] { ResultSet.class },
              new ResultSetProxyInvocationHandler(this.statement.executeQuery((String) args[0])));
        } else {
            result = method.invoke(this.statement, args);
        }

        return result;
    }
}
