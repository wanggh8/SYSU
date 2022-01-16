#pragma once
#include "RouterWithDV.h"
#include "RouterWithLS.h"
#include "utils.h"
/**********************************************************8*

Author: qyh

Date created : 18/11/18

Date finished:

Description: 通信服务类，初始化socket和关闭，接收和发送报文

**************************************************************/

class SocketService {

private:
	SocketService() {
	
	}	// ctor hidden
	//SocketService(SocketService const&);            // copy ctor hidden
	//SocketService& operator=(SocketService const&); // assign op. hidden
	~SocketService() = default;                           // dtor hidden

	SOCKET sendSocket = 0;
	SOCKET receiveSocket = 1;

public:
	static SocketService& Instance() {
		static SocketService theSocketService;
		return theSocketService;
	}

	/*打印通信错误信息*/
	void ErrMsg(DWORD dwErr);
	/*完成初始的网络配置*/
	bool InitSocket(string sendIP, int sendPort, string receiveIP, int receivePort);

	/*结束连接*/
	bool ExitSocket();

	/*发送报文到目的地*/
	bool SendMessageToDst(char* msg, int dstRouterID);

	/*接收报文到目的地, 接收消息报文并返回*/
	char* ReceiveMessageFromSrc(char* msg);

	/*发送路由信息返回给源地址*/
	bool SendMessageToSrc(string msg, string srcIP, int srcPort);


};

