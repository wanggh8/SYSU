#pragma once
/*****************************************************

Author: qiuyihao

Date created: 18/11/18

Date finished: 

Description：存放一些全局常量、变量、通用的函数

*******************************************************/
#ifndef  UTILS_H
#define UTILS_H

#include <stdio.h>
#include <winsock.h>  // 套接字所需头文件
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

/*******************全局常量************************************/
#define LS 1                          /* LS 算法协议*/

#define DV  2                        /* DV 算法协议*/

#define SOCK_VER 2                   /*程序使用的winsock主版本*/

#define IP "127.0.0.1"             /*本客户端的IP地址*/


// #define LOCAL_HOST_NUM  2           /*本地主机序号，假设不可变且唯一*/


/////////////////////////////////////////////////////////////////////// 无需修改
#define BUFF_SIZE 2048              /*收发消息的缓冲区大小*/

#define INFINITY_COST 55555         /*若两个节点不相连，那么设置为该值*/

#define MAX_ROUTER_NODES  100       /*最大路由节点*/

#define ROUTER_NODES 5               /*实际结点数目*/

#define DEBUG 0                     /* 为1时打印出进程消息*/

#define PROTOCOL_VERSION  "1.0"     /*报文版本号*/

#define FORWARDING_MESSAGE 0       /*代表消息报文*/

#define UPDATE_MESSAGE 1           /*代表更新报文*/



/****************全局通用数据结构********************************/

/************
路由连接结构
*************/
struct RouterLink {
	int localHostNum;           /* 本地主机序号          */
	int dstHostNum;             /* 目的主机序号          */
	int linkCost;               /* 链路代价              */
	bool linkState;             /* 为真代表网络连接正常  */
	time_t lastModified;        /* 链路状态上次更新时间戳*/
};


/********
转发表项
*********/
struct ForwardingRouterInfo {
	int targetedHostNum;           /* 目的主机序号     */
	int nextHostNum;               /* 下一跳主机序号   */
	int jumpNum;                   /* 到目的子网的跳数 */
};


/**************************全局变量********************************/

extern int ROUTER_PROTOCOL;           /*定义采用的路由选择协议*/

extern int LOCAL_HOST_NUM; 

extern int RECEIVE_PORT;           /*客户端的接收端口*/

extern int SEND_PORT;              /*客户端发送端口*/

extern string IPTable[5];

extern int sendPortTable[5];

extern int receivePortTable[5];



/*功能: 映射表，根据网络序号，获得对应的IP+【接收】端口。
  备注：当网络拓扑图设计好之后，基本保持不变，
		当某一节点出现故障之后，可以删除表项*/
//map<int, sockaddr_in>& GetNumToAddrReceiveRouterMappingTable();

extern map<int, sockaddr_in> NumToAddrReceiveRouterMappingTable;


/*功能: 映射表，根据网络序号，获得对应的IP+【发送】端口。
  备注：当网络拓扑图设计好之后，基本保持不变，
		当某一节点出现故障之后，可以删除表项*/
//map<int, sockaddr_in>& GetNumToAddrSendRouterMappingTable();
extern map<int, sockaddr_in> NumToAddrSendRouterMappingTable;



/*功能：初始的全局路由表, 数组下标代表主机序号，值代表主机之间的费用。
  备注：OSPF拥有该表全部信息，而RIP则拥有该表中与自身节点相邻的信息。
		仅供初始的路由消息录入对象，随后的路由消息无需再这个表中更新。
*/
//int& GetRouterLinkTable();
extern int routerLinkTable[MAX_ROUTER_NODES][MAX_ROUTER_NODES];





/*******************全局通用函数**************************************/
/********************************************
功能：将初始路由信息录入全局路由信息表格中
Author: 邱奕浩
Date created: 18/11/19
Date finished:
**********************************************/

void InitRouterLinkTable();
void PrintRouterLinkTable();

/*********************************************
功能：将主机序号和IP地址信息录入到全局映射表中
Author: 邱奕浩
Date created: 18/11/19
Date finished:
**********************************************/

void InitRouterMappingTable();
void PrintRouterMappingTable();
// TODO
/**************
功能：画出网路拓扑图
输入：
输出：
依赖：
Author: qyh
Date created:
***************/
void DrawRouterTable();

#endif // ! UTILS_H

