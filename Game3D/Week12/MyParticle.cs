using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class MyParticle : MonoBehaviour {

	// Use this for initialization
	ParticleSystem exhaust;
	void Start () {
		exhaust = GetComponent<ParticleSystem>();
	}
	
	// Update is called once per frame
	void Update () {
		RaycastHit hit;
       		 if (Physics.Raycast (Camera.main.ScreenPointToRay (Input.mousePosition), out hit)) {
            		exhaust.startColor =  new Color32(17, 255, 84, 255);
       		 }	
	}
}


