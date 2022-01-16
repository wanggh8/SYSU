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

Description: �ܿس������ã�����ͨ�ţ�Э��������Լ�·��ѡ��

****************************************************/


class ControlUnit {

private:
	static ControlUnit* m_staticSelf;   /*�����ھ�̬�����е��÷Ǿ�̬����*/

	ControlUnit() {
		m_staticSelf = this;
		InitializeCriticalSection(&Section);
	};                              // ctor hidden
	//ControlUnit(ControlUnit const&);            // copy ctor hidden
	//ControlUnit& operator=(ControlUnit const&); // assign op. hidden
	~ControlUnit() =default;                             // dtor hidden

	HANDLE receiveMessageThread;               /*���߳̽�������������Ϣ��ͨ���������ݾ�����ת����Ϣ���Ǹ���·����Ϣ*/
	HANDLE checkRouterThread;                  /*���߳���Ҫ���ڼ�������ϻ�·����Ϣ*/
	HANDLE sendRouterMessageThread;            /*���߳��������������ڽӽ�㷢��·�ɸ�����Ϣ*/

	static int exitFlag;                         /*�˳�����ı�־����ֹ�̵߳ı�־*/
	
	static CRITICAL_SECTION Section;                  /*�����ٽ�������*/

	int routerProtocol;                /*OSPF  or  RIP*/
public:
	static ControlUnit& Instance() {
		static ControlUnit theControlUnit;
		return theControlUnit;
	}

	/*���绷����ʼ��*/
	bool InitSocket(string sendIP, int sendPort, string receiveIP, int receivePort);

	/*�ر�socket*/
	bool ExitSocket();

	/*��ĳĿ������������Ϣ����*/
	bool SendMessageToOtherRouter(string msg, int dstRouterID);

	/*��������㷢�ͱ��ڵ�·����Ϣ, ���͵�����·���ϵ�ʱ����������*/
	bool SendRouterInfoToOtherRouter();

	/*�����̣߳��ȴ�������Ϣ(�Զ�ִ�У�*/
	static DWORD WINAPI StartReceivingThread(LPVOID lpParameter);

	/*����һ���̣߳������Եط���·����Ϣ���������ڽ��(�Զ�ִ�У�*/
	static DWORD WINAPI StartSendRouterInfoThread(LPVOID lpParameter);

	/*����ϻ�·�ɣ������Զ�ִ��*/
	static DWORD WINAPI StartCheckRouterInfoThread(LPVOID lpParameter);

	/*�ܿغ�������ʼ���п��Ƶ�Ԫ*/
	bool Run();

};


