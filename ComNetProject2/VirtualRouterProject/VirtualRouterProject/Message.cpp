#pragma once
#include "Message.h"
#include "utils.h"

/*******************************************************
�������ܣ� Դ����������Ϣ����
�������룺 Msg ��Ϣ����  DstRouterID Ŀ�ĵ�����ID
		   nextRouterID ��һ������ID
��������� Э���ַ������������紫��
ȫ�ֱ�����NumToAddrSendRouterMappingTable
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
�������ܣ� �յ�json���ģ������޸ģ�������Ϣת������
�������룺 jsonMsg ��Ϣ����json  fromRouterID ת������ID
		   nextRouterID  ������һ������·��ID
��������� Э���ַ������������紫��
ȫ�ֱ�����NumToAddrSendRouterMappingTable
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
�������ܣ� ����OSPFЭ���µı���·����Ϣ��׼�����ķ��͸��������
�������룺 routerTable ·�ɱ�
		   dstRouterID Ŀ������·��
��������� Э���ַ������������紫��
ȫ�ֱ�����NumToAddrSendRouterMappingTable
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
�������ܣ� ����DVЭ���µı���·����Ϣ��׼�����ķ��͸��������
�������룺 routerTable ·�ɱ�
		   dstRouterID Ŀ������·��
��������� Э���ַ������������紫��
ȫ�ֱ�����NumToAddrSendRouterMappingTable
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


/*��json����תΪ�ַ���*/
string Message::EncodeMessage(Json::Value * msg, char * res)
{
	Json::FastWriter styled_writer;

	string s = styled_writer.write(*msg);

	return s;
}


/*���ַ���תΪjson����*/
Json::Value  Message::DecodeMessage(char * msg, Json::Value& body)
{
	Json::Reader reader;

	string str(msg);
	reader.parse(str, body);
	return body;
}

/*�������Ը�ʽ����json�ַ������*/
void Message::Print(Json::Value msg)
{
	Json::StyledWriter styled_writer;
	std::cout << styled_writer.write(msg) << std::endl;
}
