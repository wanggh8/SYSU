#include "utils.h"

string IPTable[5] = { "127.0.0.1","127.0.0.1", "127.0.0.1", "127.0.0.1", "127.0.0.1"};

//string IPTable[5] = { "172.20.10.4","172.20.10.4", "172.20.10.2", "172.20.10.2", "172.20.10.6" };

// 需要保持发送端口大于接收端口  100
int sendPortTable[5] = { 3330,3331,3332,3333,3334 };

int receivePortTable[5] = { 3220,3221,3222,3223,3224 };

int ROUTER_PROTOCOL = DV;

int LOCAL_HOST_NUM = 0;

int RECEIVE_PORT = 0;           /*客户端的接收端口*/

int SEND_PORT = 0;              /*客户端发送端口*/

/*
map<int, sockaddr_in>& GetNumToAddrReceiveRouterMappingTable()
{
	// TODO: 在此处插入 return 语句
	static map<int, sockaddr_in> NumToAddrReceiveRouterMappingTable;
	return NumToAddrReceiveRouterMappingTable;
}
*/
map<int, sockaddr_in> NumToAddrReceiveRouterMappingTable;

/*
map<int, sockaddr_in>& GetNumToAddrSendRouterMappingTable()
{
	// TODO: 在此处插入 return 语句
	static map<int, sockaddr_in> NumToAddrSendRouterMappingTable;
	return NumToAddrSendRouterMappingTable;
}
*/
map<int, sockaddr_in> NumToAddrSendRouterMappingTable;

int routerLinkTable[MAX_ROUTER_NODES][MAX_ROUTER_NODES] = { 0 };
/*
int& GetRouterLinkTable()
{
	static int routerLinkTable[MAX_ROUTER_NODES][MAX_ROUTER_NODES];
	return routerLinkTable;
}*/


void InitRouterLinkTable() {
	// TODO
	cout << "初始化链路信息表~" << endl;
	int Link[ROUTER_NODES][ROUTER_NODES] = { {INFINITY_COST,INFINITY_COST, 10, 4, 3},
											 {INFINITY_COST, INFINITY_COST, INFINITY_COST, 7, 9},
											 {10, INFINITY_COST, INFINITY_COST,6,INFINITY_COST},
											 {4,7,6,INFINITY_COST,INFINITY_COST},
											 {3, 9,INFINITY_COST,INFINITY_COST, INFINITY_COST},
	};
	
	for (int i = 0; i < ROUTER_NODES; i++) {
		for (int j = 0; j < ROUTER_NODES; j++) {
			routerLinkTable[i][j] = Link[i][j];
		}
	}
}

void PrintRouterLinkTable()
{
	cout << setw(5) << " ";
	for (int i = 0; i < ROUTER_NODES; i++) {
		cout  <<"|" << setw(5) << i;
	}
	cout << endl;
	for (int i = 0; i < ROUTER_NODES; i++) {
		cout << setw(5) << i << "|";
		for (int j = 0; j < ROUTER_NODES; j++) {
			if (routerLinkTable[i][j] == INFINITY_COST)
				cout << setw(5) << "*" ;
			else
				cout << setw(5) << routerLinkTable[i][j];
		}
		cout << endl;
	}
	cout << " * 号代表不直接相连" << endl;
	cout << "-----------------------------------" << endl;
}

void InitRouterMappingTable() {
	// TODO
	cout << "初始化路由表，包括接收端和发送端" << endl;
	//string IPTable[5] = { "172.20.10.2","127.0.0.1", "172.20.10.4", "172.20.10.6", "127.0.0.1" };
	//int sendPortTable[5] = { 3330,3331,3332,3333,3334 };
	//int receivePortTable[5] = { 3220,3221,3222,3223,3224 };
	
	//map<int, sockaddr_in> NumToAddrSendRouterMappingTable = GetNumToAddrSendRouterMappingTable();
	//map<int, sockaddr_in> NumToAddrReceiveRouterMappingTable = GetNumToAddrReceiveRouterMappingTable();

	for (int i = 0; i < ROUTER_NODES; i++) {
		sockaddr_in sendAddr = { 0 };
		sendAddr.sin_family = AF_INET;
		sendAddr.sin_port = htons(sendPortTable[i]);
		sendAddr.sin_addr.S_un.S_addr = inet_addr(IPTable[i].c_str());
		NumToAddrSendRouterMappingTable.insert(make_pair(i, sendAddr));
		sockaddr_in receiveAddr = { 0 };
		receiveAddr.sin_family = AF_INET;
		receiveAddr.sin_port = htons(receivePortTable[i]);
		receiveAddr.sin_addr.S_un.S_addr = inet_addr(IPTable[i].c_str());
		NumToAddrReceiveRouterMappingTable.insert(make_pair(i, receiveAddr));
	}
}

void PrintRouterMappingTable()
{
	//map<int, sockaddr_in> NumToAddrSendRouterMappingTable = GetNumToAddrSendRouterMappingTable();
//	map<int, sockaddr_in> NumToAddrReceiveRouterMappingTable = GetNumToAddrReceiveRouterMappingTable();

	cout << setw(6) << "路由ID" << setw(10) << "IP地址" << setw(10) <<"    接收端口" << setw(10) << "  发送端口" << endl;
	auto it1 = NumToAddrReceiveRouterMappingTable.begin();
	auto it2 = NumToAddrSendRouterMappingTable.begin();
	for (it1, it2 ; it1 != NumToAddrReceiveRouterMappingTable.end(); it1++, it2++) {
		cout << setw(4) << it1->first << setw(15) <<  string(inet_ntoa(it1->second.sin_addr)) << setw(8) << ntohs(it1->second.sin_port) << setw(8) << ntohs(it2->second.sin_port) << endl;
	}
	
	
	
}

void DrawRouterTable()
{

}
