package rlill.reminder;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import org.apache.log4j.Logger;

public class Database {

	private static Logger LOG = Logger.getLogger(Database.class);

	private Connection connection;

	public final static String bbq1 = "select description, ? - year(event_date) as years, "
		   + "dayofmonth(event_date) as month, month(event_date) as day from events_ann "
		   + "where date_format(event_date, ?) between ? and ? "
		   + "   or date_format(event_date, ?) between ? and ? "
		   + "and (flags & ?) <> 0 "
		   + "order by month(event_date), dayofmonth(event_date) asc";

	public final static String bbq2 = "select description, "
			+ "case when date_format(event_date, ?) between ? and ? then ? - year(event_date) "
			+ "when date_format(event_date, ?) between ? and ? then ? - year(event_date) "
			+ "end as years, "
			+ "case when date_format(event_date, ?) between ? and ? then 0 "
			+ "when date_format(event_date, ?) between ? and ? then 1 "
			+ "end as ysort, "
			+ "dayofmonth(event_date) as month, month(event_date) as day from events_ann "
			+ "where date_format(event_date, ?) between ? and ? "
			+ "   or date_format(event_date, ?) between ? and ? "
			+ "and (flags & ?) <> 0 "
			+ "order by ysort, month(event_date), dayofmonth(event_date) asc";

	public boolean init(String url, String user, String password) {

		try {
			// load the DBdriver
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();

			// start the DBdriver
			connection = DriverManager.getConnection(url, user, password);
		}
		catch (Exception e) {
            LOG.error(e.getClass().getName() + ": " + e.getMessage(), e);
            return false;
		}
		return true;
	}

	public void nextBirthdays(int futureDays, int flags, DefaultTableModel tableModel) {

		try {

			Calendar cal = Calendar.getInstance();
			Date now = new Date();
			cal.setTime(now);
			cal.add(Calendar.DAY_OF_MONTH, -1);
			int nowyear = cal.get(Calendar.YEAR);
			now = cal.getTime();

			cal.add(Calendar.DAY_OF_MONTH, futureDays);
			Date then = cal.getTime();

			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

			PreparedStatement stmt = connection.prepareStatement(bbq2);

			int i = 1;
			stmt.setString(i++, String.format("%4d-%%m-%%d", nowyear));
			stmt.setString(i++, df.format(now));
			stmt.setString(i++, df.format(then));
			stmt.setInt(i++, nowyear);

			stmt.setString(i++, String.format("%4d-%%m-%%d", nowyear+1));
			stmt.setString(i++, df.format(now));
			stmt.setString(i++, df.format(then));
			stmt.setInt(i++, nowyear + 1);

			stmt.setString(i++, String.format("%4d-%%m-%%d", nowyear));
			stmt.setString(i++, df.format(now));
			stmt.setString(i++, df.format(then));

			stmt.setString(i++, String.format("%4d-%%m-%%d", nowyear+1));
			stmt.setString(i++, df.format(now));
			stmt.setString(i++, df.format(then));

			stmt.setString(i++, String.format("%4d-%%m-%%d", nowyear));
			stmt.setString(i++, df.format(now));
			stmt.setString(i++, df.format(then));

			stmt.setString(i++, String.format("%4d-%%m-%%d", nowyear+1));
			stmt.setString(i++, df.format(now));
			stmt.setString(i++, df.format(then));

			stmt.setInt(i++, flags);

			LOG.debug(String.format("Query %d days in the future from %s to %s",
					futureDays, now.toString(), then.toString()));
			ResultSet rs = stmt.executeQuery();

			while (rs.next())
			{
				Vector<String> row = new Vector<>();
				row.add(String.format("%02d.%02d.", rs.getInt("month"), rs.getInt("day")));
				row.add(rs.getString("description"));
				row.add(String.format("%d", rs.getInt("years")));
				tableModel.addRow(row);
			}

			rs.close();

		}
		catch (Exception e) {
            LOG.error(e.getClass().getName() + ": " + e.getMessage(), e);
		}
	}

}
