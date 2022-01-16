using System.Collections;
using System.Collections.Generic;
using UnityEngine;


public class statusGraph{
	private List<node> allGraphNodes;
	private operation[] nodeOperations = {new operation (0, 1) , new operation (1, 0) , 
		new operation (1, 1) , new operation (2, 0) , new operation (0, 2) };
	private int maxP;
	private int maxD;
	private bool boatStartSize ;
	private node endStatusNode ;
	 
	public statusGraph(int maxP ,int maxD , bool boatStartSize){
		this.maxP = maxP;
		this.maxD = maxD;
		this.boatStartSize = boatStartSize;
		if (boatStartSize == true) {
			endStatusNode = new node (maxP, maxD, !boatStartSize);
		} else {
			endStatusNode =  new node (0, 0, !boatStartSize);
		}

		createGraph ();
	}


	private void createGraph(){
		allGraphNodes = new List<node> ();
		if (boatStartSize == true) {
			//开始船在右岸
			allGraphNodes.Add(new node(0, 0, boatStartSize));
		} else {
			//开始船在左岸
			allGraphNodes.Add(new node(maxP, maxD, boatStartSize));
		}

		for (int index = 0 ; index != allGraphNodes.Count ; index++) {
			node thisNode = allGraphNodes[index];

			foreach (operation op in nodeOperations) {
				//船在右岸
				node adjcentNode;
				if (thisNode.ifBoatSizeRight ) {
					//当前船在右岸，由于node是左岸，因此船送人过来，node的P和D应该增加。
				  	adjcentNode = thisNode + op;
				} else {
					adjcentNode = thisNode - op;
				}
				node anotherSizeNode = getAnotherSizeNode (adjcentNode);
				//若两岸node都合法
				if (ifNodeValid (adjcentNode) && ifNodeValid(anotherSizeNode)  ) {
					//若node在Graph中，node不加入graph
					adjcentNode = getNodeFromList (adjcentNode);
					//两个node互相接通(函数内会判断是否已有那个新的node，有就不加了)
					adjcentNode.addAdjacentNode (thisNode);
					thisNode.addAdjacentNode (adjcentNode);
					/*Debug.Log (thisNode.P + " " + thisNode.D + " " + thisNode.ifBoatSizeRight +
						"->" + adjcentNode.P + " " +  adjcentNode.D + " " + adjcentNode.ifBoatSizeRight );*/
				}

			}
		}
	}

	private bool ifNodeValid(node test){
		return (test.P >= test.D || test.P == 0) && test.D <= maxD && test.P <= maxP;
	}

	private node getNodeFromList(node findNode){
		foreach (node temp in allGraphNodes) {
			//两个statu等价，除了P和D要相等外，船也要在同一岸，list有node就返回node
			if (node.ifTwoNodeSame(temp , findNode) ) {
				return temp;
			}
		}
		//linkedList没有node，就把node加入里面 
		allGraphNodes.Add(findNode);
		return findNode;
	}
	//得到另一岸的node
	private node getAnotherSizeNode(node thisSizeNode){
		return new node(maxP - thisSizeNode.P 
			, maxD - thisSizeNode.D , !thisSizeNode.ifBoatSizeRight );
	}

	public operation getNextStep(node nowNode){
		node anotherSizeNode = getAnotherSizeNode (nowNode);//用当前node得到graph中的node

		if(ifNodeValid (nowNode) && ifNodeValid(anotherSizeNode) ){
			nowNode = getNodeFromList (nowNode);
			node nextNode = getStartNodeByWidthSearch (nowNode);
			if (nextNode == null) {
				return null;
			} else {
				return new operation (Mathf.Abs (nowNode.P - nextNode.P), Mathf.Abs (nowNode.D - nextNode.D));
			}
		}
		else{
			return null;
		}

	}

	private node getStartNodeByWidthSearch(node startNode){
		List<withLengthNode> alreadySearchNode = new List<withLengthNode>();
		//开始点已搜索
		alreadySearchNode.Add (new withLengthNode(startNode , 0) );
		//与开始点相接的点全部放入list中，起始点就是相接的node自身，也即当前下一步要走的地方
		foreach (node adjacentNode in startNode.adjacentNodes) {
			if (node.ifTwoNodeSame (adjacentNode, endStatusNode)) {
				return adjacentNode;
			} else {
				alreadySearchNode.Add (new withLengthNode (adjacentNode, 1, adjacentNode));
			}

		}

		for (int i = 1; i < alreadySearchNode.Count; i++) {
			//alreadySearchNode [i]是当前要搜索的点，将所有与它相接且不在list中的node都放到待搜索list中
			foreach (node adjacentNode in alreadySearchNode[i].adjacentNodes ) {
				//如果当前node的下一个node是目的点，返回node的开始点，也就是下一步要走的地方
				if (node.ifTwoNodeSame (adjacentNode, endStatusNode)) {
					return alreadySearchNode[i].startNode;
				}
				//如果当前node的下一个node不是目的点，看它是否已被搜索，没被搜索就加入list
				else if (!withLengthNode.ifNodeExitInList (adjacentNode, alreadySearchNode)) {
					alreadySearchNode.Add (new withLengthNode(adjacentNode , alreadySearchNode[i].length + 1 , alreadySearchNode[i].startNode ) );
				}

			}
		}
		return null;
	}
}

