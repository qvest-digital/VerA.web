package de.tarent.dblayer.engine.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;

public class ResultSetProxyInvocationHandler implements InvocationHandler
{
	private ResultSet resultSet;

	public ResultSetProxyInvocationHandler( ResultSet resultSet )
	{
		this.resultSet = resultSet;
	}

	public Object invoke( Object proxy, Method method, Object[] args ) throws Throwable
	{
		Object result = null;

		String methodName = method.getName();
		if ( "getObject".equals( methodName ) && args.length == 1 )
		{
			int columnIndex;
			if ( args[ 0 ] instanceof String )
			{
				columnIndex = this.resultSet.findColumn( ( String ) args[ 0 ] );
			}
			else
			{
				columnIndex = ( Integer ) args[ 0 ];
			}
			if ( this.resultSet.getMetaData().getColumnType( columnIndex ) == java.sql.Types.TIMESTAMP )
			{
				result = ( ( ResultSet ) proxy ).getTimestamp( columnIndex );
			}
			else
			{
				result = method.invoke( this.resultSet, args );
			}
		}
		else if ( "getTimestamp".equals( methodName ) )
		{
			int columnIndex;
			if ( args[ 0 ] instanceof String )
			{
				columnIndex = this.resultSet.findColumn( ( String ) args[ 0 ] );
			}
			else
			{
				columnIndex = ( Integer ) args[ 0 ];
			}
			result = this.getTimestamp0( columnIndex, args.length == 2 ? ( Calendar ) args[ 1 ] : null );
		}
		else
		{
			result = method.invoke( this.resultSet, args );
		}

		return result;
	}

	private Timestamp getTimestamp0( int columnIndex, Calendar calendar ) throws SQLException
	{
		// this fixes the issue with the incorrectly calculated time for
		// the postgresql driver, other drivers may behave similarly,
		// so we will strip all timezone related information from the
		// input string
		// note that this fix is made so that we do not have to fix all
		// views or behaviour that will manipulate existing timestamps
		// since JDBC does not define a timestamp with additional timezone
		// information, the timezone information will be lost and will be
		// replaced by the timezone of the current system locale on update.
		// note that since we live in a so-called modern era, we will simply
		// ignore the era information being part of the input string, so it
		// is always "AC". note that we will also ignore any fractional seconds
		// as they are not required for the veraweb application
		if ( calendar == null )
		{
			calendar = Calendar.getInstance();
		}

		String tmp = this.resultSet.getString( columnIndex );

		if ( tmp != null && ! ( "".equals( tmp ) ) )
		{
			int pos = 0;

			// skip leading whitespace
			while ( tmp.charAt( pos ) == ' ' )
			{
				pos++;
			}

			StringBuffer read = new StringBuffer();
			while ( pos < tmp.length() && tmp.charAt( pos ) != '-' )
			{
				read.append( tmp.charAt( pos ) );
				pos++;
			}
			if ( read.length() != 4 )
			{
				return null;
			}
			calendar.set( Calendar.YEAR, Integer.valueOf( read.toString() ) );
			read.setLength(0);

			pos++;
			while ( pos < tmp.length() && tmp.charAt( pos ) != '-' )
			{
				read.append( tmp.charAt( pos ) );
				pos++;
			}
			if ( read.length() != 2 )
			{
				return null;
			}
			calendar.set( Calendar.MONTH, Integer.valueOf( read.toString() ) - 1 );
			read.setLength(0);

			pos++;
			while ( pos < tmp.length() && tmp.charAt( pos ) != ' ' )
			{
				read.append( tmp.charAt( pos ) );
				pos++;
			}
			if ( read.length() != 2 )
			{
				return null;
			}
			calendar.set( Calendar.DAY_OF_MONTH, Integer.valueOf( read.toString() ) );
			read.setLength(0);

			// skip whitespace between date and time information
			while ( pos < tmp.length() && tmp.charAt( pos ) == ' ' )
			{
				pos++;
			}

			while ( pos < tmp.length() && tmp.charAt( pos ) != ':' )
			{
				read.append( tmp.charAt( pos ) );
				pos++;
			}
			if ( read.length() != 2 )
			{
				return null;
			}
			calendar.set( Calendar.HOUR_OF_DAY, Integer.valueOf( read.toString() ) );
			read.setLength(0);

			pos++;
			while ( pos < tmp.length() && tmp.charAt( pos ) != ':' )
			{
				read.append( tmp.charAt( pos ) );
				pos++;
			}
			if ( read.length() != 2 )
			{
				return null;
			}
			calendar.set( Calendar.MINUTE, Integer.valueOf( read.toString() ) );
			read.setLength(0);

			pos++;
			while ( pos < tmp.length() && tmp.charAt( pos ) != '+' && tmp.charAt( pos ) != '-' )
			{
				read.append( tmp.charAt( pos ) );
				pos++;
			}
			if ( read.length() != 2 )
			{
				return null;
			}
			calendar.set( Calendar.SECOND, Integer.valueOf( read.toString() ) );
			read.setLength(0);

			return new Timestamp( calendar.getTimeInMillis() );
		}

		return null;
	}
}
