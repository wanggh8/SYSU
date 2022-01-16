#pragma once
#include "ControlUnit.h"
#include "utils.h"
#include "RouterWithDV.h"
#include "RouterWithLS.h"
#include "Message.h"
#include "SocketService.h"

int ControlUnit::exitFlag = 0;
ControlUnit* ControlUnit::m_staticSelf = NULL;
CRITICAL_SECTION ControlUnit::Section = { 0 };

/*初始化socket*/
bool ControlUnit::InitSocket(string sendIP, int sendPort, string receiveIP, int receivePort)
{
	routerProtocol = ROUTER_PROTOCOL;
	if (SocketService::Instance().InitSocket(sendIP, sendPort, receiveIP, receivePort)) {
		cout << "ip" << sendIP << endl;
		cout << "初始化socket环境成功,可以发送和接收消息了~~~" << endl;
		cout << "***********************************************************" << endl << endl;
		return true;
	}
	else {
		cout << "socket环境初始化失败,请重新检查程序和网络设置~~" << endl;
		cout << "***********************************************************" << endl << endl;
		return false;
	}
}

/*关闭socket*/
bool ControlUnit::ExitSocket()
{
	exitFlag = 1;
	if (SocketService::Instance().ExitSocket()) {
		cout << "关闭socket成功~" << endl;
		return true;
	}
	else {
		cout << "关闭socket失败" << endl;
		return false;
	}
	return false;
}

/*主动发起消息报文*/
bool ControlUnit::SendMessageToOtherRouter(string msg, int dstRouterID)
{
	int nextRouterID = 0;
	if (ROUTER_PROTOCOL == LS)
		nextRouterID = RouterWithLS::Instance().DecideForwardingAddress(dstRouterID);
	else if (ROUTER_PROTOCOL == DV)
		nextRouterID = RouterWithDV::Instance().DecideForwardingAddress(dstRouterID);

	char*  protoMsg = Message::Instance().CreateMessage(msg, LOCAL_HOST_NUM, dstRouterID, nextRouterID);
	cout << "主动发送的报文内容---------" << endl;
	cout << protoMsg << endl;
	cout << "------------------------------" << endl;
	if (SocketService::Instance().SendMessageToDst(protoMsg, dstRouterID)) {
		cout << "报文发送成功" << endl;
		return true;
	}
	else {
		cout << "发送失败，请检查网络状态" << endl;
		return false;
	}
}

/***************************************************************
函数功能：该函数用于向其他结点发送路由消息
函数输入： 线程参数
函数输出： 线程退出标志
函数依赖：  Message::Instance().CreateExchangeMessageWithLS
			Message::Instance().CreateExchangeMessageWithDV
			RouterWithLS::Instance().GetDVRouterTable
			RouterWithDV::Instance().GetDVRouterTable
			SocketService::Instance().SendMessageToDst
全局变量：  NumToAddrRouterMappingTable
Author: qyh
Date created: 18/11/21
Date finished: 18/11/21
***************************************************************/
bool ControlUnit::SendRouterInfoToOtherRouter()
{
	/*根据协议，准备向其他结点发送自己的路由信息*/
	if (ROUTER_PROTOCOL == LS) {
		for (int i = 0; i < RouterWithLS::Instance().GetLSRouterTable().size(); i++) {
			if (i == LOCAL_HOST_NUM)
				continue;

			if (RouterWithLS::Instance().GetLSRouterTable().at(LOCAL_HOST_NUM).at(i).linkCost == INFINITY) {
				continue;
			}
			/*生成报文*/
			string msg = Message::Instance().CreateLSMessage(&RouterWithLS::Instance().GetLSRouterTable(), i);
			//cout << msg << endl;
			//cout << i << endl;
			if (SocketService::Instance().SendMessageToDst((char*)msg.c_str(), i)) {
				//	cout << "发送路由消息给其他结点" << endl;
				return true;
			}
			else {
				cout << "发送路由消息出错" << endl;
				return false;
			}
		}
	}
	else if (ROUTER_PROTOCOL == DV) {

		for (int i = 0; i < RouterWithDV::Instance().GetDVRouterTable().size(); i++) {
			char* msg = { 0 };
			int dstRouterID = RouterWithDV::Instance().GetDVRouterTable().at(i).dstHostNum;
			if (!RouterWithDV::Instance().GetDVRouterTable().at(i).linkState) {
				continue;
			}
			/*生成报文*/
			Message::Instance().CreateDVMessage(&RouterWithDV::Instance().GetDVForwardingTable(), dstRouterID);
			if (SocketService::Instance().SendMessageToDst(msg, i)) {
				cout << "发送路由消息给其他结点" << endl;
				return true;
			}
			else {
				cout << "发送路由消息出错" << endl;
				return false;
			}
		}
	}
	return true;
}


/*******************************************************************
函数功能：线程函数，用于接收消息，判断消息是应该转发还是更新路由消息
函数输入：线程必备的参数，实际未使用
函数输出：返回0
函数依赖：RouterWithLS::Instance().GetDVRouterTable()
		  RouterWithLS::Instance().DecideForwardingAddress
		  RouterWithDV::Instance().GetDVRouterTable
		  RouterWithDV::Instance().ReceiveUpdatingRouterInfo
全局变量：NumToAddrRouterMappingTable  exitFlag
Author: qyh
Date created: 18/11/20
Date finished: 18/11/21
*********************************************************************/
DWORD __stdcall ControlUnit::StartReceivingThread(LPVOID lpParameter)
{

	while (1) {
		//cout << "接收消息线程-----" << endl;

		EnterCriticalSection(&Section);    /*进入临界区*/

		char msg[BUFF_SIZE] = { 0 };            /*等待报文*/

		SocketService::Instance().ReceiveMessageFromSrc(msg);  /*等待，接收报文，将结果存在msg中*/

		if (strlen(msg) == 0) {

		}
		else if (strlen(msg) < 100) {
			Sleep(20);
			cout << "接收消息线程（跟踪信息）----------" << endl;
			cout << msg << endl;   // 非报文消息，此处打印回显消息
		}
		else {
			cout << "接收消息线程（接收报文消息）----------" << endl;

			Json::Value jsonBody = Message::Instance().DecodeMessage(msg, jsonBody);
			Message::Instance().Print(jsonBody);

			if (jsonBody["Message_Type"] == FORWARDING_MESSAGE) {     /*接收到转发报文，下一步需要转发下一跳*/
				if (jsonBody["Router_Protocol"] == LS) {
					cout << "收到LS协议消息报文。消息来自" << jsonBody["From_Addr"] << endl; /**************************/
					//cout << jsonBody["Dst_Addr"] << string(inet_ntoa(NumToAddrReceiveRouterMappingTable[LOCAL_HOST_NUM].sin_addr)) << endl;
					if (jsonBody["Dst_Port"].asInt() == htons(NumToAddrReceiveRouterMappingTable[LOCAL_HOST_NUM].sin_port)
						&& jsonBody["Dst_Addr"] == string(inet_ntoa(NumToAddrReceiveRouterMappingTable[LOCAL_HOST_NUM].sin_addr))) {
						cout << "消息内容是： " << jsonBody["Msg"];
						string returnToSrc = "The message arrived the destination. from " + IPTable[LOCAL_HOST_NUM];
						int router_id = jsonBody["Src_Num"].asInt();
						int router_receive_port = htons(NumToAddrReceiveRouterMappingTable[router_id].sin_port);
						SocketService::Instance().SendMessageToSrc(returnToSrc, jsonBody["Src_Addr"].asString(), router_receive_port);
						RouterWithLS::Instance().UpdateTimestamp(jsonBody["Src_Num"].asInt());
						RouterWithLS::Instance().UpdateTimestamp(jsonBody["From_Num"].asInt());

					}
					else {
						cout << "开始转发该报文" << endl;
						jsonBody["From_Addr"] = string(inet_ntoa(NumToAddrSendRouterMappingTable[LOCAL_HOST_NUM].sin_addr));
						jsonBody["From_Port"] = NumToAddrSendRouterMappingTable[LOCAL_HOST_NUM].sin_port;
						jsonBody["From_Num"] = LOCAL_HOST_NUM;
						/*查询转发表,得到下一跳 IP PORT NUM*/
						jsonBody["Next_Addr"] = string(inet_ntoa(NumToAddrReceiveRouterMappingTable[RouterWithLS::Instance().DecideForwardingAddress(jsonBody["Dst_Num"].asInt())].sin_addr));
						jsonBody["Next_Port"] = NumToAddrReceiveRouterMappingTable[RouterWithLS::Instance().DecideForwardingAddress(jsonBody["Dst_Num"].asInt())].sin_port;
						jsonBody["Next_Num"] = RouterWithLS::Instance().DecideForwardingAddress(jsonBody["Dst_Num"].asInt());
						jsonBody["Cost"] = jsonBody["Cost"].asInt() + routerLinkTable[LOCAL_HOST_NUM][RouterWithLS::Instance().DecideForwardingAddress(jsonBody["Dst_Num"].asInt())];
						/*发送报文*/
						char _msg[BUFF_SIZE] = { 0 };

						string s = Message::Instance().EncodeMessage(&jsonBody, _msg);

						string ip = IPTable[LOCAL_HOST_NUM];
						RouterWithLS::Instance().UpdateTimestamp(jsonBody["Src_Num"].asInt());
						RouterWithLS::Instance().UpdateTimestamp(jsonBody["From_Num"].asInt());

						if (SocketService::Instance().SendMessageToDst((char*)s.c_str(), jsonBody["Dst_Num"].asInt())) {
							cout << "转发消息报文成功" << endl;
							Message::Instance().Print(jsonBody);
							cout << "转发报文内容如上" << endl;
							string returnToSrc = "The message arrived the middle route.Forwarded sucessfully. From " + string(ip);
							int router_id = jsonBody["Src_Num"].asInt();
							int router_receive_port = htons(NumToAddrReceiveRouterMappingTable[router_id].sin_port);
							SocketService::Instance().SendMessageToSrc(returnToSrc, jsonBody["Src_Addr"].asString(), router_receive_port);
						}
						else {
							string returnToSrc = "The message arrived the middle route.Forwarded unsucessfully. From " + string(ip);
							int router_id = jsonBody["Src_Num"].asInt();
							int router_receive_port = htons(NumToAddrReceiveRouterMappingTable[router_id].sin_port);
							SocketService::Instance().SendMessageToSrc(returnToSrc, jsonBody["Src_Addr"].asString(), router_receive_port);
							cout << "转发消息报文失败" << endl;
						}
					}

				}
				else if (jsonBody["Router_Protocol"] == DV) {
					cout << "DV" << endl;
					string ip = IPTable[LOCAL_HOST_NUM];
					cout << "收到DV协议消息报文。消息来自" << jsonBody["From_Addr"] << ":" << jsonBody["From_Port"] << endl;
					if (jsonBody["Dst_Addr"] == string(inet_ntoa(NumToAddrReceiveRouterMappingTable[LOCAL_HOST_NUM].sin_addr))) {
						cout << "收到DV协议消息报文。消息来自" << jsonBody["Src_Addr"] << ":" << jsonBody["Src_Port"] << endl;
						cout << "消息内容是： " << jsonBody["Msg"] << endl;
						string returnToSrc = "The message arrived the destination. from " + string(ip);
						int router_id = jsonBody["Src_Num"].asInt();
						int router_receive_port = htons(NumToAddrReceiveRouterMappingTable[router_id].sin_port);
						SocketService::Instance().SendMessageToSrc(returnToSrc, jsonBody["Src_Addr"].asString(), router_receive_port);
					}
					else {
						cout << "开始转发该报文" << endl;
						jsonBody["From_Addr"] = string(inet_ntoa(NumToAddrSendRouterMappingTable[LOCAL_HOST_NUM].sin_addr));
						jsonBody["From_Port"] = NumToAddrSendRouterMappingTable[LOCAL_HOST_NUM].sin_port;
						jsonBody["From_Num"] = LOCAL_HOST_NUM;
						/*查询转发表,得到下一跳 IP PORT*/
						jsonBody["Next_Addr"] = string(inet_ntoa(NumToAddrReceiveRouterMappingTable[RouterWithDV::Instance().DecideForwardingAddress(jsonBody["Dst_Num"].asInt())].sin_addr));
						jsonBody["Next_Port"] = NumToAddrReceiveRouterMappingTable[RouterWithDV::Instance().DecideForwardingAddress(jsonBody["Dst_Num"].asInt())].sin_port;
						jsonBody["Cost"] = jsonBody["Cost"].asInt() + routerLinkTable[LOCAL_HOST_NUM][RouterWithDV::Instance().DecideForwardingAddress(jsonBody["Dst_Num"].asInt())];
						/*发送报文*/
						char _msg[BUFF_SIZE] = { 0 };
						cout << "...." << endl;
						string s = Message::Instance().EncodeMessage(&jsonBody, _msg);
						strcpy_s(_msg, s.length(), s.c_str());

						//cout << Message::Instance().EncodeMessage(&jsonBody, _msg) << endl;
						if (SocketService::Instance().SendMessageToDst((char*)s.c_str(), jsonBody["Dst_Num"].asInt())) {
							cout << "转发消息报文成功" << endl;
							string returnToSrc = "The message arrived the middle route.Forwarded sucessfully. From " + string(ip);
							int router_id = jsonBody["Src_Num"].asInt();
							int router_receive_port = htons(NumToAddrReceiveRouterMappingTable[router_id].sin_port);
							SocketService::Instance().SendMessageToSrc(returnToSrc, jsonBody["Src_Addr"].asString(), router_receive_port);
						}
						else {
							cout << "转发消息报文失败" << endl;
							int router_id = jsonBody["Src_Num"].asInt();
							int router_receive_port = htons(NumToAddrReceiveRouterMappingTable[router_id].sin_port);
							string returnToSrc = "The message arrived the middle route.Forwarded unsucessfully. From " + string(ip);
							SocketService::Instance().SendMessageToSrc(returnToSrc, jsonBody["Src_Addr"].asString(), router_receive_port);
						}
					}
				}
				else {
					cout << "收到未知类型协议消息报文。消息来自" << jsonBody["From_Addr"] << ":" << jsonBody["From_Port"] << endl;

				}
			}
			else if (jsonBody["Message_Type"] == UPDATE_MESSAGE) { /*接收到路由更新报文*/

				// 报文回显
				string returnToSrc = "The update message arrived the route. From " + IPTable[LOCAL_HOST_NUM];
				int router_id = jsonBody["Src_Num"].asInt();
				int router_receive_port = htons(NumToAddrReceiveRouterMappingTable[router_id].sin_port);
				SocketService::Instance().SendMessageToSrc(returnToSrc, jsonBody["Src_Addr"].asString(), router_receive_port);
				RouterWithLS::Instance().UpdateTimestamp(jsonBody["Src_Num"].asInt());
			
				if (jsonBody["Router_Protocol"] == LS) {
					vector<vector<RouterLink>> routerTable;
					for (int i = 0; i < ROUTER_NODES; i++) {
						vector<RouterLink> tmpTable;
						for (int j = 0; j < ROUTER_NODES; j++) {
							RouterLink rl;
							//vector<vector<RouterLink>> test = RouterWithLS::Instance().GetLSRouterTable();
							//cout << test.size() << " " << test[0].size() << endl;
							cout << (jsonBody["Router_Num"]).asInt() << endl;
							
						
								rl.localHostNum = jsonBody["Router_Info"][i][j]["Src_Num"].asInt();
								rl.dstHostNum = jsonBody["Router_Info"][i][j]["Dst_Num"].asInt();
								rl.linkCost = jsonBody["Router_Info"][i][j]["Cost"].asInt();
								rl.linkState = true;
								rl.lastModified = jsonBody["Router_Info"][i][j]["Timestamp"].asInt();
								tmpTable.push_back(rl);
							
						}
						routerTable.push_back(tmpTable);
					}
					if (RouterWithLS::Instance().ReceiveUpdatingRouterInfo(routerTable)) {
						cout << "路由更新，消息来自" << " 路由信息来自" << jsonBody["Src_Addr"] << ":" << jsonBody["Src_Port"] << endl;
					}
					else {
						cout << "路由消息不更新" << endl;
					}
				}
				else if (jsonBody["Router_Protocol"] == DV) {
					vector<ForwardingRouterInfo> routerTable;
					int fromNum = jsonBody["Src_Num"].asInt();
					for (int i = 0; i < (jsonBody["Router_Num"]).asInt(); i++) {
						ForwardingRouterInfo rl;
						rl.nextHostNum = jsonBody["Router_Info"][i]["Next_Num"].asInt();
						rl.targetedHostNum = jsonBody["Router_Info"][i]["Dst_Num"].asInt();
						rl.jumpNum = jsonBody["Router_Info"][i]["jumpNum"].asInt();
						routerTable.push_back(rl);
					}
					if (RouterWithDV::Instance().ReceiveUpdatingRouterInfo(routerTable, fromNum)) {
						cout << "路由更新，消息来自" << " 路由信息来自" << jsonBody["Src_Addr"] << ":" << jsonBody["Src_Port"] << endl;
					}
					else {
						cout << "路由消息不更新" << endl;
					}
				}
				else {
					cout << "收到未知的路由更新报文" << endl;
				}
			}
			else {
				cout << "收到其他未知报文" << endl;
			}
		}

		LeaveCriticalSection(&Section);   /*离开临界区*/
		Sleep(1);
		if (exitFlag)
			return 0;
	}
	return 0;
}


/***************************************************************
函数功能：该函数用于周期性向其他结点发送路由消息,30s发送一次
函数输入： 线程参数
函数输出： 线程退出标志
函数依赖：  Message::Instance().CreateExchangeMessageWithLS
			Message::Instance().CreateExchangeMessageWithDV
			RouterWithLS::Instance().GetDVRouterTable
			RouterWithDV::Instance().GetDVRouterTable
			SocketService::Instance().SendMessageToDst
全局变量：NumToAddrRouterMappingTable exitFlag
Author: qyh
Date created: 18/11/21
Date finished: 18/11/21
***************************************************************/
DWORD __stdcall ControlUnit::StartSendRouterInfoThread(LPVOID lpParameter)
{
	while (1) {
		Sleep(30 * 1000);   /*30秒*/
		EnterCriticalSection(&Section); /*进入临界区*/
		cout << "发送路由消息线程：每隔30s发送路由消息给其他路由结点-------------" << endl;
		m_staticSelf->SendRouterInfoToOtherRouter();       /*向其他结点发送本地路由消息*/
		LeaveCriticalSection(&Section);                    /*退出临界区*/

		if (exitFlag)
			return 0;
	}
}

/***************************************
函数功能：每隔180s用于检查那些老化的路由
函数输入：线程参数
函数输出：线程参数
函数依赖：RouterWithLS::Instance().CheckRouterTable(); RouterWithDV::Instance().CheckRouterTable();
全局变量：exitFlag
Author: qyh
Date created: 18/11/21
Date finished: 18/11/22
****************************************/
DWORD __stdcall ControlUnit::StartCheckRouterInfoThread(LPVOID lpParameter)
{
	while (1) {
		Sleep(1000 * 180);  /*暂停180s*/
		EnterCriticalSection(&Section);        /*进入临界区*/
		//cout << "更新消息线程-----------------------------------------------------------------------" << endl;
		cout << "检查路由消息线程：每隔180s检查有无老化路由" << endl;
		if (ROUTER_PROTOCOL == LS) {
			int change = RouterWithLS::Instance().CheckRouterTable();
			if (change == 1) {
				cout << "出现路由老化, 向其他结点更新路由消息" << endl;
				// 如果老化
				cout << "更新后本地路由为 :" << endl;
				RouterWithLS::Instance().DrawRouterTable();
				m_staticSelf->SendRouterInfoToOtherRouter();
			 }/*向其他结点发送本地路由消息*/
		}
		else if (ROUTER_PROTOCOL == DV) {
			RouterWithDV::Instance().CheckRouterTable();
		}

		LeaveCriticalSection(&Section);        /*退出临界区*/

		if (exitFlag)
			return 0;
	}
}

/***************************
函数功能：该函数为程序的顶层入口。开启多个线程用于，接收消息，发送消息，更新消息。。。
Author: qyh
Date created: 18/11/21
Date finished:
*****************************/
bool ControlUnit::Run()
{
	cout << "***************************************" << endl << endl;
	cout << "Welcome to the Virtual Routing World ! " << endl;
	cout << "               .                       " << endl;
	cout << "__________2018-11-31___________________" << endl << endl;

	cout << "请选择测试的路由算法： 1. DV  2.LS" << endl;
	string choice;
	while (1) {
		cin >> choice;
		if (choice == "1") {
			ROUTER_PROTOCOL = DV;
			break;
		}
		else if (choice == "2") {
			ROUTER_PROTOCOL = LS;
			break;
		}
		else {
			cout << "请输入正确的数字，1或2" << endl;
		}
	}


	cout << "请输入你的路由ID: " << endl;
	int router_Num = 0;
	while (1) {
		cin >> router_Num;
		if (router_Num < MAX_ROUTER_NODES) {
			LOCAL_HOST_NUM = router_Num;
			RECEIVE_PORT = receivePortTable[LOCAL_HOST_NUM];
			SEND_PORT = sendPortTable[LOCAL_HOST_NUM];

			break;
		}
		else {
			cout << "请输入合法的ID，1~10以内" << endl;
		}
	}

	InitRouterLinkTable();
	InitRouterMappingTable();
	cout << "--------------------" << endl;

	// 初始化
	RouterWithDV::Instance();
	RouterWithLS::Instance();

	bool Connected;
	while (1) {
		if (InitSocket(IPTable[LOCAL_HOST_NUM], SEND_PORT, IPTable[LOCAL_HOST_NUM], RECEIVE_PORT)) {
			// cout << "创建socket成功" << endl;
			Connected = true;
			break;
		}
		else {
			cout << "创建socket失败,请检查你是否联网" << endl;
			return 0;
		}
	}

	cout << "你的IP：" << string(inet_ntoa(NumToAddrReceiveRouterMappingTable[LOCAL_HOST_NUM].sin_addr)) << endl;
	cout << "接收端口： " << htons(NumToAddrReceiveRouterMappingTable[LOCAL_HOST_NUM].sin_port) << endl;
	cout << "发送端口:  " << htons(NumToAddrSendRouterMappingTable[LOCAL_HOST_NUM].sin_port) << endl;
	cout << endl << "-----------------------------------\n请输入以下序号选择对应的功能\n1. 发送报文消息到某一路由器。\n2. 显示网路拓扑图。\n3. 显示路由表。\n4.显示转发信息。\n5.关闭socket\n6.重新开启socket\n7.退出系统\n8.测试路由之间连通性" << endl;


	/*创建三个线程*/
	InitializeCriticalSection(&Section);
	receiveMessageThread = ::CreateThread(NULL, 0, StartReceivingThread, NULL, 0, NULL);
	checkRouterThread = ::CreateThread(NULL, 0, StartCheckRouterInfoThread, NULL, 0, NULL);
	sendRouterMessageThread = ::CreateThread(NULL, 0, StartSendRouterInfoThread, NULL, 0, NULL);


	while (1) {

		string choose;
		cin >> choose;

		if (choose == "1") {
			if (!Connected) {
				cout << "请确保你是否建立socket" << endl;
			}
			else {
				int routerID;
				cout << "输入你发送的目的路由器" << endl;
				cin >> routerID;
				string msg;
				cout << "输入你想要发送的内容" << endl;
				cin >> msg;
				if (SendMessageToOtherRouter(msg, routerID)) {
					//cout << "发送成功" << endl;
				}
				else {
					cout << "发送失败" << endl;
				}
			}
		}
		else if (choose == "2") {
			//	DrawRouterTable();
			PrintRouterLinkTable();
		}
		else if (choose == "3") {
			//PrintRouterLinkTable();
			if (ROUTER_PROTOCOL == DV) {
				cout << "DV 协议路由表" << endl;
				RouterWithDV::Instance().DrawRouterTable();
			}
			if (ROUTER_PROTOCOL == LS) {
				cout << "LS 协议路由表" << endl;
				RouterWithLS::Instance().DrawRouterTable();
			}
		}
		else if (choose == "4") {
			cout << "全局表" << endl;
			PrintRouterMappingTable();
			if (ROUTER_PROTOCOL == DV) {
				cout << "DV 协议转发表表" << endl;
				RouterWithDV::Instance().PrintForwardingTable();
			}
			if (ROUTER_PROTOCOL == LS) {
				cout << "LS 协议转发表" << endl;
				RouterWithLS::Instance().PrintForwardingTable();
			}
		}
		else if (choose == "5") {
			ExitSocket();
			Connected = false;
			exitFlag = 1;
		}
		else if (choose == "6") {
			if (InitSocket(IPTable[LOCAL_HOST_NUM], SEND_PORT, IPTable[LOCAL_HOST_NUM], RECEIVE_PORT)) {
				cout << "创建socket成功" << endl;
				Connected = true;
			}
			else {
				cout << "创建socket失败,程序退出" << endl;
				return 0;
			}
		}
		else if (choose == "7") {
			exit(0);
		}
		else if (choose == "8") {
			int routerID;
			cout << "输入你发送的目的路由器" << endl;
			cin >> routerID;
			string msg = "TEST";
			string _IP = string(inet_ntoa(NumToAddrReceiveRouterMappingTable[routerID].sin_addr));
			int _Port = htons(NumToAddrReceiveRouterMappingTable[routerID].sin_port);
			if (SocketService::Instance().SendMessageToSrc(msg, _IP, _Port)) {
				cout << "消息已发送成功! 目的IP为 " << _IP << "  端口为:" << _Port << endl;
				cout << "发送成功" << endl;
			}
			else {
				cout << "发送失败" << endl;
			}
		}
		else {
			cout << "请输入正确的序号" << endl;
		}
		cout << endl << "-----------------------------------\n请输入以下序号选择对应的功能\n1. 发送报文消息到某一路由器。\n2. 显示网路拓扑图。\n3. 显示路由表。\n4.显示转发信息。\n5.关闭socket\n6.重新开启socket\n7.退出系统\n8.测试路由之间连通性" << endl;

	}
	/*关闭子进程*/
	::CloseHandle(receiveMessageThread);
	::CloseHandle(checkRouterThread);
	::CloseHandle(sendRouterMessageThread);

	DeleteCriticalSection(&Section);  /*删除临界区对象*/
	cout << "退出程序" << endl;
	return 0;
}

