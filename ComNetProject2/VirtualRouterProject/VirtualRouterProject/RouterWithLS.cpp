#pragma once
#include "RouterWithLS.h"
#include "utils.h"

//���캯������������
RouterWithLS::RouterWithLS() {
	local = LOCAL_HOST_NUM;
	//cout << "LS constructor start" << endl;
	RouterLink temp;
	for (int i = 0; i < ROUTER_NODES; i++) {
		vector<RouterLink> tmpVec;
		for (int t = 0; t < ROUTER_NODES; t++) {
			if (routerLinkTable[i][t]) {
				temp.localHostNum = i;
				temp.dstHostNum = t;
				temp.lastModified = time(NULL);
				temp.linkCost = routerLinkTable[i][t];
				temp.linkState = true;
				tmpVec.push_back(temp);
				
			}
		}
		RouterTable.push_back(tmpVec);
	}
	//cout << "LS constructor end" << endl;
	UpdateForwadingTable();
	//cout << "..." << endl;
}
RouterWithLS::RouterWithLS(RouterWithLS const&) {

}





void RouterWithLS::UpdateTimestamp(int routerID)
{
	for (int i = 0; i < RouterTable.size(); i++) {
		for (int j = 0; j < RouterTable[i].size(); j++) {
			if (RouterTable[i][j].dstHostNum == routerID) {
				RouterTable[i][j].lastModified = (int)time(NULL);
			}
		}
	}
}

/*���·�ɱ��г���180��û��ͨ�ŵ�·�ɣ���routeLink�е�linkState��Ϊfalse���޷�����*/
int RouterWithLS::CheckRouterTable() {
	time_t nowTime = (int)time(NULL);
	bool flag = false;
	for (auto i = RouterTable[local].begin(); i != RouterTable[local].end(); i++) {
		//cout << nowTime << " " << i->lastModified << endl;
		if (nowTime - (i->lastModified) <= 180) {

		}
		else {
			if (i->linkState == true) {
				i->linkState = false;
				//UpdateForwardingTable(i->dstHostNum, i->linkCost, INFINITY_COST);
				i->linkCost = INFINITY_COST;
				flag = true;
			}
		}
	}
	if (flag) return 1;
	return 0;
}

/*����ID�����·״̬�������Ա�ʾ�ܹ�����*/
bool RouterWithLS::CheckLinkState(int RouterID) {
	for (auto i = RouterTable[local].begin(); i != RouterTable[local].end(); i++) {
		if (i->dstHostNum == RouterID) {
			return i->linkState == true ? true : false;
		}
	}
	return false;
}

/*����·�ɱ�ı仯������ת����*/
bool RouterWithLS::UpdateForwadingTable() {
	if (DEBUG) cout << "����ת����" << endl;
	int min; int index = 0;
	int nodes = RouterTable[0].size();
	for (int i = 0; i < nodes; i++) {
		visited.push_back(false);
	}
	visited[local] = true;
	for (int i = 0; i < nodes; i++) {
		dist[i] = RouterTable[local][i].linkCost;
		//dist.push_back(RouterTable[local][i].linkCost);
		path[i] = local;
	}

	for (int i = 1; i < nodes; i++) {
		min = 9999;
		for (int j = 0; j < nodes; j++)
		{
			if (visited[j] == false && dist[j] < min)  // �ҵ�Ȩֵ��С
			{
				min = dist[j];
				index = j;
			}
		}
		visited[index] = true;
		for (int j = 0; j < nodes; j++) {
			if (!visited[j] && RouterTable[index][j].linkCost != 9999 && RouterTable[index][j].linkCost + min < dist[j]) {
				dist[j] = RouterTable[index][j].linkCost + min;
				path[j] = index;
			}
		}
	}
	
	for (int i = 0; i < nodes; i++) {
		if (i != local) {
			int num = 0;
			vector<int> realpath;
			ForwardingRouterInfo temp;
			realpath.push_back(i);
			cout << endl << local << "��" << i << "��̾����ǣ�" <<
				dist[i] << "��·���ǣ�" << i;
			int t = path[i];
			while (t != local) {
				realpath.push_back(t);
				cout << "<--" << t << endl;
				t = path[t];
				num++;
			}
			
			temp.nextHostNum = realpath.back();
			realpath.push_back(t);
			temp.jumpNum = num + 1;
			temp.targetedHostNum = i;

			ForwardingTable.push_back(temp);
			
		}
		if (i == local) {
			ForwardingRouterInfo temp;
			temp.nextHostNum = i;
			temp.jumpNum = 0;
			temp.targetedHostNum = i;
			ForwardingTable.push_back(temp);
		}
	}
	return true;
}

/*������������·�ɸ�����Ϣ*/
bool RouterWithLS::ReceiveUpdatingRouterInfo(vector<vector<RouterLink>> fromOtherRouterTable) {

	for (int i = 0; i < RouterTable[0].size(); i++) {
		for (int t = 0; t < RouterTable[0].size(); t++) {
			RouterTable[i][t].lastModified = time(NULL);
			RouterTable[i][t].linkCost = fromOtherRouterTable[i][t].linkCost;
			RouterTable[i][t].linkState = fromOtherRouterTable[i][t].linkState;
		}
	}
	UpdateForwadingTable();
	return true;
}

/*���͸��������·�ɵĸ�����Ϣ*/
vector<vector<RouterLink>> RouterWithLS::SendUpdatingRouterInfo() {
	return RouterTable;
}

/*�޸��뵱ǰ�ڽӽ�����·������Ϣ*/
bool RouterWithLS::ModifyNeighborRouterInfo(int neighborRouterID, int cost) {
	if (neighborRouterID >= RouterTable[0].size()) {
		return false;
	}
	RouterTable[local][neighborRouterID].linkCost = cost;
	return true;
}

/*������Ϣ��ȷ����һ����ַID*/
int RouterWithLS::DecideForwardingAddress(int routerID) {
	//cout << routerID << "neibu" << ForwardingTable[routerID].nextHostNum << endl;
	return ForwardingTable[routerID].nextHostNum;
}



/*����·�ɱ�*/
void RouterWithLS::DrawRouterTable() {
	cout << "The Router Table:" << endl;
	for (auto i = RouterTable[local].begin(); i != RouterTable[local].end(); i++) {
		cout << "Local Number: " << i->localHostNum << " Neighbor: " << i->dstHostNum << " Link Status: " << i->linkState << " ";
		if (i->linkCost == INFINITY_COST) cout << "INFINITY" << endl;
		else cout << i->linkCost << endl;
	}

}

/*��ӡת����*/
void RouterWithLS::PrintForwardingTable() {
	for (auto i = ForwardingTable.begin(); i != ForwardingTable.end(); i++) {
		cout << "Destination: " << i->targetedHostNum << " Next number: " << i->nextHostNum << " ";
		if (i->jumpNum == INFINITY_COST) cout << "INFINITY" << endl;
		else cout <<" hops " << i->jumpNum << endl;
	}
}


