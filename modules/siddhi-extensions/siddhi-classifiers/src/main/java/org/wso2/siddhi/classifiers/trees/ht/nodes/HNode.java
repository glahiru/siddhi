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
package org.wso2.siddhi.classifiers.trees.ht.nodes;


import org.wso2.siddhi.classifiers.trees.ht.Instance;
import org.wso2.siddhi.classifiers.trees.ht.SplitNode;
import org.wso2.siddhi.classifiers.trees.ht.WeightMass;
import org.wso2.siddhi.classifiers.trees.ht.utils.Utils;
import org.wso2.siddhi.query.api.definition.Attribute;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * Abstract base class for nodes in a Hoeffding tree
 *
 */
public abstract class HNode implements Serializable {
    /**
     * For serialization
     */
    private static final long serialVersionUID = 197233928177240264L;

    /**
     * Class distribution at this node
     */
    public Map<String, WeightMass> weightedClassDist = new LinkedHashMap<String, WeightMass>();

    /**
     * Holds the leaf number (if this is a leaf)
     */
    protected int leafNum;

    /**
     * Holds the node number (for graphing purposes)
     */
    protected int nodeNum;

    /**
     * Construct a new HNode
     */
    public HNode() {
    }

    /**
     * Construct a new HNode with the supplied class distribution
     *
     * @param classDistrib
     */
    public HNode(Map<String, WeightMass> classDistrib) {
        weightedClassDist = classDistrib;
    }

    /**
     * Returns true if this is a leaf
     *
     * @return
     */
    public boolean isLeaf() {
        return true;
    }

    /**
     * The size of the class distribution
     *
     * @return the number of entries in the class distribution
     */
    public int numEntriesInClassDistribution() {
        return weightedClassDist.size();
    }

    /**
     * Returns true if the class distribution is pure
     *
     * @return true if the class distribution is pure
     */
    public boolean classDistributionIsPure() {
        int count = 0;
        for (Map.Entry<String, WeightMass> el : weightedClassDist.entrySet()) {
            if (el.getValue().weight > 0) {
                count++;

                if (count > 1) {
                    break;
                }
            }
        }

        return (count < 2);
    }

    /**
     * Update the class frequency distribution with the supplied instance
     *
     * @param inst the instance to update with
     */
    public void updateDistribution(Instance inst) {
        if (inst.classIsMissing()) {
            return;
        }
        String classVal = inst.stringValue(inst.classAttribute());

        WeightMass m = weightedClassDist.get(classVal);
        if (m == null) {
            m = new WeightMass();
            m.weight = 1.0;

            weightedClassDist.put(classVal, m);
        }
        m.weight += inst.weight();
    }

    /**
     * Return a class probability distribution computed from the frequency counts
     * at this node
     *
     * @param classAtt the class attribute
     * @return a class probability distribution
     * @throws Exception if a problem occurs
     */
    public double[] getDistribution(Instance inst, Attribute classAtt)
            throws Exception {
        double[] dist = new double[classAtt.numValues()];

        for (int i = 0; i < classAtt.numValues(); i++) {
            WeightMass w = weightedClassDist.get(classAtt.value(i));
            if (w != null) {
                dist[i] = w.weight;
            } else {
                dist[i] = 1.0;
            }
        }

        Utils.normalize(dist);
        return dist;
    }

    public int installNodeNums(int nodeNum) {
        nodeNum++;
        this.nodeNum = nodeNum;

        return nodeNum;
    }

    public int dumpTree(int depth, int leafCount, StringBuffer buff) {

        double max = -1;
        String classVal = "";
        for (Map.Entry<String, WeightMass> e : weightedClassDist.entrySet()) {
            if (e.getValue().weight > max) {
                max = e.getValue().weight;
                classVal = e.getKey();
            }
        }
        buff.append(classVal + " (" + String.format("%-9.3f", max).trim() + ")");
        leafCount++;
        leafNum = leafCount;

        return leafCount;
    }

    public void printLeafModels(StringBuffer buff) {
    }

    public void graphTree(StringBuffer text) {

        double max = -1;
        String classVal = "";
        for (Map.Entry<String, WeightMass> e : weightedClassDist.entrySet()) {
            if (e.getValue().weight > max) {
                max = e.getValue().weight;
                classVal = e.getKey();
            }
        }

        text.append("N" + nodeNum + " [label=\"" + classVal + " ("
                + String.format("%-9.3f", max).trim() + ")\" shape=box style=filled]\n");
    }

    /**
     * Print a textual description of the tree
     *
     * @param printLeaf true if leaf models (NB, NB adaptive) should be output
     * @return a textual description of the tree
     */
    public String toString(boolean printLeaf) {

        installNodeNums(0);

        StringBuffer buff = new StringBuffer();

        dumpTree(0, 0, buff);

        if (printLeaf) {
            buff.append("\n\n");
            printLeafModels(buff);
        }

        return buff.toString();
    }

    /**
     * Return the total weight of instances seen at this node
     *
     * @return the total weight of instances seen at this node
     */
    public double totalWeight() {
        double tw = 0;

        for (Map.Entry<String, WeightMass> e : weightedClassDist.entrySet()) {
            tw += e.getValue().weight;
        }

        return tw;
    }

    /**
     * Return the leaf that the supplied instance ends up at
     *
     * @param inst         the instance to find the leaf for
     * @param parent       the parent node
     * @param parentBranch the parent branch
     * @return the leaf that the supplied instance ends up at
     */
    public LeafNode leafForInstance(Instance inst, SplitNode parent,
                                    String parentBranch) {
        return new LeafNode(this, parent, parentBranch);
    }

    /**
     * Update the node with the supplied instance
     *
     * @param inst the instance to update with
     * @throws Exception if a problem occurs
     */
    public abstract void updateNode(Instance inst) throws Exception;

    public int getLeafNum() {
        return leafNum;
    }

    public int getNodeNum() {
        return nodeNum;
    }
}

