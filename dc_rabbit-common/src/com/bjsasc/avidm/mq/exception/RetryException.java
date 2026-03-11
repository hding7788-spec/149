package com.bjsasc.avidm.mq.exception;

/**
 * 当队列事件处理器需要重试的时候，抛出此异常
 * 
 * @author hwz
 *
 */
public class RetryException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4671531760726539477L;

	public RetryException() {
		super();
	}

	public RetryException(String message) {
		super(message);
	}

	public RetryException(String message, Throwable cause) {
		super(message, cause);
	}

	public RetryException(Throwable cause) {
		super(cause);
	}
}
