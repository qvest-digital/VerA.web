package de.tarent.dblayer.engine.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

public class ConnectionProxyInvocationHandler implements InvocationHandler
{
	private Connection connection;

	public ConnectionProxyInvocationHandler( Connection connection )
	{
		this.connection = connection;
	}

	public Object invoke( Object proxy, Method method, Object[] args ) throws Throwable
	{
		Object result = method.invoke( this.connection, args );

		String methodName = method.getName();
		if ( "createStatement".equals( methodName ) )
		{
			return Proxy.newProxyInstance( this.getClass().getClassLoader(), new Class[] { Statement.class }, new StatementProxyInvocationHandler( ( Statement ) result ) );
		}
		else if ( "prepareStatement".equals( methodName ) )
		{
			return Proxy.newProxyInstance( this.getClass().getClassLoader(), new Class[] { PreparedStatement.class }, new PreparedStatementProxyInvocationHandler( ( PreparedStatement ) result ) );
		}
		else if ( "prepareCall".equals( methodName ) )
		{
			return Proxy.newProxyInstance( this.getClass().getClassLoader(), new Class[] { CallableStatement.class }, new CallableStatementProxyInvocationHandler( ( CallableStatement ) result ) );
		}

		return result;
	}
}
