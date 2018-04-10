//package de.tarent.dblayer;
//
//import java.sql.Connection;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;
//
//import org.apache.commons.logging.Log;
//
//import de.tarent.commons.logging.LogFactory;
//import de.tarent.dblayer.engine.DB;
//
///**
// * Tests to ensure a correct pooling.
// */
//public class PoolingTest extends junit.framework.TestCase {
//	/** Logger */
//	public static final Log logger = LogFactory.getLog(PoolingTest.class);
//
//	public PoolingTest() {
//		super();
//	}
//
//	public PoolingTest(String init) {
//		super(init);
//	}
//
//	/**
//	 * Set up a test database connection.
//	 */
//	public void setUp() throws Exception {
//		super.setUp();
//		SchemaCreator.getInstance().setUp(false);
//	}
//
//	/**
//	 * Tear down the test database connection.
//	 */
//	protected void tearDown() throws Exception {
//		// TODO close database connection.
//		super.tearDown();
//	}
//
//	/**
//	 * Test time while creating a connection.
//	 *
//	 * @throws SQLException
//	 */
//	public void testConnectionCreationTime() throws SQLException {
//		// count of connections
//		int connectionCount = 10;
//		Connection[] cons = new Connection[connectionCount];
//
//		// open connections, first time.
//		long start = System.currentTimeMillis();
//		for (int i = 0; i < connectionCount; i++) {
//			cons[i] = DB.getConnection(SchemaCreator.TEST_POOL);
//		}
//		logger.info("first creation of " + connectionCount + " connection: "
//				+ ((System.currentTimeMillis() - start) / connectionCount)
//				+ " ms/connection");
//
//		// close connections, first time.
//		start = System.currentTimeMillis();
//		for (int i = 0; i < connectionCount; i++) {
//			cons[i].close();
//		}
//		logger.info("return connections to pool: "
//				+ ((System.currentTimeMillis() - start) / connectionCount)
//				+ " ms/connection");
//
//		// open connection, second test.
//		start = System.currentTimeMillis();
//		for (int i = 0; i < connectionCount; i++) {
//			cons[i] = DB.getConnection(SchemaCreator.TEST_POOL);
//		}
//		long reuseTime = (System.currentTimeMillis() - start) / connectionCount;
//		logger.info("reuse of " + connectionCount + " connection: "
//				+ ((System.currentTimeMillis() - start) / connectionCount)
//				+ " ms/connection");
//
//		// close connections, second test.
//		for (int i = 0; i < connectionCount; i++) {
//			cons[i].close();
//		}
//
//		assertTrue("Reuse of connections must not larger than 1 ms.", reuseTime <= 1);
//	}
//
//    /**
//     * This method tests some assumptions of the db layer library concerning the
//     * commons pooling framework.
//     *
//     * @throws SQLException
//     */
//	public void testWorkingOnClosedConnection() throws SQLException {
//		String poolname = SchemaCreator.TEST_POOL;
//
//		Connection c;
//		Statement s;
//
//		// Some simple open/close test.
//		c = DB.getConnection(poolname);
//		assertFalse(c.isClosed());
//		c.close();
//		assertTrue(c.isClosed());
//
//		c = DB.getStatement(poolname).getConnection();
//		assertFalse(c.isClosed());
//		c.close();
//		assertTrue(c.isClosed());
//
//		s = DB.getStatement(poolname);
//		c = s.getConnection();
//		assertFalse(c.isClosed());
//		s.close();
//		assertFalse(c.isClosed());
//		c.close();
//		assertTrue(c.isClosed());
//
//		// Working on a DB.getConnection statement which connection was closed.
//		c = DB.getConnection(poolname);
//		s = c.createStatement();
//		try {
//			ResultSet rs = s.executeQuery("SELECT 1");
//			s.close();
//			assertTrue(rs.next()); // this may fail throwing a SQLException.
//			assertEquals(1, rs.getInt(1));
//			assertFalse(rs.next());
//		} catch (SQLException e) {
//			logger.info("Pooling does not allow read attempts on ResultSet created by statements now closed");
//		}
//	}
//}
