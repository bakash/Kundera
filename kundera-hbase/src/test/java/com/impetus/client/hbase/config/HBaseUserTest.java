/**
 * 
 */
package com.impetus.client.hbase.config;

import java.io.IOException;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import junit.framework.Assert;

import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableNotFoundException;
import org.apache.hadoop.hbase.io.hfile.Compression.Algorithm;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.impetus.client.hbase.junits.HBaseCli;

/**
 * @author Kuldeep Mishra
 * 
 */
public class HBaseUserTest
{

    private EntityManagerFactory emf;

    private HBaseCli cli;

    /**
     * logger used for logging statement.
     */
    private static final Logger logger = LoggerFactory.getLogger(HBaseUserTest.class);

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {
        cli = new HBaseCli();
        cli.startCluster();
        emf = Persistence.createEntityManagerFactory("XmlPropertyTest");
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception
    {
        cli.dropTable("USERXYZ");
        emf.close();
    }

    @Test
    public void test()
    {
        try
        {
            HTableDescriptor hTableDescriptor = HBaseCli.utility.getHBaseAdmin().getTableDescriptor(
                    "USERXYZ".getBytes());
            int count = 0;
            for (HColumnDescriptor columnDescriptor : hTableDescriptor.getColumnFamilies())
            {
                if (columnDescriptor.getNameAsString().equalsIgnoreCase("address"))
                {
                    Assert.assertEquals(Algorithm.valueOf("GZ"), columnDescriptor.getCompactionCompressionType());
                    Assert.assertEquals(Integer.parseInt("1234567"), columnDescriptor.getTimeToLive());
                    Assert.assertEquals(Algorithm.valueOf("GZ"), columnDescriptor.getCompressionType());
                    Assert.assertEquals(Integer.parseInt("5"), columnDescriptor.getMaxVersions());
                    Assert.assertEquals(Integer.parseInt("2"), columnDescriptor.getMinVersions());
                    count++;
                }
                else
                {
                    Assert.assertEquals("age", columnDescriptor.getNameAsString());
                    Assert.assertEquals(Algorithm.valueOf("GZ"), columnDescriptor.getCompactionCompressionType());
                    Assert.assertEquals(Integer.parseInt("12345678"), columnDescriptor.getTimeToLive());
                    Assert.assertEquals(Algorithm.valueOf("GZ"), columnDescriptor.getCompressionType());
                    Assert.assertEquals(Integer.parseInt("6"), columnDescriptor.getMaxVersions());
                    Assert.assertEquals(Integer.parseInt("3"), columnDescriptor.getMinVersions());
                    count++;
                }
            }
            Assert.assertEquals(2, count);
        }
        catch (TableNotFoundException tnfe)
        {
            logger.error("Error during UserTest, caused by :" + tnfe);
        }
        catch (IOException ie)
        {
            logger.error("Error during UserTest, caused by :" + ie);
        }
    }
}
