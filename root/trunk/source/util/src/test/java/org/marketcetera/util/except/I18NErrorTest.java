package org.marketcetera.util.except;

import org.junit.Test;

public class I18NErrorTest
	extends I18NThrowableTestBase
{
    @Test
    public void empty()
    {
        empty(new Error(),new I18NError());
    }

    @Test
    public void causeWithoutMessage()
    {
        AssertionError nested=new AssertionError();
        causeWithoutMessage(nested,new Error(nested),new I18NError(nested));
    }

    @Test
    public void causeWithMessage()
    {
        AssertionError nested=new AssertionError(TEST_MSG_1);
        causeWithMessage(nested,new Error(nested),new I18NError(nested));
    }

    @Test
    public void causeWithI18NMessage()
    {
        I18NError nested=new I18NError
            (TestMessages.PROVIDER,TestMessages.MID_EXCEPTION,MID_MSG_PARAM);
        causeWithI18NMessage
            (nested,new Error(nested),new I18NError(nested));
    }

    @Test
    public void myMessage()
    {
        myMessage
            (new Error(TEST_MSG_1),
             new I18NError
             (TestMessages.PROVIDER,TestMessages.MID_EXCEPTION,MID_MSG_PARAM));
    }

    @Test
    public void myMessageAndCauseWithoutMessage()
    {
        AssertionError nested=new AssertionError();
        myMessageAndCauseWithoutMessage
            (nested,new Error(TEST_MSG_1,nested),
             new I18NError
             (nested,
              TestMessages.PROVIDER,TestMessages.MID_EXCEPTION,MID_MSG_PARAM));
    }

    @Test
    public void myMessageAndCauseWithMessage()
    {
        AssertionError nested=new AssertionError(TEST_MSG_2);
        myMessageAndCauseWithMessage
            (nested,new Error(TEST_MSG_1,nested),
             new I18NError
             (nested,
              TestMessages.PROVIDER,TestMessages.MID_EXCEPTION,MID_MSG_PARAM));
    }

    @Test
    public void myMessageAndCauseWithI18NMessage()
    {
        I18NError nested=new I18NError
            (TestMessages.PROVIDER,TestMessages.BOT_EXCEPTION);
        myMessageAndCauseWithI18NMessage
            (nested,new Error(TEST_MSG_1,nested),
             new I18NError
             (nested,
              TestMessages.PROVIDER,TestMessages.MID_EXCEPTION,MID_MSG_PARAM));
    }

    @Test
    public void nesting()
    {
        I18NError exBot=new I18NError
            (TestMessages.PROVIDER,TestMessages.BOT_EXCEPTION);
        I18NError exMid=new I18NError
            (exBot,TestMessages.PROVIDER,TestMessages.MID_EXCEPTION,
             MID_MSG_PARAM);
        I18NError exTop=new I18NError
            (exMid,TestMessages.PROVIDER,TestMessages.TOP_EXCEPTION);
        nesting(exBot,exMid,exTop);
    }
}
