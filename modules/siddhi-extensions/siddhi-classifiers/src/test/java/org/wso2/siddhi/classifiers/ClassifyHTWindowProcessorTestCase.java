/*
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
*/
package org.wso2.siddhi.classifiers;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.wso2.siddhi.core.SiddhiManager;
import org.wso2.siddhi.core.config.SiddhiConfiguration;
import org.wso2.siddhi.core.event.Event;
import org.wso2.siddhi.core.stream.input.InputHandler;
import org.wso2.siddhi.core.util.EventPrinter;

import java.util.Random;

public class ClassifyHTWindowProcessorTestCase {
    static final Logger log = Logger.getLogger(ClassifyHTWindowProcessorTestCase.class);

    private int inEventCount = 0;
    private int removeEventCount = 0;
    private boolean eventArrived;
    SiddhiConfiguration configuration;
    SiddhiManager siddhiManager;

    @Before
    public void initialize() {
        eventArrived = false;
        configuration = new SiddhiConfiguration();
        configuration.setAsyncProcessing(false);
        siddhiManager = new SiddhiManager(configuration);
    }

    @Test
    public void testHoeffdingTreeClassifier() throws InterruptedException {
        log.info("Hoeffding Tree test is Running");
        siddhiManager.defineStream("define stream weather (outlook nominal(sunny, overcast, rainy), temperature nominal(hot, mild, cool),humidity nominal(high, normal),windy nominal(TRUE, FALSE),play nominal(yes, no))");
        siddhiManager.addQuery("from weather#window.classifyHt(14,no)" +
                "select play " +
                "insert into Results for all-events ;");

        InputHandler loginSucceedEvents = siddhiManager.getInputHandler("weather");
        inEventCount = 0;
        removeEventCount = 0;
        String[] outlook = {"sunny","overcast","rainy"};
        String[] tempreture = {"hot","mild","cool"};
        String[] humid = {"high","normal"};
        String[] windy = {"TRUE", "FALSE"};
        String[] play = {"yes","no"};
       /* while(true) {
            int index1 = new Random().nextInt(Integer.MAX_VALUE) % outlook.length;
            int index2 = new Random().nextInt(Integer.MAX_VALUE) % tempreture.length;
            int index3 = new Random().nextInt(Integer.MAX_VALUE) % humid.length;
            int index4 = new Random().nextInt(Integer.MAX_VALUE) % windy.length;
            int index5 = new Random().nextInt(Integer.MAX_VALUE) % play.length;
            System.out.println(index1+","+index2+","+index3+","+index4+","+index5);
            loginSucceedEvents.send(new Object[]{outlook[index1], tempreture[index2],
                    humid[index3], windy[index4],
                    play[index5]});
        }*/
        loginSucceedEvents.send(new Object[]{"sunny", "hot", "high", "FALSE", "no"});
        loginSucceedEvents.send(new Object[]{"sunny","hot","high","TRUE","no"});
        loginSucceedEvents.send(new Object[]{"overcast","hot","high","FALSE","yes"});
        loginSucceedEvents.send(new Object[]{"rainy","mild","high","FALSE","yes"});
        loginSucceedEvents.send(new Object[]{"rainy","cool","normal","FALSE","yes"});
        loginSucceedEvents.send(new Object[]{"rainy","cool","normal","TRUE","no"});
        loginSucceedEvents.send(new Object[]{"overcast","cool","normal","TRUE","yes"});
        loginSucceedEvents.send(new Object[]{"sunny","mild","high","FALSE","no"});
        loginSucceedEvents.send(new Object[]{"sunny","cool","normal","FALSE","yes"});
        loginSucceedEvents.send(new Object[]{"rainy","mild","normal","FALSE","yes"});
        loginSucceedEvents.send(new Object[]{"sunny","mild","normal","TRUE","yes"});
        loginSucceedEvents.send(new Object[]{"overcast","mild","high","TRUE","yes"});
        loginSucceedEvents.send(new Object[]{"overcast","hot","normal","FALSE","yes"});
        loginSucceedEvents.send(new Object[]{"rainy","mild","high","TRUE","no"});
        // finished publishing traning data-set
        loginSucceedEvents.send(new Object[]{"sunny", "hot", "high", "FALSE", "no"});
        loginSucceedEvents.send(new Object[]{"sunny","hot","high","TRUE","no"});
        loginSucceedEvents.send(new Object[]{"overcast","hot","high","FALSE","yes"});
        loginSucceedEvents.send(new Object[]{"rainy","mild","high","FALSE","yes"});
        loginSucceedEvents.send(new Object[]{"rainy","cool","normal","FALSE","yes"});
        loginSucceedEvents.send(new Object[]{"rainy","cool","normal","TRUE","no"});
        loginSucceedEvents.send(new Object[]{"overcast","cool","normal","TRUE","yes"});
        loginSucceedEvents.send(new Object[]{"sunny","mild","high","FALSE","no"});
        loginSucceedEvents.send(new Object[]{"sunny","cool","normal","FALSE","yes"});
        loginSucceedEvents.send(new Object[]{"rainy","mild","normal","FALSE","yes"});
        loginSucceedEvents.send(new Object[]{"sunny","mild","normal","TRUE","yes"});
        loginSucceedEvents.send(new Object[]{"overcast","mild","high","TRUE","yes"});
        loginSucceedEvents.send(new Object[]{"overcast","hot","normal","FALSE","yes"});
        loginSucceedEvents.send(new Object[]{"rainy","mild","high","TRUE","no"});
        Thread.sleep(1000);
    }

}
