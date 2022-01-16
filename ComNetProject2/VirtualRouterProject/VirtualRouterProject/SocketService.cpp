#pragma once
#include "SocketService.h"
#include "RouterWithDV.h"
#include "RouterWithLS.h"
#include "utils.h"


/*��ӡͨ�Ŵ�����Ϣ*/
void SocketService::ErrMsg(DWORD dwErr) {
	char szErr[1024] = { 0 };
	FormatMessage(FORMAT_MESSAGE_FROM_SYSTEM, NULL, dwErr,
		MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT), szErr, 1024, NULL);
	cout << szErr << endl;
}

/********************************************
�������ܣ� ��ʼ������socket�ͽ���socket
�������룺 ���� IP Port  ����IP Port.
��������� ���������ʼ���ɹ�
Author:    qiuyihao
Date created:  18/11/20
Date finished: 18/11/20
**********************************************/
bool SocketService::InitSocket(string sendIP, int sendPort, string receiveIP, int receivePort)
{
	/* ��ʼ������**************************************8*/
	WSADATA wd1 = { 0 };
	int nStart1 = WSAStartup(MAKEWORD(SOCK_VER, 0), &wd1);
	if (nStart1 != 0) {
		cout << "��ʼ��socket����" << endl;
		return 0;
	}

	/*��������socket***********************************************/
	 sendSocket = socket(AF_INET, SOCK_DGRAM, IPPROTO_UDP);
	if (sendSocket == INVALID_SOCKET) {
		ErrMsg(WSAGetLastError());
		return 0;
	}
	cout << "����socket�ɹ�" << endl;


	/***************����socket�󶨵��˿� ****************/
	sockaddr_in addr1 = { 0 };
	addr1.sin_family = AF_INET;
	addr1.sin_port = htons(sendPort);
	addr1.sin_addr.S_un.S_addr = inet_addr(sendIP.c_str());
	int nBind1 = bind(sendSocket, (sockaddr *)&addr1, sizeof(addr1));

	if (nBind1 != 0) {
		cout << "�󶨷���IP�˿ڳ���" << endl;
		return 0;
	}

	/* ȡ�÷���socket�Ѱ󶨵Ķ˿ں�****************************/
	int nLen1 = sizeof(addr1);
	getsockname(sendSocket, (sockaddr *)& addr1, &nLen1);
	cout << "����Socket �ɹ��󶨵��˿�" << ntohs(addr1.sin_port) << "" << endl;


	/* ��ʼ������**************************************8*/
	WSADATA wd2 = { 0 };
	int nStart2 = WSAStartup(MAKEWORD(SOCK_VER, 0), &wd2);
	if (nStart2 != 0) {
		cout << "��ʼ��socket����" << endl;
		return 0;
	}

	/*����receive socket***********************************************/
	receiveSocket = socket(AF_INET, SOCK_DGRAM, IPPROTO_UDP);
	if (receiveSocket == INVALID_SOCKET) {
		ErrMsg(WSAGetLastError());
		return 0;
	}
	cout << "��������socket�ɹ�" << endl;


	/***************����socket�󶨵��˿� ****************/
	sockaddr_in addr2 = { 0 };
	addr2.sin_family = AF_INET;
	addr2.sin_port = htons(receivePort);
	addr2.sin_addr.S_un.S_addr = inet_addr(receiveIP.c_str());
	int nBind2 = bind(receiveSocket, (sockaddr *)&addr2, sizeof(addr2));

	if (nBind2 != 0) {
		cout << "�󶨽���IP�˿ڳ���" << endl;
		return 0;
	}

	/* ȡ�÷���socket�Ѱ󶨵Ķ˿ں�****************************/
	int nLen2 = sizeof(addr2);
	getsockname(receiveSocket, (sockaddr *)& addr2, &nLen2);
	cout << "����Socket �ɹ��󶨵��˿�" << ntohs(addr2.sin_port) << "" << endl;


	/*����recvfrom��ʱʱ��5�룬����5�뷵��*************************/
	
	struct timeval tv_out;
	tv_out.tv_sec = 1;   //����״̬
	tv_out.tv_usec = 30;
	setsockopt(sendSocket, SOL_SOCKET, SO_RCVTIMEO, (char*)&tv_out, sizeof(tv_out));
	setsockopt(receiveSocket, SOL_SOCKET, SO_RCVTIMEO, (char*)&tv_out, sizeof(tv_out));
	
	return 1;
}



/********************************************************************
�������ܣ� �ر�socket
��������� ��������رճɹ�
Author:    qiuyihao
Date created:  18/11/20
Date finished: 18/11/20
********************************************************************/
bool SocketService::ExitSocket()
{
	try
	{
		closesocket(sendSocket);
		closesocket(receiveSocket);
		WSACleanup();
		return true;
	}
	catch (const std::exception& e)
	{
		cout << "�ر�socketʱ����" << endl;
		return false;
	}
}

/***************************************************************
�������ܣ� �����յ�����ID�����ת����ȷ����һ����������Ϣ����
�������룺 Msg �ṹ������ָ��    dstRouterID ����Ŀ�ĵ�
��������� ���������ͳɹ�
ȫ�ֱ����� NumToAddrRouterMappingTable
Author:    qiuyihao
Date created:  18/11/20
Date finished: 18/11/20
**************************************************************/
bool SocketService::SendMessageToDst(char * msg, int dstRouterID)
{
	int nextRouterID = 0;
	if (ROUTER_PROTOCOL == DV) {
		if (!RouterWithDV::Instance().CheckLinkState(dstRouterID))
			return false;
		nextRouterID = RouterWithDV::Instance().DecideForwardingAddress(dstRouterID);
	}
	else if (ROUTER_PROTOCOL == LS) {
		if (!RouterWithLS::Instance().CheckLinkState(dstRouterID))
			return false;
		nextRouterID = RouterWithLS::Instance().DecideForwardingAddress(dstRouterID);
		
	}
	
	sockaddr_in addr = NumToAddrReceiveRouterMappingTable[nextRouterID];

	int nSent = sendto(sendSocket, msg, strlen(msg) + 1, 0, (sockaddr*)&addr, sizeof(addr));
	//cout << nSent<< "��Ϣ�ѷ���! ---- ·��ID�� " << dstRouterID << " ,Ŀ��IPΪ: " << string(inet_ntoa(addr.sin_addr)) <<":" << htons(addr.sin_port) << endl;
	if (nSent == -1) {
		ErrMsg(WSAGetLastError());
		return false;
	}

	return true;
}

/***************************************************************
�������ܣ��������������Ϣ�������õ�һ�ݱ���
������������ؽ��յı���
Author: qiuyihao
Date created: 18/11/20
Date finished: 18/11/20
*****************************************************************/
char * SocketService::ReceiveMessageFromSrc(char* msg)
{
	struct sockaddr_in otherRouterAddr = { 0 };
	int nFromLen = sizeof(otherRouterAddr);
	int nRecv = recvfrom(receiveSocket,msg, BUFF_SIZE, 0, (struct sockaddr*)& otherRouterAddr, &nFromLen);
	
	if (nRecv == SOCKET_ERROR) {
		//cout << "�Ӱ���ʱ" << endl;
		return msg;
	}

	if (string(msg) == "TEST") {
		this->SendMessageToSrc("ACK", string(inet_ntoa(otherRouterAddr.sin_addr)), htons(otherRouterAddr.sin_port) - 110);
	}
	return msg;
}

/*����·����Ϣ���ظ�Դ��ַ*/
bool SocketService::SendMessageToSrc(string msg, string srcIP, int srcPort) {
	sockaddr_in addr = { 0 };
	addr.sin_family = AF_INET;
	addr.sin_port = htons(srcPort);
	addr.sin_addr.S_un.S_addr = inet_addr(srcIP.c_str());
	char _msg[BUFF_SIZE] = { 0 };
	strcpy_s(_msg, msg.c_str());
	
	int Sent = sendto(sendSocket, _msg, strlen(_msg) + 1, 0, (sockaddr*)&addr, sizeof(addr));
	
	if (Sent == 0) {
		ErrMsg(WSAGetLastError());
		return false;
	}
	return true;
}
