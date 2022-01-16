using System.Collections;
using System.Collections.Generic;
using UnityEngine;


public class node{
	//node存的是左岸的P，D
	public int P;
	public int D;
	public bool ifBoatSizeRight; //false left , true right
	public List<node> adjacentNodes;
	public node (node cpyNode){
		this.P = cpyNode.P;
		this.D = cpyNode.D;
		this.ifBoatSizeRight = cpyNode.ifBoatSizeRight;
		this.adjacentNodes = new List<node>(cpyNode.adjacentNodes);

	}

	public node(int P , int D , bool boatSize){
		this.P = P;
		this.D = D;
		this.ifBoatSizeRight = boatSize;
		adjacentNodes = new List<node> ();
	}

	public void addAdjacentNode(node newAdjacentNode){
		if (!ifNodeExitInList (newAdjacentNode , adjacentNodes)) {
			adjacentNodes.Add (newAdjacentNode);
		}
	}

	public static bool ifNodeExitInList(node findNode , List<node> searchList){
		foreach (node temp in searchList) {
			if (ifTwoNodeSame(temp , findNode) ) {
				return true;
			}
		}
		return false;
	}
	//两个statu等价，除了P和D要相等外，船也要在同一岸.
	public static bool ifTwoNodeSame(node first , node second){
		if (first.P == second.P && first.D == second.D
		    && first.ifBoatSizeRight == second.ifBoatSizeRight) {
			return true;
		} else {
			return false;
		}

	}
	//到相接的点所用的操作
	public operation[] getOperationByAnthoerNode(){
		operation[] result = new operation[adjacentNodes.Count];
		for (int i = 0; i < adjacentNodes.Count; i++) {
			result [i] = new operation (Mathf.Abs(adjacentNodes [i].P - P),Mathf.Abs(adjacentNodes [i].D - D) );
		}
		return result;
	}

	public static node operator - (node first , operation second ){
		return new node (first.P - second.P, first.D - second.D , !first.ifBoatSizeRight);
	}

	public static node operator + (node first , operation second ){
		return new node (first.P + second.P, first.D + second.D ,  !first.ifBoatSizeRight);
	}
}

