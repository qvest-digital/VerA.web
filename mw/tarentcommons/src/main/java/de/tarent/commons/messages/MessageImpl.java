package de.tarent.commons.messages;

import java.text.MessageFormat;

/**
 * This simple message helper class help you to i18n your application
 * and make error messages compile safe. Use this with the static
 * {@link MessageHelper#init() init}-method of the {@link MessageHelper}.
 *
 * @author Christoph Jerolimov, tarent GmbH
 */
public class MessageImpl implements Message {
	/** The key of this message. */
	private String key;
	/** The source of this message. */
	private String source;
	/** The untransformed message. */
	private String message;

	/**
	 * This class implement a {@link Message} and use the given parameter
	 * <code>key</code>, <code>source</code> and <code>message</code> for
	 * {@link #getKey()}, {@link #getSource()} and transforming message
	 * with the different {@link #getMessage()} methods.
	 *
	 * @param key
	 * @param source
	 * @param message
	 */
	MessageImpl(String key, String source, String message) {
		this.key = key;
		this.source = source;
		this.message = message;
	}

	/** {@inheritDoc} */
	public String getKey() {
		return key;
	}

	/** {@inheritDoc} */
	public String getSource() {
		return source;
	}

	/** {@inheritDoc} */
	public String getPlainMessage() {
		return message;
	}

	/** {@inheritDoc} */
	public String getMessage() {
		return getMessage(new Object[] {});
	}

	/** {@inheritDoc} */
	public String getMessage(Object arg0) {
		return getMessage(new Object[] { arg0 });
	}

	/** {@inheritDoc} */
	public String getMessage(Object arg0, Object arg1) {
		return getMessage(new Object[] { arg0, arg1 });
	}

	/** {@inheritDoc} */
	public String getMessage(Object arg0, Object arg1, Object arg2) {
		return getMessage(new Object[] { arg0, arg1, arg2 });
	}

	/** {@inheritDoc} */
	public String getMessage(Object arg0, Object arg1, Object arg2, Object arg3) {
		return getMessage(new Object[] { arg0, arg1, arg2, arg3 });
	}

	/** {@inheritDoc} */
	public String getMessage(Object arg0, Object arg1, Object arg2, Object arg3, Object arg4) {
		return getMessage(new Object[] { arg0, arg1, arg2, arg3, arg4 });
	}

	/** {@inheritDoc} */
	public String getMessage(Object[] arguments) {
		return MessageFormat.format(getPlainMessage(), arguments);
	}

	/** {@inheritDoc} */
	public String toString() {
		return super.toString() + " [source=" + getSource() + "; key=" + getKey() + "]";
	}
}
