package org.objectweb.joram.mom.proxies;


import fr.dyade.aaa.agent.AgentId;
import org.junit.Assert;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;


public class AmplClientContextEncodingTest {
    @Test(timeout = 10000)
    public void run() throws Exception {
        EncodingHelper.init();
        AgentId proxyId = null;
        int id = 40;
        ClientContext cc1 = new ClientContext(proxyId, id);
        Assert.assertEquals(131076, ((int) (((ClientContext) (cc1)).getEncodableClassId())));
        Assert.assertEquals(17, ((int) (((ClientContext) (cc1)).getEncodedSize())));
        Assert.assertNull(((ClientContext) (cc1)).getProxyId());
        Assert.assertEquals("ClientContext (proxyId=null,id=40,tempDestinations=[],deliveringQueues={},transactionsTable=null,started=false,cancelledRequestId=-1,activeSubs=[],repliesBuffer=[])", ((ClientContext) (cc1)).toString());
        boolean o_run__7 = cc1.getActiveSubList().add("sub1");
        Assert.assertTrue(o_run__7);
        boolean o_run__9 = cc1.getActiveSubList().add("sub2");
        Assert.assertTrue(o_run__9);
        AgentId queue1 = new AgentId(((short) (50)), ((short) (60)), 70);
        AgentId o_run__13 = cc1.getDeliveringQueueTable().put(queue1, queue1);
        Assert.assertNull(o_run__13);
        AgentId queue2 = new AgentId(((short) (80)), ((short) (90)), 100);
        AgentId o_run__17 = cc1.getDeliveringQueueTable().put(queue2, queue2);
        Assert.assertNull(o_run__17);
        AgentId tmpDest1 = new AgentId(((short) (110)), ((short) (120)), 130);
        boolean o_run__21 = cc1.getTempDestinationList().add(tmpDest1);
        Assert.assertTrue(o_run__21);
        AgentId tmpDest2 = new AgentId(((short) (120)), ((short) (130)), 140);
        boolean o_run__25 = cc1.getTempDestinationList().add(tmpDest2);
        Assert.assertTrue(o_run__25);
        checkEncoding(cc1);
        Assert.assertEquals(131076, ((int) (((ClientContext) (cc1)).getEncodableClassId())));
        Assert.assertEquals(65, ((int) (((ClientContext) (cc1)).getEncodedSize())));
        Assert.assertNull(((ClientContext) (cc1)).getProxyId());
        Assert.assertEquals("ClientContext (proxyId=null,id=40,tempDestinations=[#110.120.130, #120.130.140],deliveringQueues={#50.60.70=#50.60.70, #80.90.100=#80.90.100},transactionsTable=null,started=false,cancelledRequestId=-1,activeSubs=[sub1, sub2],repliesBuffer=[])", ((ClientContext) (cc1)).toString());
        Assert.assertTrue(o_run__7);
        Assert.assertTrue(o_run__9);
        Assert.assertNull(o_run__13);
        Assert.assertNull(o_run__17);
        Assert.assertTrue(o_run__21);
        Assert.assertTrue(o_run__25);
    }

    private void checkEncoding(ClientContext cc) throws Exception {
        byte[] bytes = EncodingHelper.encode(cc);
        ClientContext ccDec = ((ClientContext) (EncodingHelper.decode(cc.getEncodableClassId(), bytes)));
        junit.framework.Assert.assertEquals(cc.getId(), ccDec.getId());
        assertEquals(cc.getActiveSubList(), ccDec.getActiveSubList());
        assertEquals(cc.getDeliveringQueueTable(), ccDec.getDeliveringQueueTable());
        assertEquals(cc.getTempDestinationList(), ccDec.getTempDestinationList());


    }
}

