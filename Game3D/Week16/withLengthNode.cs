using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class withLengthNode:node{
	public int length;
	public node startNode;
	public withLengthNode(node thisNode , int length , node startNode = null) :base(thisNode){
		this.length = length;
		this.startNode = startNode;
	}

	public static bool ifNodeExitInList(node findNode , List<withLengthNode> searchList){
		foreach (node temp in searchList) {
			if (node.ifTwoNodeSame(temp , findNode) ) {
				return true;
			}
		}
		return false;
	}
}
