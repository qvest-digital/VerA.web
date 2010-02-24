package de.tarent.dblayer.engine.proxy;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PreparedStatementProxyInvocationHandler extends StatementProxyInvocationHandler
{
	public PreparedStatementProxyInvocationHandler( PreparedStatement preparedStatement )
	{
		super( preparedStatement );
	}

	public Object invoke( Object proxy, Method method, Object[] args ) throws Throwable
	{
		Object result = null;

		String methodName = method.getName();
		if ( "executeQuery".equals( methodName ) )
		{
			result = Proxy.newProxyInstance( this.getClass().getClassLoader(), new Class[] { ResultSet.class }, new ResultSetProxyInvocationHandler( ( ( PreparedStatement ) this.statement ).executeQuery() ) );
		}
		else
		{
			result = super.invoke( proxy, method, args );
		}

		return result;
	}
}
