/*
 * Copyright (c) tarent GmbH
 * Bahnhofstrasse 13 . 53123 Bonn
 * www.tarent.de . info@tarent.de
 *
 * Created on 04.11.2005
 */

package de.tarent.commons.ui;

public class QualityFeedbackAgentDemoMockup extends QualityFeedbackAgent
{
    /** serialVersionUID */
	private static final long serialVersionUID = -1083045893975708438L;

	public QualityFeedbackAgentDemoMockup(String message, Exception exception)
    {
        super(message, exception);
    }

    public void send(String email, String doneText, String message,
            Exception exception)
    {
    }

}
