#pragma once
#include "utils.h"
#include "RouterWithDV.h"
#include "RouterWithLS.h"
#include "Message.h"
#include "SocketService.h"
/*****************************************************

Author: qyh

Date created : 18/11/19

Date finished:

Description: 总控程序，作用：控制通信，协议解析，以及路由选择

****************************************************/


class ControlUnit {

private:
	static ControlUnit* m_staticSelf;   /*用于在静态函数中调用非静态函数*/

	ControlUnit() {
		m_staticSelf = this;
		InitializeCriticalSection(&Section);
	};                              // ctor hidden
	//ControlUnit(ControlUnit const&);            // copy ctor hidden
	//ControlUnit& operator=(ControlUnit const&); // assign op. hidden
	~ControlUnit() =default;                             // dtor hidden

	HANDLE receiveMessageThread;               /*该线程接收其他结点的消息，通过报文内容决定是转发消息还是更新路由消息*/
	HANDLE checkRouterThread;                  /*该线程主要用于检测有无老化路由信息*/
	HANDLE sendRouterMessageThread;            /*该线程用于周期性向邻接结点发送路由更新消息*/

	static int exitFlag;                         /*退出程序的标志，终止线程的标志*/
	
	static CRITICAL_SECTION Section;                  /*定义临界区对象*/

	int routerProtocol;                /*OSPF  or  RIP*/
public:
	static ControlUnit& Instance() {
		static ControlUnit theControlUnit;
		return theControlUnit;
	}

	/*网络环境初始化*/
	bool InitSocket(string sendIP, int sendPort, string receiveIP, int receivePort);

	/*关闭socket*/
	bool ExitSocket();

	/*向某目的主机发送消息报文*/
	bool SendMessageToOtherRouter(string msg, int dstRouterID);

	/*向其他结点发送本节点路由信息, 发送单点链路故障的时候，主动更新*/
	bool SendRouterInfoToOtherRouter();

	/*开启线程，等待接收消息(自动执行）*/
	static DWORD WINAPI StartReceivingThread(LPVOID lpParameter);

	/*开启一个线程，周期性地发送路由消息给其他相邻结点(自动执行）*/
	static DWORD WINAPI StartSendRouterInfoThread(LPVOID lpParameter);

	/*检查老化路由（周期自动执行*/
	static DWORD WINAPI StartCheckRouterInfoThread(LPVOID lpParameter);

	/*总控函数，开始运行控制单元*/
	bool Run();

};


