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
