#pragma once
/*****************************************************

Author: qiuyihao

Date created: 18/11/18

Date finished: 

Description�����һЩȫ�ֳ�����������ͨ�õĺ���

*******************************************************/
#ifndef  UTILS_H
#define UTILS_H

#include <stdio.h>
#include <winsock.h>  // �׽�������ͷ�ļ�
#pragma comment(lib, "WSOCK32.LIB")
#include <iostream>
#include <stdlib.h>
#include <vector>
#include <map>
#include <time.h>
#include <algorithm>
#include <fstream>
#include <time.h>
#include "CImg.h"
#include <windows.data.json.h>
#include "json\json.h"
#include <queue>
#include <iomanip>

using namespace cimg_library;
using namespace std;

/*******************ȫ�ֳ���************************************/
#define LS 1                          /* LS �㷨Э��*/

#define DV  2                        /* DV �㷨Э��*/

#define SOCK_VER 2                   /*����ʹ�õ�winsock���汾*/

#define IP "127.0.0.1"             /*���ͻ��˵�IP��ַ*/


// #define LOCAL_HOST_NUM  2           /*����������ţ����費�ɱ���Ψһ*/


/////////////////////////////////////////////////////////////////////// �����޸�
#define BUFF_SIZE 2048              /*�շ���Ϣ�Ļ�������С*/

#define INFINITY_COST 55555         /*�������ڵ㲻��������ô����Ϊ��ֵ*/

#define MAX_ROUTER_NODES  100       /*���·�ɽڵ�*/

#define ROUTER_NODES 5               /*ʵ�ʽ����Ŀ*/

#define DEBUG 0                     /* Ϊ1ʱ��ӡ��������Ϣ*/

#define PROTOCOL_VERSION  "1.0"     /*���İ汾��*/

#define FORWARDING_MESSAGE 0       /*������Ϣ����*/

#define UPDATE_MESSAGE 1           /*������±���*/



/****************ȫ��ͨ�����ݽṹ********************************/

/************
·�����ӽṹ
*************/
struct RouterLink {
	int localHostNum;           /* �����������          */
	int dstHostNum;             /* Ŀ���������          */
	int linkCost;               /* ��·����              */
	bool linkState;             /* Ϊ�����������������  */
	time_t lastModified;        /* ��·״̬�ϴθ���ʱ���*/
};


/********
ת������
*********/
struct ForwardingRouterInfo {
	int targetedHostNum;           /* Ŀ���������     */
	int nextHostNum;               /* ��һ���������   */
	int jumpNum;                   /* ��Ŀ������������ */
};


/**************************ȫ�ֱ���********************************/

extern int ROUTER_PROTOCOL;           /*������õ�·��ѡ��Э��*/

extern int LOCAL_HOST_NUM; 

extern int RECEIVE_PORT;           /*�ͻ��˵Ľ��ն˿�*/

extern int SEND_PORT;              /*�ͻ��˷��Ͷ˿�*/

extern string IPTable[5];

extern int sendPortTable[5];

extern int receivePortTable[5];



/*����: ӳ�������������ţ���ö�Ӧ��IP+�����ա��˿ڡ�
  ��ע������������ͼ��ƺ�֮�󣬻������ֲ��䣬
		��ĳһ�ڵ���ֹ���֮�󣬿���ɾ������*/
//map<int, sockaddr_in>& GetNumToAddrReceiveRouterMappingTable();

extern map<int, sockaddr_in> NumToAddrReceiveRouterMappingTable;


/*����: ӳ�������������ţ���ö�Ӧ��IP+�����͡��˿ڡ�
  ��ע������������ͼ��ƺ�֮�󣬻������ֲ��䣬
		��ĳһ�ڵ���ֹ���֮�󣬿���ɾ������*/
//map<int, sockaddr_in>& GetNumToAddrSendRouterMappingTable();
extern map<int, sockaddr_in> NumToAddrSendRouterMappingTable;



/*���ܣ���ʼ��ȫ��·�ɱ�, �����±����������ţ�ֵ��������֮��ķ��á�
  ��ע��OSPFӵ�иñ�ȫ����Ϣ����RIP��ӵ�иñ���������ڵ����ڵ���Ϣ��
		������ʼ��·����Ϣ¼���������·����Ϣ������������и��¡�
*/
//int& GetRouterLinkTable();
extern int routerLinkTable[MAX_ROUTER_NODES][MAX_ROUTER_NODES];





/*******************ȫ��ͨ�ú���**************************************/
/********************************************
���ܣ�����ʼ·����Ϣ¼��ȫ��·����Ϣ�����
Author: ���Ⱥ�
Date created: 18/11/19
Date finished:
**********************************************/

void InitRouterLinkTable();
void PrintRouterLinkTable();

/*********************************************
���ܣ���������ź�IP��ַ��Ϣ¼�뵽ȫ��ӳ�����
Author: ���Ⱥ�
Date created: 18/11/19
Date finished:
**********************************************/

void InitRouterMappingTable();
void PrintRouterMappingTable();
// TODO
/**************
���ܣ�������·����ͼ
���룺
�����
������
Author: qyh
Date created:
***************/
void DrawRouterTable();

#endif // ! UTILS_H

