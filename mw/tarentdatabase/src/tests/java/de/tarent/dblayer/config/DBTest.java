//package de.tarent.dblayer.config;
//
//
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;
//
//import junit.framework.TestCase;
//import de.tarent.dblayer.SchemaCreator;
//import de.tarent.dblayer.engine.DB;
//
//public class DBTest extends TestCase {
//
//	protected void setUp() throws Exception {
//		super.setUp();
//		SchemaCreator.getInstance().setUp(false);
//	}
//
//	public void testStatement() throws SQLException {
//		Runnable runnable = new Runnable() {
//			public void run() {
//				try {
//					//log("start...");
//					Thread.sleep(100);
//
//					//log("query...");
//					Statement statement = DB.getStatement(SchemaCreator.TEST_POOL);
//					//ResultSet resultSet = statement.executeQuery("SELECT " + new Random().nextInt());
//                    ResultSet resultSet = statement.executeQuery("SELECT * FROM person");
//					while (resultSet.next()) {
//						log("result: " + resultSet.getObject(1));
//					}
//
//					//log("close");
//					Thread.sleep(500);
//
//					statement.getConnection().close();
//
//					//System.out.println("s: " + statement.getClass());
//					//System.out.println("rss: " + resultSet.getStatement().getClass());
//					//System.out.println("rs: " + resultSet.getClass());
//
//					//System.out.println(statement.getClass().getSuperclass());
//
//				} catch (Exception e) {
//                    e.printStackTrace();
//                    fail(e.getMessage());
//				}
//			}
//		};
//
//		Thread t0 = new Thread(runnable);
//		t0.start();
//		Thread t1 = new Thread(runnable);
//		t1.start();
//		Thread t2 = new Thread(runnable);
//		t2.start();
//
//		while (t0.isAlive() || t1.isAlive() || t2.isAlive()) {
//			try {
//				Thread.sleep(100);
//			} catch (InterruptedException e) {
//			}
//		}
//	}
//
//	private void log(String msg) {
//		System.out.println(Thread.currentThread().getName() + " " + msg);
//	}
//}
