import java.util.*;

class PostBox implements Runnable {
	static final int MAX_SIZE = 10;
  static final int DELAY = 1000;
  
 	class Message 
	{
    	String sender;
    	String recipient;
    	String msg;
    	Message(String sender, String recipient, String msg) 
		{ 
			this.sender = sender;
			this.recipient = recipient;
			this.msg = msg;
    }

    public String getMsg(){return msg;}

  }    
  
  	private final LinkedList<Message> messages;
  	private LinkedList<Message> myMessages;
  	private String myId;
  	private boolean stop = false;
  
  	public PostBox(String myId) 
	{ 
    	messages = new LinkedList<Message>(); 
    	this.myId = myId;
    	this.myMessages = new LinkedList<Message>();
    	new Thread(this).start();
  	}
  
  	public PostBox(String myId, PostBox p) 
	{
    	this.myId = myId;
    	this.messages = p.messages;
    	this.myMessages = new LinkedList<Message>();
    	new Thread(this).start(); 
  	}	
  
  	public String getId() { return myId; }
  	public void stop() 
	{ 
    	this.stop = true;
  	}
  
  	public void send(String recipient, String msg) 
  	{
      synchronized(messages)
      {
         Message newMsg = new Message( myId,recipient, msg);
         messages.add(newMsg);
      }
  	}

  	public List<String> retreive() 
  	{
      List <String> ret = new LinkedList <String>();
      synchronized(myMessages)
      {
        for(Message note : myMessages)
        {
          String mes = "From " + note.sender + " to " + note.recipient + ".  Message: " + note.msg;
          ret.add(mes);
        }
        myMessages.clear();
        return ret;
      }
    }

  	public void run() 
  	{
    // loop forever
    //   1. approximately once every second move all messages
    //      addressed to this post box from the shared message 
    //      queue to the private myMessages queue
    //   2. also approximately once every second, if the message
    //      queue has more than MAX_SIZE messages, delete oldest messages
    //      so that size is at most MAX_SIZE
      for( ; ; )
      {   
        try
        {
          if(stop != true)
          {
            synchronized(messages)
            {

              synchronized(myMessages)
              {
               Iterator <Message> it = messages.iterator();
               while(it.hasNext())
               {
                 Message m = it.next();
                 if(m.recipient == myId)
                 { 
                   it.remove();
                   myMessages.add(m);
                 }
                
               }
              }

              if(messages.size() >= MAX_SIZE)
              {
               messages.removeFirst();
              }
            }
             Thread.sleep(DELAY);
          }   
        }
        catch(InterruptedException e)
        {

        }
      }
    }
}
