package org.marketcetera.photon.views;

import javax.jms.Destination;
import javax.jms.Queue;

import org.springframework.jms.JmsException;
import org.springframework.jms.core.BrowserCallback;
import org.springframework.jms.core.JmsOperations;
import org.springframework.jms.core.MessageCreator;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.jms.core.ProducerCallback;
import org.springframework.jms.core.SessionCallback;

/**
 * A mock <code>JmsOperations</code> that retains the last message sent via the 
 * <code>convertAndSend(Object message)</code> method. The stored message can be 
 * retrieved with a call to <code>getStoredMessage()</code>.
 */
	public class MockJmsOperations implements JmsOperations {
		private Object storedMessage;
		
		public void convertAndSend(Object message) throws JmsException {
			storedMessage = message;
		}
		
		public Object getStoredMessage() {
			return storedMessage;
		}
	
		public void convertAndSend(Destination destination, Object message) throws JmsException {
		}
	
		public void convertAndSend(String destinationName, Object message) throws JmsException {
		}
	
		public void convertAndSend(Object message, MessagePostProcessor postProcessor) throws JmsException {
		}
	
		public void convertAndSend(Destination destination, Object message, MessagePostProcessor postProcessor) throws JmsException {
		}
	
		public void convertAndSend(String destinationName, Object message, MessagePostProcessor postProcessor) throws JmsException {
		}
	
		public Object execute(SessionCallback action) throws JmsException {
			return null;
		}
	
		public Object execute(ProducerCallback action) throws JmsException {
			return null;
		}
	
		public javax.jms.Message receive() throws JmsException {
			return null;
		}
	
		public javax.jms.Message receive(Destination destination) throws JmsException {
			return null;
		}
	
		public javax.jms.Message receive(String destinationName) throws JmsException {
			return null;
		}
	
		public Object receiveAndConvert() throws JmsException {
			return null;
		}
	
		public Object receiveAndConvert(Destination destination) throws JmsException {
			return null;
		}
	
		public Object receiveAndConvert(String destinationName) throws JmsException {
			return null;
		}
	
		public javax.jms.Message receiveSelected(String messageSelector) throws JmsException {
			return null;
		}
	
		public javax.jms.Message receiveSelected(Destination destination, String messageSelector) throws JmsException {
			return null;
		}
	
		public javax.jms.Message receiveSelected(String destinationName, String messageSelector) throws JmsException {
			return null;
		}
	
		public Object receiveSelectedAndConvert(String messageSelector) throws JmsException {
			return null;
		}
	
		public Object receiveSelectedAndConvert(Destination destination, String messageSelector) throws JmsException {
			return null;
		}
	
		public Object receiveSelectedAndConvert(String destinationName, String messageSelector) throws JmsException {
			return null;
		}
	
		public void send(MessageCreator messageCreator) throws JmsException {
		}
	
		public void send(Destination destination, MessageCreator messageCreator) throws JmsException {
		}
	
		public void send(String destinationName, MessageCreator messageCreator) throws JmsException {
		}

		@Override
		public Object browse(BrowserCallback arg0) throws JmsException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object browse(Queue arg0, BrowserCallback arg1)
				throws JmsException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object browse(String arg0, BrowserCallback arg1)
				throws JmsException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object browseSelected(String arg0, BrowserCallback arg1)
				throws JmsException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object browseSelected(Queue arg0, String arg1,
				BrowserCallback arg2) throws JmsException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object browseSelected(String arg0, String arg1,
				BrowserCallback arg2) throws JmsException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object execute(Destination arg0, ProducerCallback arg1)
				throws JmsException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object execute(String arg0, ProducerCallback arg1)
				throws JmsException {
			// TODO Auto-generated method stub
			return null;
		}

	}