package com.ptc.extend.ixb;

import wt.util.WTException;
import wt.util.WTMessage;

public class MissingObjectException extends WTException {

    /**
	 * 
	 */
    private static final long serialVersionUID = 247063683698144653L;

    public MissingObjectException() {
    }

    public MissingObjectException(WTMessage wtmessage) {
        super(wtmessage);
    }

    public MissingObjectException(Object aobj[]) {
        super(aobj);
    }

    public MissingObjectException(WTMessage wtmessage, Object aobj[]) {
        super(wtmessage, aobj);
    }

    public MissingObjectException(String s) {
        super(s);
    }

    public MissingObjectException(String s, Object aobj[]) {
        super(s, aobj);
    }

    public MissingObjectException(Throwable throwable) {
        super(throwable);
    }

}
