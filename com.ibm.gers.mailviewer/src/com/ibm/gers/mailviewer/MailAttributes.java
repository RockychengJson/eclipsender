package com.ibm.gers.mailviewer;

import exc.com.WCBufferException;
import exc.com.WCBufferedStream;
import exs.serv.WCMailAttributes;

public class MailAttributes extends WCMailAttributes
{
	protected void fromStream(WCBufferedStream stream) throws WCBufferException
	{
		super.fromStream(stream);
	}
}
