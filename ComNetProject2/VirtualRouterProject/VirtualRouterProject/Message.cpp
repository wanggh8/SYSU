#pragma once
#include "Message.h"
#include "utils.h"

/*******************************************************
函数功能： 源主机生成消息报文
函数输入： Msg 消息内容  DstRouterID 目的地主机ID
		   nextRouterID 下一跳主机ID
函数输出： 协议字符串，用于网络传输
全局变量：NumToAddrSendRouterMappingTable
		  NumToAddrReceiveRouterMappingTable
Author: qyh
Date created: 18/11/22
Date finished:
*******************************************************/
char * Message::CreateMessage(string Msg, int srcRouterID, int dstRouterID, int nextRouterID)
{
	//map<int, sockaddr_in> NumToAddrSendRouterMappingTable = GetNumToAddrSendRouterMappingTable();
	//map<int, sockaddr_in> NumToAddrReceiveRouterMappingTable = GetNumToAddrReceiveRouterMappingTable();

	Json::Value JsonMsg;
	JsonMsg["Version"] = PROTOCOL_VERSION;
	JsonMsg["Message_Type"] = FORWARDING_MESSAGE;
	JsonMsg["Router_Protocol"] = ROUTER_PROTOCOL;
	JsonMsg["Stamptime"] = (int)time(0);
	JsonMsg["Msg"] = Msg;

	JsonMsg["Src_Num"] = srcRouterID;
	JsonMsg["Src_Addr"] = string(inet_ntoa(NumToAddrSendRouterMappingTable[srcRouterID].sin_addr));
	JsonMsg["Src_Port"] = ntohs(NumToAddrSendRouterMappingTable[srcRouterID].sin_port);

	JsonMsg["Dst_Num"] = dstRouterID;
	JsonMsg["Dst_Addr"] = string(inet_ntoa(NumToAddrReceiveRouterMappingTable[dstRouterID].sin_addr));
	JsonMsg["Dst_Port"] = ntohs(NumToAddrReceiveRouterMappingTable[dstRouterID].sin_port);

	JsonMsg["From_Num"] = srcRouterID;
	JsonMsg["From_Addr"] = string(inet_ntoa(NumToAddrSendRouterMappingTable[srcRouterID].sin_addr));
	JsonMsg["From_Port"] = ntohs(NumToAddrSendRouterMappingTable[srcRouterID].sin_port);

	JsonMsg["Next_Num"] = nextRouterID;
	JsonMsg["Next_Addr"] = string(inet_ntoa(NumToAddrReceiveRouterMappingTable[nextRouterID].sin_addr));
	JsonMsg["Next_Port"] = ntohs(NumToAddrReceiveRouterMappingTable[nextRouterID].sin_port);

	JsonMsg["Cost"] = routerLinkTable[srcRouterID][nextRouterID];

	Json::FastWriter styled_writer;

	string s = styled_writer.write(JsonMsg);

	char res[BUFF_SIZE] = { 0 };

	strcpy_s(res, s.c_str());

	return res;
}


/*******************************************************
函数功能： 收到json报文，进行修改，生成消息转发报文
函数输入： jsonMsg 消息报文json  fromRouterID 转发主机ID
		   nextRouterID  生成下一跳主机路由ID
函数输出： 协议字符串，用于网络传输
全局变量：NumToAddrSendRouterMappingTable
		  NumToAddrReceiveRouterMappingTable
Author: qyh
Date created: 18/11/22
Date finished:
*******************************************************/
char * Message::CreateForwardingMessage(Json::Value * jsonMsg, int fromRouterID, int nextRouterID, int addCost)
{
	//map<int, sockaddr_in> NumToAddrSendRouterMappingTable = GetNumToAddrSendRouterMappingTable();
	//map<int, sockaddr_in> NumToAddrReceiveRouterMappingTable = GetNumToAddrReceiveRouterMappingTable();

	(*jsonMsg)["From_Num"] = fromRouterID;
	(*jsonMsg)["From_Addr"] = string(inet_ntoa(NumToAddrSendRouterMappingTable[fromRouterID].sin_addr));
	(*jsonMsg)["From_Port"] = ntohs(NumToAddrSendRouterMappingTable[fromRouterID].sin_port);

	(*jsonMsg)["Next_Num"] = nextRouterID;
	(*jsonMsg)["Next_Addr"] = string(inet_ntoa(NumToAddrReceiveRouterMappingTable[fromRouterID].sin_addr));
	(*jsonMsg)["Next_Port"] = ntohs(NumToAddrReceiveRouterMappingTable[fromRouterID].sin_port);

	(*jsonMsg)["Cost"] = (*jsonMsg)["Cost"].asInt() + addCost;

	Json::FastWriter styled_writer;

	string s = styled_writer.write(*jsonMsg);

	char res[BUFF_SIZE] = { 0 };

	strcpy_s(res, s.c_str());

	return res;
}



/************************************************************
函数功能： 接收OSPF协议下的本机路由信息，准备报文发送给其他结点
函数输入： routerTable 路由表
		   dstRouterID 目的主机路由
函数输出： 协议字符串，用于网络传输
全局变量：NumToAddrSendRouterMappingTable
		  NumToAddrReceiveRouterMappingTable
Author: qyh
Date created: 18/11/22
Date finished:
*******************************************************/
string Message::CreateLSMessage(vector<vector<RouterLink>>* routerTable, int dstRouterID)
{

	Json::Value JsonMsg;
	JsonMsg["Version"] = PROTOCOL_VERSION;
	JsonMsg["Message_Type"] = UPDATE_MESSAGE;
	JsonMsg["Stamptime"] = (int)time(0);
	JsonMsg["Router_Protocol"] = LS;

	JsonMsg["Src_Num"] = LOCAL_HOST_NUM;
	JsonMsg["Src_Addr"] = string(inet_ntoa(NumToAddrSendRouterMappingTable[LOCAL_HOST_NUM].sin_addr));
	JsonMsg["Src_Port"] = ntohs(NumToAddrSendRouterMappingTable[LOCAL_HOST_NUM].sin_port);

	JsonMsg["Dst_Num"] = dstRouterID;
	JsonMsg["Dst_Addr"] = string(inet_ntoa(NumToAddrReceiveRouterMappingTable[dstRouterID].sin_addr));
	JsonMsg["Dst_Port"] = ntohs(NumToAddrReceiveRouterMappingTable[dstRouterID].sin_port);

	//JsonMsg["Cost"] = routerLinkTable[LOCAL_HOST_NUM][dstRouterID];

	JsonMsg["Router_Num"] = (int)(*routerTable).size();
	for (int i = 0; i < (*routerTable)[0].size(); i++) {
		for (int j = 0; j < (*routerTable)[i].size(); j++) {
			JsonMsg["Router_Info"][i][j]["Src_Num"] = (*routerTable)[i][j].localHostNum;
			JsonMsg["Router_Info"][i][j]["Cost"] = (*routerTable)[i][j].linkCost;
			JsonMsg["Router_Info"][i][j]["Dst_Num"] = (*routerTable)[i][j].dstHostNum;
			JsonMsg["Router_Info"][i][j]["Timestamp"] = (int)(*routerTable)[i][j].lastModified;
		}
	}

	Json::FastWriter styled_writer;

	string s = styled_writer.write(JsonMsg);
	return s;
}

/************************************************************
函数功能： 接收DV协议下的本机路由信息，准备报文发送给其他结点
函数输入： routerTable 路由表
		   dstRouterID 目的主机路由
函数输出： 协议字符串，用于网络传输
全局变量：NumToAddrSendRouterMappingTable
		  NumToAddrReceiveRouterMappingTable
Author: qyh
Date created: 18/11/22
Date finished:
*******************************************************/
string Message::CreateDVMessage(vector<ForwardingRouterInfo>* routerTable, int dstRouterID)
{

	Json::Value JsonMsg;
	JsonMsg["Version"] = PROTOCOL_VERSION;
	JsonMsg["Message_Type"] = UPDATE_MESSAGE;
	JsonMsg["Router_Protocol"] = DV;
	JsonMsg["Timestamp"] = (int)time(0);

	JsonMsg["Src_Num"] = LOCAL_HOST_NUM;
	JsonMsg["Src_Addr"] = string(inet_ntoa(NumToAddrSendRouterMappingTable[LOCAL_HOST_NUM].sin_addr));
	JsonMsg["Src_Port"] = ntohs(NumToAddrSendRouterMappingTable[LOCAL_HOST_NUM].sin_port);

	JsonMsg["Dst_Num"] = dstRouterID;
	JsonMsg["Dst_Addr"] = string(inet_ntoa(NumToAddrReceiveRouterMappingTable[dstRouterID].sin_addr));
	JsonMsg["Dst_Port"] = ntohs(NumToAddrReceiveRouterMappingTable[dstRouterID].sin_port);

	JsonMsg["Cost"] = routerLinkTable[LOCAL_HOST_NUM][dstRouterID];

	JsonMsg["Router_Num"] = (int)(*routerTable).size();

	for (int i = 0; i < (*routerTable).size(); i++) {
		JsonMsg["Router_Info"][i]["Next_Num"] = (*routerTable)[i].nextHostNum;
		JsonMsg["Router_Info"][i]["Dst_Num"] = (*routerTable)[i].targetedHostNum;
	}

	Json::FastWriter styled_writer;

	string s = styled_writer.write(JsonMsg);

	return s;
}


/*将json对象转为字符串*/
string Message::EncodeMessage(Json::Value * msg, char * res)
{
	Json::FastWriter styled_writer;

	string s = styled_writer.write(*msg);

	return s;
}


/*将字符串转为json对象*/
Json::Value  Message::DecodeMessage(char * msg, Json::Value& body)
{
	Json::Reader reader;

	string str(msg);
	reader.parse(str, body);
	return body;
}

/*将报文以格式化的json字符串输出*/
void Message::Print(Json::Value msg)
{
	Json::StyledWriter styled_writer;
	std::cout << styled_writer.write(msg) << std::endl;
}
