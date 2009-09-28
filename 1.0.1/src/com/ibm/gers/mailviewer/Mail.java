package com.ibm.gers.mailviewer;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

import exc.com.WCBufferException;
import exc.com.WCBufferedStream;
import exs.serv.WCLogger;
import exs.serv.WCMailAttachment;
import exs.serv.WCMailException;
import exs.serv.WCRC;

public class Mail
{
	private int					companyId_;

	private String				to_					= "";

	private String				subject_			= "";

	private String				body_				= "";

	private MailAttributes		attributes_			= new MailAttributes();

	private String				templateName_;

	private Vector				attachments_		= new Vector();		// 2005-005

	private String				cc_					= "";

	private String				bcc_				= "";

	private String				replyTo_			= "";

	transient private Hashtable	mailRecipientSets_	= new Hashtable();
	
	public int getCompanyId() 
	{
		return companyId_;
	}

	public String getTo()
	{
		return (to_);
	}

	// CORE0068
	public String getCC()
	{
		return (cc_);
	}

	public String getBCC()
	{
		return (bcc_);
	}

	public String getSubject()
	{
		if (subject_ != null)
			return (subject_);
		else
			return (templateName_);
	}
	
	public String getBody(){
		return body_;
	}

	public String getReplyTo()
	{
		return (replyTo_);
	}

	public WCMailAttachment[] getAttachments()
	{
		WCMailAttachment array[];
		array = new WCMailAttachment[attachments_.size()];
		if (attachments_.size() > 0)
		{
			attachments_.copyInto(array);
		}
		return (array);
	}

	public boolean hasAttachments()
	{
		return (attachments_.size() > 0);
	}

	public boolean hasMultipleRecipients()
	{
		return ((to_.indexOf(',') > 0) ? true : false);
	}

	public void fromDisk(String fileName) throws WCMailException
	{
		BufferedInputStream buf;
		FileInputStream f = null;
		byte[] data;
		WCBufferedStream stream;

		try
		{
			f = new FileInputStream(fileName);
			buf = new BufferedInputStream(f);
			data = new byte[buf.available()];
			buf.read(data, 0, data.length);
			stream = new WCBufferedStream();
			stream.setBuffer(data);
			fromStream(stream);
		} catch (IOException e)
		{
			throw new WCMailException(fileName + " " + e.getMessage(),
					WCRC.MAIL_IO_ERROR);
		} catch (WCBufferException e)
		{
			throw new WCMailException(fileName + " " + e.getMessage(),
					WCRC.MAIL_IO_ERROR);
		} finally
		{
			if (f != null)
			{
				try
				{
					f.close();
				} catch (IOException e)
				{
					WCLogger.errorLog(this, "read: msg=" + e.getMessage());
				}
			}
		}
	}

	public void fromStream(WCBufferedStream stream) throws WCBufferException
	{
		boolean b;

		attachments_.clear(); // EXS8652
		mailRecipientSets_.clear(); // EXS8652
		companyId_ = stream.unbufferInt();
		to_ = stream.unbufferString();
		templateName_ = stream.unbufferString();
		if (templateName_.length() == 0)
		{
			templateName_ = null;
		}
		subject_ = stream.unbufferString();
		if (subject_.length() == 0)
		{
			subject_ = null;
		}
		body_ = stream.unbufferString();
		attributes_.fromStream(stream);
		b = stream.unbufferBoolean();
		if (b == true)
		{
			WCMailAttachment attachment = new WCMailAttachment();
			attachment.fromStream(stream);
			addAttachment(attachment);
		}
		// CC SUPPORT
		if (stream.more())
		{
			cc_ = stream.unbufferString();
		} else
		{
			cc_ = "";
		}
		if (stream.more())
		{
			bcc_ = stream.unbufferString();
		} else
		{
			bcc_ = "";
		}
		if (stream.more()) // abg AmexP8-011
		{
			replyTo_ = stream.unbufferString();
		} else
		{
			replyTo_ = "";
		}
		// 2005-005 multi attachment support
		if (stream.more())
		{
			WCMailAttachment attachment;
			int attachmentCount = stream.unbufferInt();
			for (int i = 0; i < attachmentCount; ++i)
			{
				attachment = new WCMailAttachment();
				attachment.fromStream(stream);
				addAttachment(attachment);
			}
		}
	}

	public void addAttachment(WCMailAttachment attachment)
	{
		if (attachment != null)
		{
			attachments_.add(attachment);
		}
	}
}
