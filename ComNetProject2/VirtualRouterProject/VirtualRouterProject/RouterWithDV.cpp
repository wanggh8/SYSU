#pragma once
#include "RouterWithDV.h"
#include "utils.h"


RouterWithDV::RouterWithDV() {
	LOCAL = LOCAL_HOST_NUM;
	RouterLink temp;
	ForwardingRouterInfo T;
	for (int i = 0; i < 5; i++) {
		if (routerLinkTable[LOCAL][i] && routerLinkTable[LOCAL][i] != INFINITY_COST) {
			temp.dstHostNum = i;
			temp.lastModified = time(NULL);
			temp.linkCost = routerLinkTable[LOCAL][i];
			temp.linkState = true;
			temp.localHostNum = LOCAL;
			RouterTable.push_back(temp);
		}
		else continue;
	}
	for (auto i = RouterTable.begin(); i != RouterTable.end(); i++) {
		T.targetedHostNum = i->dstHostNum;
		T.nextHostNum = i->dstHostNum;
		T.jumpNum = i->linkCost;
		ForwardingTable.push_back(T);
	}
	//cout << "DV Construct" << endl;
}


void RouterWithDV::CheckRouterTable() {
	//cout << LOCAL << "$: ";
	//cout << "CheckRouterTable()" << endl;
	time_t nowTime = (int)time(NULL);
	for (auto i = RouterTable.begin(); i != RouterTable.end(); i++) {
		if (nowTime - (i->lastModified) <= 30) {

		}
		else {
			if (i->linkState == true) {
				i->linkState = false;
				UpdateForwardingTable(i->dstHostNum, i->linkCost, INFINITY_COST);
				i->linkCost = INFINITY_COST;
				if (DEBUG) cout << "The link to neibor router " << i->dstHostNum << " breaks down." << endl;
			}
		}
	}
}

bool RouterWithDV::CheckLinkState(int RouterID) {
	for (auto i = RouterTable.begin(); i != RouterTable.end(); i++) {
		if (i->dstHostNum == RouterID) {
			return i->linkState == true ? true : false;
		}
		else continue;
	}
	return false;
}


bool RouterWithDV::UpdateForwardingTable(vector<ForwardingRouterInfo>& fromOtherRouterTable, const int fromNum) {
	bool hasChanged = false;
	
	int cost;
	for (int i = 0; i < RouterTable.size(); i++) {
		if (RouterTable[i].dstHostNum == fromNum) {
			RouterTable[i].linkState = true;
			RouterTable[i].lastModified = time(NULL);
			if (RouterTable[i].linkCost == INFINITY_COST) {
				RouterTable[i].linkCost = routerLinkTable[RouterTable[0].localHostNum][fromNum];
			}
			cost = RouterTable[i].linkCost;
		}
		else continue;
	}
	for (int i = 0; i < fromOtherRouterTable.size(); i++) {
		bool hasExiste = false;
		if (fromOtherRouterTable[i].targetedHostNum == RouterTable[0].localHostNum) {
			if (fromOtherRouterTable[i].nextHostNum == RouterTable[0].localHostNum) {
				for (auto h = RouterTable.begin(); h != RouterTable.end(); h++) {
					if (h->dstHostNum == fromNum) {
						cost = h->linkCost = fromOtherRouterTable[i].jumpNum;
					}
				}
			}
			continue;
		}
		for (auto d = ForwardingTable.begin(); d != ForwardingTable.end(); d++) {
			if (d->targetedHostNum == fromOtherRouterTable[i].targetedHostNum) {
				hasExiste = true;
				if (fromOtherRouterTable[i].nextHostNum != RouterTable[0].localHostNum) {
					if (fromOtherRouterTable[i].jumpNum < d->jumpNum - cost) {
						d->jumpNum = fromOtherRouterTable[i].jumpNum + cost;
						d->nextHostNum = fromNum;
						hasChanged = true;
					}
					else if (fromOtherRouterTable[i].jumpNum == INFINITY_COST && d->nextHostNum == fromNum) {
						d->jumpNum = fromOtherRouterTable[i].jumpNum;
					}
					else if (d->nextHostNum == fromNum) {
						d->jumpNum = fromOtherRouterTable[i].jumpNum + cost;
						d->nextHostNum = fromNum;
						hasChanged = true;
					}
					else continue;
				}
				else continue;
			}
			else continue;
		}
		if (!hasExiste) {
			if (fromOtherRouterTable[i].targetedHostNum != RouterTable[0].localHostNum) {
				hasChanged = true;
				ForwardingRouterInfo addNew;
				addNew.targetedHostNum = fromOtherRouterTable[i].targetedHostNum;
				addNew.nextHostNum = fromNum;
				addNew.jumpNum = fromOtherRouterTable[i].jumpNum + cost;
				ForwardingTable.push_back(addNew);
			}
		}
	}
	return hasChanged;
}

bool RouterWithDV::ReceiveUpdatingRouterInfo(vector<ForwardingRouterInfo>& fromOtherRouterTable, const int fromNum) {
	return UpdateForwardingTable(fromOtherRouterTable, fromNum);
}

bool RouterWithDV::UpdateForwardingTable(const int nextNum, const int st, const int cost) {
	cout << LOCAL << "$: ";
	bool hasChanged = false;
	for (auto i = ForwardingTable.begin(); i != ForwardingTable.end(); i++) {
		if (i->nextHostNum == nextNum) {
			if (cost == INFINITY_COST) {
				i->jumpNum = cost;
			}
			else {
				if (i->jumpNum == INFINITY_COST) {
					if (i->targetedHostNum == nextNum) i->jumpNum = i->jumpNum - st + cost;
					else continue;
				}
				else {
					i->jumpNum = i->jumpNum - st + cost;
				}
			}
			if (DEBUG) cout << "Forwardong Table updated for destination subnet " << nextNum << endl;
			hasChanged = true;
		}
	}
	return hasChanged;
}

vector<ForwardingRouterInfo>* RouterWithDV::SendUpdatingRouterInfo(vector<ForwardingRouterInfo>* toOtherRouterTable) {
	for (auto i = ForwardingTable.begin(); i != ForwardingTable.end(); i++) {
		ForwardingRouterInfo temp;
		temp.jumpNum = i->jumpNum;
		temp.nextHostNum = i->nextHostNum;
		temp.targetedHostNum = i->targetedHostNum;
		toOtherRouterTable->push_back(temp);
	}
	return toOtherRouterTable;
}

bool RouterWithDV::ModifyNeighborRouterInfo(int neighborRouterID, int cost) {
	for (auto i = RouterTable.begin(); i != RouterTable.end(); i++) {
		if (i->dstHostNum == neighborRouterID) {
			if (cost == INFINITY_COST) {
				if (i->linkCost != cost) {
					UpdateForwardingTable(i->dstHostNum, i->linkCost, INFINITY_COST);
					i->linkCost = cost;
					i->linkState = false;
				}
				else continue;
			}
			else {
				UpdateForwardingTable(i->dstHostNum, i->linkCost, cost);
				i->linkCost = cost;
				i->linkState = true;
			}
		}
	}
	return true;
}

int RouterWithDV::DecideForwardingAddress(int routerID) {
	cout << LOCAL << "$: ";
	for (auto i = ForwardingTable.begin(); i != ForwardingTable.end(); i++) {
		if (i->targetedHostNum == routerID && i->jumpNum != INFINITY_COST) {
			return i->nextHostNum;
		}
		else continue;
	}
	printf("Error:There is not anyone next router to choice.\nLocal Number: %d  Destination: %d", RouterTable[0].localHostNum, routerID);
	printf("Debug$ the ForwardingTable:\n");
	PrintForwardingTable();
	exit(0);
}

void RouterWithDV::PrintForwardingTable() {
	cout << LOCAL << "$: " << endl;
	for (auto i = ForwardingTable.begin(); i != ForwardingTable.end(); i++) {
		cout << "Destination: " << i->targetedHostNum << " Next number: " << i->nextHostNum << " ";
		if (i->jumpNum == INFINITY_COST) cout << "INFINITY" << endl;
		else cout << i->jumpNum << endl;
	}
}

void RouterWithDV::DrawRouterTable() {
	cout << LOCAL << "$: ";
	cout << "The Router Table:" << endl;
	for (auto i = RouterTable.begin(); i != RouterTable.end(); i++) {
		cout << "Local Number: " << i->localHostNum << " Neighbor: " << i->dstHostNum << " Link Status: " << i->linkState << " ";
		if (i->linkCost == INFINITY_COST) cout << "INFINITY" << endl;
		else cout << i->linkCost << endl;
	}
}

