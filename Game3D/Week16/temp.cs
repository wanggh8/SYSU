using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class temp : MonoBehaviour {

	// Use this for initialization
	void Start () {
		statusGraph tempGraph = new statusGraph (3,3,false);
		node test = new node(3 , 2 ,false);
		operation result = tempGraph.getNextStep(test);
		Debug.Log (result.P + " "  + result.D);
	}
	
	// Update is called once per frame
	void Update () {
		
	}
}
