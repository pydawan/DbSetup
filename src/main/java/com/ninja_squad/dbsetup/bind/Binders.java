package com.ninja_squad.dbsetup.bind;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;

/**
 * Utility class allowing to get various kinds of binders. The {@link DefaultBinderConfiguration} uses binders
 * returned by this class, based on the type of the parameter.
 * @author JB
 */
public final class Binders {

    private static final Binder DEFAULT_BINDER = new DefaultBinder();
    private static final Binder DATE_BINDER = new DateBinder();
    private static final Binder TIMESTAMP_BINDER = new TimestampBinder();
    private static final Binder DECIMAL_BINDER = new DecimalBinder();
    private static final Binder INTEGER_BINDER = new IntegerBinder();
    private static final Binder TIME_BINDER = new TimeBinder();
    private static final Binder STRING_BINDER = new StringBinder();

    private Binders() {
    }

    /**
     * Returns the default binder, which uses <code>stmt.setObject()</code> to bind the parameter.
     */
    public static Binder defaultBinder() {
        return DEFAULT_BINDER;
    }

    /**
     * Returns a binder suitable for columns of type CHAR and VARCHAR. The returned binder supports values of type
     * <ul>
     *   <li><code>String</code></li>
     *   <li><code>enum</code>: the name of the enum is used as bound value</li>
     *   <li><code>Object: the <code>toString()</code> of the object is used as bound value</code></li>
     * </ul>
     */
    public static Binder stringBinder() {
        return STRING_BINDER;
    }

    /**
     * Returns a binder suitable for columns of type DATE. The returned binder supports values of type
     * <ul>
     *   <li><code>java.sql.Date</code></li>
     *   <li><code>java.util.Date</code>: the milliseconds of the date are used to construct a java.sql.Date</li>
     *   <li><code>java.util.Calendar: the milliseconds of the calendar are used to construct a java.sql.Date</code>
     *   </li>
     *   <li><code>String</code>: the string is transformed to a java.sql.Date using the <code>Date.valueOf()</code>
     *       method</li>
     * </ul>
     * If the value is none of these types, <code>stmt.setObject()</code> is used to bind the value.
     */
    public static Binder dateBinder() {
        return DATE_BINDER;
    }

    /**
     * Returns a binder suitable for columns of type TIMESTAMP. The returned binder supports values of type
     * <ul>
     *   <li><code>java.sql.Timestamp</code></li>
     *   <li><code>java.util.Date</code>: the milliseconds of the date are used to construct a java.sql.Timestamp</li>
     *   <li><code>java.util.Calendar: the milliseconds of the calendar are used to construct a
     *   java.sql.Timestamp</code></li>
     *   <li><code>String</code>: the string is transformed to a java.sql.Timestamp using the
     *       <code>Timestamp.valueOf()</code> method</li>
     * </ul>
     * If the value is none of these types, <code>stmt.setObject()</code> is used to bind the value.
     */
    public static Binder timestampBinder() {
        return TIMESTAMP_BINDER;
    }

    /**
     * Returns a binder suitable for columns of type TIME. The returned binder supports values of type
     * <ul>
     *   <li><code>java.sql.Time</code></li>
     *   <li><code>java.util.Date</code>: the milliseconds of the date are used to construct a java.sql.Time</li>
     *   <li><code>java.util.Calendar: the milliseconds of the calendar are used to construct a java.sql.Time</code>
     *   </li>
     *   <li><code>String</code>: the string is transformed to a java.sql.Time using the
     *       <code>Time.valueOf()</code> method</li>
     * </ul>
     * If the value is none of these types, <code>stmt.setObject()</code> is used to bind the value.
     */
    public static Binder timeBinder() {
        return TIME_BINDER;
    }

    /**
     * Returns a binder suitable for numeric, decimal columns. The returned binder supports values of type
     * <ul>
     *   <li><code>String</code>: the string is transformed to a java.math.BigDecimal using its constructor</li>
     * </ul>
     * If the value is none of these types, <code>stmt.setObject()</code> is used to bind the value.
     */
    public static Binder decimalBinder() {
        return DECIMAL_BINDER;
    }

    /**
     * Returns a binder suitable for numeric, integer columns. The returned binder supports values of type
     * <ul>
     *   <li><code>enum</code>: the enum is transformed into an integer by taking its ordinal</li>
     *   <li><code>String</code>: the string is transformed to a java.math.BigInteger using its constructor</li>
     * </ul>
     * If the value is none of these types, <code>stmt.setObject()</code> is used to bind the value.
     */
    public static Binder integerBinder() {
        return INTEGER_BINDER;
    }

    /**
     * The implementation for {@link Binders#stringBinder()}
     * @author JB
     */
    private static final class StringBinder implements Binder {
        @Override
        public void bind(java.sql.PreparedStatement stmt, int param, Object value) throws java.sql.SQLException {
            if (value instanceof String) {
                stmt.setString(param, (String) value);
            }
            else if (value instanceof Enum<?>) {
                stmt.setString(param, ((Enum<?>) value).name());
            }
            else if (value == null) {
                stmt.setObject(param, null);
            }
            else {
                stmt.setString(param, value.toString());
            }
        }

        @Override
        public String toString() {
            return "Binders.stringBinder";
        }
    }

    /**
     * The implementation for {@link Binders#timeBinder()}
     * @author JB
     */
    private static final class TimeBinder implements Binder {
        @Override
        public void bind(java.sql.PreparedStatement stmt, int param, Object value) throws java.sql.SQLException {
            if (value instanceof Time) {
                stmt.setTime(param, (Time) value);
            }
            else if (value instanceof java.util.Date) {
                stmt.setTime(param, new Time(((java.util.Date) value).getTime()));
            }
            else if (value instanceof java.util.Calendar) {
                stmt.setTime(param, new Time(((java.util.Calendar) value).getTimeInMillis()));
            }
            else if (value instanceof String) {
                stmt.setTime(param, Time.valueOf((String) value));
            }
            else {
                stmt.setObject(param, value);
            }
        }

        @Override
        public String toString() {
            return "Binders.timeBinder";
        }
    }

    /**
     * The implementation for {@link Binders#integerBinder()}
     * @author JB
     */
    private static final class IntegerBinder implements Binder {
        @Override
        public void bind(java.sql.PreparedStatement stmt, int param, Object value) throws java.sql.SQLException {
            if (value instanceof BigInteger) {
                stmt.setObject(param, value, Types.BIGINT);
            }
            else if (value instanceof Enum<?>) {
                stmt.setInt(param, ((Enum<?>) value).ordinal());
            }
            else if (value instanceof String) {
                stmt.setObject(param, new BigInteger((String) value), Types.BIGINT);
            }
            else {
                stmt.setObject(param, value);
            }
        }

        @Override
        public String toString() {
            return "Binders.integerBinder";
        }
    }

    /**
     * The implementation for {@link Binders#decimalBinder()}
     * @author JB
     */
    private static final class DecimalBinder implements Binder {
        @Override
        public void bind(java.sql.PreparedStatement stmt, int param, Object value) throws java.sql.SQLException {
            if (value instanceof String) {
                stmt.setBigDecimal(param, new BigDecimal((String) value));
            }
            else {
                stmt.setObject(param, value);
            }
        }

        @Override
        public String toString() {
            return "Binders.decimalBinder";
        }
    }

    /**
     * The implementation for {@link Binders#timeStampBinder()}
     * @author JB
     */
    private static final class TimestampBinder implements Binder {
        @Override
        public void bind(java.sql.PreparedStatement stmt, int param, Object value) throws java.sql.SQLException {
            if (value instanceof Timestamp) {
                stmt.setTimestamp(param, (Timestamp) value);
            }
            else if (value instanceof java.util.Date) {
                stmt.setTimestamp(param, new Timestamp(((java.util.Date) value).getTime()));
            }
            else if (value instanceof java.util.Calendar) {
                stmt.setTimestamp(param, new Timestamp(((java.util.Calendar) value).getTimeInMillis()));
            }
            else if (value instanceof String) {
                stmt.setTimestamp(param, Timestamp.valueOf((String) value));
            }
            else {
                stmt.setObject(param, value);
            }
        }

        @Override
        public String toString() {
            return "Binders.timestampBinder";
        }
    }

    /**
     * The implementation for {@link Binders#dateBinder()}
     * @author JB
     */
    private static final class DateBinder implements Binder {
        @Override
        public void bind(java.sql.PreparedStatement stmt, int param, Object value) throws java.sql.SQLException {
            if (value instanceof Date) {
                stmt.setDate(param, (Date) value);
            }
            else if (value instanceof java.util.Date) {
                stmt.setDate(param, new Date(((java.util.Date) value).getTime()));
            }
            else if (value instanceof java.util.Calendar) {
                stmt.setDate(param, new Date(((java.util.Calendar) value).getTimeInMillis()));
            }
            else if (value instanceof String) {
                stmt.setDate(param, Date.valueOf((String) value));
            }
            else {
                stmt.setObject(param, value);
            }
        }

        @Override
        public String toString() {
            return "Binders.dateBinder";
        }
    }

    /**
     * The implementation for {@link Binders#defaultBinder()}
     * @author JB
     */
    private static final class DefaultBinder implements Binder {
        @Override
        public void bind(java.sql.PreparedStatement stmt, int param, Object value) throws java.sql.SQLException {
            stmt.setObject(param, value);
        }

        @Override
        public String toString() {
            return "Binders.defaultBinder";
        }
    }
}