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

/*��ʼ��socket*/
bool ControlUnit::InitSocket(string sendIP, int sendPort, string receiveIP, int receivePort)
{
	routerProtocol = ROUTER_PROTOCOL;
	if (SocketService::Instance().InitSocket(sendIP, sendPort, receiveIP, receivePort)) {
		cout << "ip" << sendIP << endl;
		cout << "��ʼ��socket�����ɹ�,���Է��ͺͽ�����Ϣ��~~~" << endl;
		cout << "***********************************************************" << endl << endl;
		return true;
	}
	else {
		cout << "socket������ʼ��ʧ��,�����¼��������������~~" << endl;
		cout << "***********************************************************" << endl << endl;
		return false;
	}
}

/*�ر�socket*/
bool ControlUnit::ExitSocket()
{
	exitFlag = 1;
	if (SocketService::Instance().ExitSocket()) {
		cout << "�ر�socket�ɹ�~" << endl;
		return true;
	}
	else {
		cout << "�ر�socketʧ��" << endl;
		return false;
	}
	return false;
}

/*����������Ϣ����*/
bool ControlUnit::SendMessageToOtherRouter(string msg, int dstRouterID)
{
	int nextRouterID = 0;
	if (ROUTER_PROTOCOL == LS)
		nextRouterID = RouterWithLS::Instance().DecideForwardingAddress(dstRouterID);
	else if (ROUTER_PROTOCOL == DV)
		nextRouterID = RouterWithDV::Instance().DecideForwardingAddress(dstRouterID);

	char*  protoMsg = Message::Instance().CreateMessage(msg, LOCAL_HOST_NUM, dstRouterID, nextRouterID);
	cout << "�������͵ı�������---------" << endl;
	cout << protoMsg << endl;
	cout << "------------------------------" << endl;
	if (SocketService::Instance().SendMessageToDst(protoMsg, dstRouterID)) {
		cout << "���ķ��ͳɹ�" << endl;
		return true;
	}
	else {
		cout << "����ʧ�ܣ���������״̬" << endl;
		return false;
	}
}

/***************************************************************
�������ܣ��ú���������������㷢��·����Ϣ
�������룺 �̲߳���
��������� �߳��˳���־
����������  Message::Instance().CreateExchangeMessageWithLS
			Message::Instance().CreateExchangeMessageWithDV
			RouterWithLS::Instance().GetDVRouterTable
			RouterWithDV::Instance().GetDVRouterTable
			SocketService::Instance().SendMessageToDst
ȫ�ֱ�����  NumToAddrRouterMappingTable
Author: qyh
Date created: 18/11/21
Date finished: 18/11/21
***************************************************************/
bool ControlUnit::SendRouterInfoToOtherRouter()
{
	/*����Э�飬׼����������㷢���Լ���·����Ϣ*/
	if (ROUTER_PROTOCOL == LS) {
		for (int i = 0; i < RouterWithLS::Instance().GetLSRouterTable().size(); i++) {
			if (i == LOCAL_HOST_NUM)
				continue;

			if (RouterWithLS::Instance().GetLSRouterTable().at(LOCAL_HOST_NUM).at(i).linkCost == INFINITY) {
				continue;
			}
			/*���ɱ���*/
			string msg = Message::Instance().CreateLSMessage(&RouterWithLS::Instance().GetLSRouterTable(), i);
			//cout << msg << endl;
			//cout << i << endl;
			if (SocketService::Instance().SendMessageToDst((char*)msg.c_str(), i)) {
				//	cout << "����·����Ϣ���������" << endl;
				return true;
			}
			else {
				cout << "����·����Ϣ����" << endl;
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
			/*���ɱ���*/
			Message::Instance().CreateDVMessage(&RouterWithDV::Instance().GetDVForwardingTable(), dstRouterID);
			if (SocketService::Instance().SendMessageToDst(msg, i)) {
				cout << "����·����Ϣ���������" << endl;
				return true;
			}
			else {
				cout << "����·����Ϣ����" << endl;
				return false;
			}
		}
	}
	return true;
}


/*******************************************************************
�������ܣ��̺߳��������ڽ�����Ϣ���ж���Ϣ��Ӧ��ת�����Ǹ���·����Ϣ
�������룺�̱߳ر��Ĳ�����ʵ��δʹ��
�������������0
����������RouterWithLS::Instance().GetDVRouterTable()
		  RouterWithLS::Instance().DecideForwardingAddress
		  RouterWithDV::Instance().GetDVRouterTable
		  RouterWithDV::Instance().ReceiveUpdatingRouterInfo
ȫ�ֱ�����NumToAddrRouterMappingTable  exitFlag
Author: qyh
Date created: 18/11/20
Date finished: 18/11/21
*********************************************************************/
DWORD __stdcall ControlUnit::StartReceivingThread(LPVOID lpParameter)
{

	while (1) {
		//cout << "������Ϣ�߳�-----" << endl;

		EnterCriticalSection(&Section);    /*�����ٽ���*/

		char msg[BUFF_SIZE] = { 0 };            /*�ȴ�����*/

		SocketService::Instance().ReceiveMessageFromSrc(msg);  /*�ȴ������ձ��ģ����������msg��*/

		if (strlen(msg) == 0) {

		}
		else if (strlen(msg) < 100) {
			Sleep(20);
			cout << "������Ϣ�̣߳�������Ϣ��----------" << endl;
			cout << msg << endl;   // �Ǳ�����Ϣ���˴���ӡ������Ϣ
		}
		else {
			cout << "������Ϣ�̣߳����ձ�����Ϣ��----------" << endl;

			Json::Value jsonBody = Message::Instance().DecodeMessage(msg, jsonBody);
			Message::Instance().Print(jsonBody);

			if (jsonBody["Message_Type"] == FORWARDING_MESSAGE) {     /*���յ�ת�����ģ���һ����Ҫת����һ��*/
				if (jsonBody["Router_Protocol"] == LS) {
					cout << "�յ�LSЭ����Ϣ���ġ���Ϣ����" << jsonBody["From_Addr"] << endl; /**************************/
					//cout << jsonBody["Dst_Addr"] << string(inet_ntoa(NumToAddrReceiveRouterMappingTable[LOCAL_HOST_NUM].sin_addr)) << endl;
					if (jsonBody["Dst_Port"].asInt() == htons(NumToAddrReceiveRouterMappingTable[LOCAL_HOST_NUM].sin_port)
						&& jsonBody["Dst_Addr"] == string(inet_ntoa(NumToAddrReceiveRouterMappingTable[LOCAL_HOST_NUM].sin_addr))) {
						cout << "��Ϣ�����ǣ� " << jsonBody["Msg"];
						string returnToSrc = "The message arrived the destination. from " + IPTable[LOCAL_HOST_NUM];
						int router_id = jsonBody["Src_Num"].asInt();
						int router_receive_port = htons(NumToAddrReceiveRouterMappingTable[router_id].sin_port);
						SocketService::Instance().SendMessageToSrc(returnToSrc, jsonBody["Src_Addr"].asString(), router_receive_port);
						RouterWithLS::Instance().UpdateTimestamp(jsonBody["Src_Num"].asInt());
						RouterWithLS::Instance().UpdateTimestamp(jsonBody["From_Num"].asInt());

					}
					else {
						cout << "��ʼת���ñ���" << endl;
						jsonBody["From_Addr"] = string(inet_ntoa(NumToAddrSendRouterMappingTable[LOCAL_HOST_NUM].sin_addr));
						jsonBody["From_Port"] = NumToAddrSendRouterMappingTable[LOCAL_HOST_NUM].sin_port;
						jsonBody["From_Num"] = LOCAL_HOST_NUM;
						/*��ѯת����,�õ���һ�� IP PORT NUM*/
						jsonBody["Next_Addr"] = string(inet_ntoa(NumToAddrReceiveRouterMappingTable[RouterWithLS::Instance().DecideForwardingAddress(jsonBody["Dst_Num"].asInt())].sin_addr));
						jsonBody["Next_Port"] = NumToAddrReceiveRouterMappingTable[RouterWithLS::Instance().DecideForwardingAddress(jsonBody["Dst_Num"].asInt())].sin_port;
						jsonBody["Next_Num"] = RouterWithLS::Instance().DecideForwardingAddress(jsonBody["Dst_Num"].asInt());
						jsonBody["Cost"] = jsonBody["Cost"].asInt() + routerLinkTable[LOCAL_HOST_NUM][RouterWithLS::Instance().DecideForwardingAddress(jsonBody["Dst_Num"].asInt())];
						/*���ͱ���*/
						char _msg[BUFF_SIZE] = { 0 };

						string s = Message::Instance().EncodeMessage(&jsonBody, _msg);

						string ip = IPTable[LOCAL_HOST_NUM];
						RouterWithLS::Instance().UpdateTimestamp(jsonBody["Src_Num"].asInt());
						RouterWithLS::Instance().UpdateTimestamp(jsonBody["From_Num"].asInt());

						if (SocketService::Instance().SendMessageToDst((char*)s.c_str(), jsonBody["Dst_Num"].asInt())) {
							cout << "ת����Ϣ���ĳɹ�" << endl;
							Message::Instance().Print(jsonBody);
							cout << "ת��������������" << endl;
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
							cout << "ת����Ϣ����ʧ��" << endl;
						}
					}

				}
				else if (jsonBody["Router_Protocol"] == DV) {
					cout << "DV" << endl;
					string ip = IPTable[LOCAL_HOST_NUM];
					cout << "�յ�DVЭ����Ϣ���ġ���Ϣ����" << jsonBody["From_Addr"] << ":" << jsonBody["From_Port"] << endl;
					if (jsonBody["Dst_Addr"] == string(inet_ntoa(NumToAddrReceiveRouterMappingTable[LOCAL_HOST_NUM].sin_addr))) {
						cout << "�յ�DVЭ����Ϣ���ġ���Ϣ����" << jsonBody["Src_Addr"] << ":" << jsonBody["Src_Port"] << endl;
						cout << "��Ϣ�����ǣ� " << jsonBody["Msg"] << endl;
						string returnToSrc = "The message arrived the destination. from " + string(ip);
						int router_id = jsonBody["Src_Num"].asInt();
						int router_receive_port = htons(NumToAddrReceiveRouterMappingTable[router_id].sin_port);
						SocketService::Instance().SendMessageToSrc(returnToSrc, jsonBody["Src_Addr"].asString(), router_receive_port);
					}
					else {
						cout << "��ʼת���ñ���" << endl;
						jsonBody["From_Addr"] = string(inet_ntoa(NumToAddrSendRouterMappingTable[LOCAL_HOST_NUM].sin_addr));
						jsonBody["From_Port"] = NumToAddrSendRouterMappingTable[LOCAL_HOST_NUM].sin_port;
						jsonBody["From_Num"] = LOCAL_HOST_NUM;
						/*��ѯת����,�õ���һ�� IP PORT*/
						jsonBody["Next_Addr"] = string(inet_ntoa(NumToAddrReceiveRouterMappingTable[RouterWithDV::Instance().DecideForwardingAddress(jsonBody["Dst_Num"].asInt())].sin_addr));
						jsonBody["Next_Port"] = NumToAddrReceiveRouterMappingTable[RouterWithDV::Instance().DecideForwardingAddress(jsonBody["Dst_Num"].asInt())].sin_port;
						jsonBody["Cost"] = jsonBody["Cost"].asInt() + routerLinkTable[LOCAL_HOST_NUM][RouterWithDV::Instance().DecideForwardingAddress(jsonBody["Dst_Num"].asInt())];
						/*���ͱ���*/
						char _msg[BUFF_SIZE] = { 0 };
						cout << "...." << endl;
						string s = Message::Instance().EncodeMessage(&jsonBody, _msg);
						strcpy_s(_msg, s.length(), s.c_str());

						//cout << Message::Instance().EncodeMessage(&jsonBody, _msg) << endl;
						if (SocketService::Instance().SendMessageToDst((char*)s.c_str(), jsonBody["Dst_Num"].asInt())) {
							cout << "ת����Ϣ���ĳɹ�" << endl;
							string returnToSrc = "The message arrived the middle route.Forwarded sucessfully. From " + string(ip);
							int router_id = jsonBody["Src_Num"].asInt();
							int router_receive_port = htons(NumToAddrReceiveRouterMappingTable[router_id].sin_port);
							SocketService::Instance().SendMessageToSrc(returnToSrc, jsonBody["Src_Addr"].asString(), router_receive_port);
						}
						else {
							cout << "ת����Ϣ����ʧ��" << endl;
							int router_id = jsonBody["Src_Num"].asInt();
							int router_receive_port = htons(NumToAddrReceiveRouterMappingTable[router_id].sin_port);
							string returnToSrc = "The message arrived the middle route.Forwarded unsucessfully. From " + string(ip);
							SocketService::Instance().SendMessageToSrc(returnToSrc, jsonBody["Src_Addr"].asString(), router_receive_port);
						}
					}
				}
				else {
					cout << "�յ�δ֪����Э����Ϣ���ġ���Ϣ����" << jsonBody["From_Addr"] << ":" << jsonBody["From_Port"] << endl;

				}
			}
			else if (jsonBody["Message_Type"] == UPDATE_MESSAGE) { /*���յ�·�ɸ��±���*/

				// ���Ļ���
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
						cout << "·�ɸ��£���Ϣ����" << " ·����Ϣ����" << jsonBody["Src_Addr"] << ":" << jsonBody["Src_Port"] << endl;
					}
					else {
						cout << "·����Ϣ������" << endl;
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
						cout << "·�ɸ��£���Ϣ����" << " ·����Ϣ����" << jsonBody["Src_Addr"] << ":" << jsonBody["Src_Port"] << endl;
					}
					else {
						cout << "·����Ϣ������" << endl;
					}
				}
				else {
					cout << "�յ�δ֪��·�ɸ��±���" << endl;
				}
			}
			else {
				cout << "�յ�����δ֪����" << endl;
			}
		}

		LeaveCriticalSection(&Section);   /*�뿪�ٽ���*/
		Sleep(1);
		if (exitFlag)
			return 0;
	}
	return 0;
}


/***************************************************************
�������ܣ��ú���������������������㷢��·����Ϣ,30s����һ��
�������룺 �̲߳���
��������� �߳��˳���־
����������  Message::Instance().CreateExchangeMessageWithLS
			Message::Instance().CreateExchangeMessageWithDV
			RouterWithLS::Instance().GetDVRouterTable
			RouterWithDV::Instance().GetDVRouterTable
			SocketService::Instance().SendMessageToDst
ȫ�ֱ�����NumToAddrRouterMappingTable exitFlag
Author: qyh
Date created: 18/11/21
Date finished: 18/11/21
***************************************************************/
DWORD __stdcall ControlUnit::StartSendRouterInfoThread(LPVOID lpParameter)
{
	while (1) {
		Sleep(30 * 1000);   /*30��*/
		EnterCriticalSection(&Section); /*�����ٽ���*/
		cout << "����·����Ϣ�̣߳�ÿ��30s����·����Ϣ������·�ɽ��-------------" << endl;
		m_staticSelf->SendRouterInfoToOtherRouter();       /*��������㷢�ͱ���·����Ϣ*/
		LeaveCriticalSection(&Section);                    /*�˳��ٽ���*/

		if (exitFlag)
			return 0;
	}
}

/***************************************
�������ܣ�ÿ��180s���ڼ����Щ�ϻ���·��
�������룺�̲߳���
����������̲߳���
����������RouterWithLS::Instance().CheckRouterTable(); RouterWithDV::Instance().CheckRouterTable();
ȫ�ֱ�����exitFlag
Author: qyh
Date created: 18/11/21
Date finished: 18/11/22
****************************************/
DWORD __stdcall ControlUnit::StartCheckRouterInfoThread(LPVOID lpParameter)
{
	while (1) {
		Sleep(1000 * 180);  /*��ͣ180s*/
		EnterCriticalSection(&Section);        /*�����ٽ���*/
		//cout << "������Ϣ�߳�-----------------------------------------------------------------------" << endl;
		cout << "���·����Ϣ�̣߳�ÿ��180s��������ϻ�·��" << endl;
		if (ROUTER_PROTOCOL == LS) {
			int change = RouterWithLS::Instance().CheckRouterTable();
			if (change == 1) {
				cout << "����·���ϻ�, ������������·����Ϣ" << endl;
				// ����ϻ�
				cout << "���º󱾵�·��Ϊ :" << endl;
				RouterWithLS::Instance().DrawRouterTable();
				m_staticSelf->SendRouterInfoToOtherRouter();
			 }/*��������㷢�ͱ���·����Ϣ*/
		}
		else if (ROUTER_PROTOCOL == DV) {
			RouterWithDV::Instance().CheckRouterTable();
		}

		LeaveCriticalSection(&Section);        /*�˳��ٽ���*/

		if (exitFlag)
			return 0;
	}
}

/***************************
�������ܣ��ú���Ϊ����Ķ�����ڡ���������߳����ڣ�������Ϣ��������Ϣ��������Ϣ������
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

	cout << "��ѡ����Ե�·���㷨�� 1. DV  2.LS" << endl;
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
			cout << "��������ȷ�����֣�1��2" << endl;
		}
	}


	cout << "���������·��ID: " << endl;
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
			cout << "������Ϸ���ID��1~10����" << endl;
		}
	}

	InitRouterLinkTable();
	InitRouterMappingTable();
	cout << "--------------------" << endl;

	// ��ʼ��
	RouterWithDV::Instance();
	RouterWithLS::Instance();

	bool Connected;
	while (1) {
		if (InitSocket(IPTable[LOCAL_HOST_NUM], SEND_PORT, IPTable[LOCAL_HOST_NUM], RECEIVE_PORT)) {
			// cout << "����socket�ɹ�" << endl;
			Connected = true;
			break;
		}
		else {
			cout << "����socketʧ��,�������Ƿ�����" << endl;
			return 0;
		}
	}

	cout << "���IP��" << string(inet_ntoa(NumToAddrReceiveRouterMappingTable[LOCAL_HOST_NUM].sin_addr)) << endl;
	cout << "���ն˿ڣ� " << htons(NumToAddrReceiveRouterMappingTable[LOCAL_HOST_NUM].sin_port) << endl;
	cout << "���Ͷ˿�:  " << htons(NumToAddrSendRouterMappingTable[LOCAL_HOST_NUM].sin_port) << endl;
	cout << endl << "-----------------------------------\n�������������ѡ���Ӧ�Ĺ���\n1. ���ͱ�����Ϣ��ĳһ·������\n2. ��ʾ��·����ͼ��\n3. ��ʾ·�ɱ�\n4.��ʾת����Ϣ��\n5.�ر�socket\n6.���¿���socket\n7.�˳�ϵͳ\n8.����·��֮����ͨ��" << endl;


	/*���������߳�*/
	InitializeCriticalSection(&Section);
	receiveMessageThread = ::CreateThread(NULL, 0, StartReceivingThread, NULL, 0, NULL);
	checkRouterThread = ::CreateThread(NULL, 0, StartCheckRouterInfoThread, NULL, 0, NULL);
	sendRouterMessageThread = ::CreateThread(NULL, 0, StartSendRouterInfoThread, NULL, 0, NULL);


	while (1) {

		string choose;
		cin >> choose;

		if (choose == "1") {
			if (!Connected) {
				cout << "��ȷ�����Ƿ���socket" << endl;
			}
			else {
				int routerID;
				cout << "�����㷢�͵�Ŀ��·����" << endl;
				cin >> routerID;
				string msg;
				cout << "��������Ҫ���͵�����" << endl;
				cin >> msg;
				if (SendMessageToOtherRouter(msg, routerID)) {
					//cout << "���ͳɹ�" << endl;
				}
				else {
					cout << "����ʧ��" << endl;
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
				cout << "DV Э��·�ɱ�" << endl;
				RouterWithDV::Instance().DrawRouterTable();
			}
			if (ROUTER_PROTOCOL == LS) {
				cout << "LS Э��·�ɱ�" << endl;
				RouterWithLS::Instance().DrawRouterTable();
			}
		}
		else if (choose == "4") {
			cout << "ȫ�ֱ�" << endl;
			PrintRouterMappingTable();
			if (ROUTER_PROTOCOL == DV) {
				cout << "DV Э��ת�����" << endl;
				RouterWithDV::Instance().PrintForwardingTable();
			}
			if (ROUTER_PROTOCOL == LS) {
				cout << "LS Э��ת����" << endl;
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
				cout << "����socket�ɹ�" << endl;
				Connected = true;
			}
			else {
				cout << "����socketʧ��,�����˳�" << endl;
				return 0;
			}
		}
		else if (choose == "7") {
			exit(0);
		}
		else if (choose == "8") {
			int routerID;
			cout << "�����㷢�͵�Ŀ��·����" << endl;
			cin >> routerID;
			string msg = "TEST";
			string _IP = string(inet_ntoa(NumToAddrReceiveRouterMappingTable[routerID].sin_addr));
			int _Port = htons(NumToAddrReceiveRouterMappingTable[routerID].sin_port);
			if (SocketService::Instance().SendMessageToSrc(msg, _IP, _Port)) {
				cout << "��Ϣ�ѷ��ͳɹ�! Ŀ��IPΪ " << _IP << "  �˿�Ϊ:" << _Port << endl;
				cout << "���ͳɹ�" << endl;
			}
			else {
				cout << "����ʧ��" << endl;
			}
		}
		else {
			cout << "��������ȷ�����" << endl;
		}
		cout << endl << "-----------------------------------\n�������������ѡ���Ӧ�Ĺ���\n1. ���ͱ�����Ϣ��ĳһ·������\n2. ��ʾ��·����ͼ��\n3. ��ʾ·�ɱ�\n4.��ʾת����Ϣ��\n5.�ر�socket\n6.���¿���socket\n7.�˳�ϵͳ\n8.����·��֮����ͨ��" << endl;

	}
	/*�ر��ӽ���*/
	::CloseHandle(receiveMessageThread);
	::CloseHandle(checkRouterThread);
	::CloseHandle(sendRouterMessageThread);

	DeleteCriticalSection(&Section);  /*ɾ���ٽ�������*/
	cout << "�˳�����" << endl;
	return 0;
}

