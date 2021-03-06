package com.SpringLibrary.SpringbootLibrary.resourceTests;

import org.junit.Assert;
import org.junit.Test;
import java.sql.Date;
import static Resource.gridHelper.caseInsensitiveContains;
import static Resource.gridHelper.overdue;
import static Resource.gridHelper.stringClean;

public class gridHelperTest
{
    private String expectedString;
    private String resultString;

    @Test
    public void stringCleanBadInputTest() throws Exception
    {

        //What the expected result is
        expectedString = "";

        //Sets the result using getter
        resultString = stringClean("3");
        //Compares expected result with the actual result.
        Assert.assertEquals(expectedString, resultString);

        resultString = stringClean("!23214");

        Assert.assertEquals(expectedString, resultString);

        expectedString = "Moth";
        resultString = stringClean("      Moth      ");

        Assert.assertEquals(expectedString, resultString);

        expectedString = "Mister Rogers";
        resultString = stringClean("    Mister Rogers     ");

        Assert.assertEquals(expectedString, resultString);
    }

    @Test
    public void stringCleanGoodInputTest() throws Exception
    {

        //What the expected result is
        expectedString = "Cat";

        //Sets the result using getter
        resultString = stringClean("Cat");
        //Compares expected result with the actual result.
        Assert.assertEquals(expectedString, resultString);

        expectedString = "Turtle";
        resultString = stringClean("Turtle");

        Assert.assertEquals(expectedString, resultString);
    }


    @Test
    public void caseInsensitiveContainsTest() throws Exception
    {

        //Sets the result using getter
        Boolean resultBoolean = caseInsensitiveContains("ROAR", "ro");
        //Compares expected result with the actual result.
        Assert.assertEquals(true, resultBoolean);

        resultBoolean = caseInsensitiveContains("ROAR","rooooo");

        Assert.assertEquals(false, resultBoolean);
    }


    @Test
    public void overdueTest() throws Exception
    {

        //Sets the result using getter
        Date date1 = new Date(System.currentTimeMillis());
        Date date2 = new Date(System.currentTimeMillis());
        //Compares expected result with the actual result.

        resultString = overdue(date1,date2);

        Assert.assertEquals("2017-08-11", resultString);
    }

}
