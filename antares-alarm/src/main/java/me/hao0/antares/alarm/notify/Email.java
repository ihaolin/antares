package me.hao0.antares.alarm.notify;

public class Email {

	/**
     * 收件人。
     * <p>多个用,号分割。
     */
    private String to;
    
    /**
     * 抄送人。
     * <p>多个用,号分割。
     */
    private String bcc;
    
    /**
     * 主题。
     */
    private String subject;
    
    /**
     * 正文。
     */
    private String content;

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}
	
	public String getBcc() {
		return bcc;
	}

	public void setBcc(String bcc) {
		this.bcc = bcc;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return "Email{" +
				"to='" + to + '\'' +
				", bcc='" + bcc + '\'' +
				", subject='" + subject + '\'' +
				", content='" + content + '\'' +
				'}';
	}
}