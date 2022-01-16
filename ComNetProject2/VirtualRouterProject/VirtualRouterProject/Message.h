#pragma once
#include "utils.h"

/*****************************************************

Author: qyh

Date created : 18/11/18

Date finished:

Description: 使用结构体封装了通信报文中的字段，集成了编码和解码
			 的功能，便于网络传输和接收数据。

****************************************************/



/************************************************************************************
协议报文设计: 变长json结构体

转发消息报文json部分 （多次转发传播）
{
	Version:  ....  协议版本号
	Router_Protocl:.ospf 和 RIP
	Message_Type:...为1 表示转发的消息报文 int
	Src_Addr: ....  消息报文源主机地址 string
	Src_Port: ....  消息报文源主机发送端口 int
	Src_Num: .....  消息报文源主机ID int
	Dst_Addr: ....  消息报文目的主机地址
	Dst_Port: ....  消息报文目的主机接收端口
	Dst_Num:  ....  消息报文目的主机ID
	Next_Addr: .... 消息报文下一跳主机地址
	Next_Port: .... 消息报文下一跳主机接收端口
	Next_Num: ....  消息报文下一条主机ID
	From_Addr: .... 当前消息报文的发送来源主机地址
	From_Port: .... 当前消息报文的发送来源主机发送端口
	From_Num: ....  当前消息报文的发送来源主机ID
	Cost:    .....  每经过一个结点，需要更新链路总代价  int
	Msg: .......... 消息报文携带的报文消息   string 
	Stamptime: .... 消息报文最初发送时间戳   int
	// src dst 字段一经确定，不再变更
	// next  from 随着转发过程进行修改
	// 对于最初的发送主机，需要特别指定报文消息，发送时间戳，链路代价
	// 对于转发主机，需要更新链路代价。
}

LS路由信息交换报文（一次传播）
{
	Version:  ....  协议版本号
	Src_Addr: ....  消息报文源主机地址
	Src_Port: ....  消息报文源主机发送端口
	Src_Num: .....  消息报文源主机ID
	Dst_Addr: ....  消息报文目的主机地址
	Dst_Port: ....  消息报文目的主机接收端口
	Cost: ........  链路代价
	Message_Type:  ....... 为0 表示更新报文 
	Router_Protocol: ...... 为 OSPF
	Router_Num: 路由信息涉及的的结点，二维向量的长度（宽度）
	Router_Info:[
			[
				[Src: Src_Num:  Dst: Dst_Num:  Cost:  Timestamp: ]   int int int time_t // 二维向量
			],
			[
				[Src: Src_Num:  Dst: Dst_Num: Cost: Timestamp: ]
			]
		 ] ....路由信息
}


DV路由信息交换报文（一次传播）
{
	Version:  ....  协议版本号
	Src_Addr: ....  消息报文源主机地址
	Src_Port: ....  消息报文源主机发送端口
	Src_Num: .....  消息报文源主机ID
	Dst_Addr: ....  消息报文目的主机地址
	Dst_Port: ....  消息报文目的主机接收端口
	Cost: ........  链路代价
	Message_Type:  .......  为0表示更新报文
	Router_Protocol: ...... 为 rip
	Router_Num: 路由信息涉及的的结点，一维向量的长度（宽度）
	Router_Info:[
			[Src:  Src_Num: Dst: Dst_Num: Cost: Timestamp: ],             // 一维向量
			[Src: Src_Num: Dst: Dst_Num: Cost: Timestamp: ].
		 ] ....路由信息
}

/*将打包和解包，以及生成报文集成到一个类中*/
class Message
{
private:
	Message() {
	
	}                             // ctor hidden
	//Message(Message const&);               // copy ctor hidden
	//Message& operator=(Message const&);    // assign op. hidden
	~Message() = default;                            // dtor hidden
	

public:
	// 单例模式
	static Message& Instance() {
		static Message theMessage;  /*使用方法 Message::Instance()得到对象*/
		return theMessage;
	}

	/*创建一则消息报文*/
	char* CreateMessage(string Msg,int srcRouterID, int dstRouterID, int nextRouterID);

	/*创建一则转发的消息报文*/
	char* CreateForwardingMessage(Json::Value* jsonMsg, int fromRouterID, int nextRouterID, int addCost);

	/*为ospf协议创建路由信息交换报文*/
	string CreateLSMessage(vector<vector<RouterLink>>* routerTable, int dstRouterID);

	/*为RIP协议创建路由信息交换报文*/
	string CreateDVMessage(vector<ForwardingRouterInfo>* routerTable, int dstRouterID);

	/*将json结构数据转换为char字符串指针，用于网络传输*/
	string EncodeMessage(Json::Value* msg, char* res);

	/*将字符串指针转为json数据用于解析报文*/
	Json::Value DecodeMessage(char* msg,Json::Value& body);

	/*打印报文*/ 
	void Print(Json::Value msg);


};
