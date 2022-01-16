#pragma once
#include "utils.h"

/*****************************************************

Author: qyh ���³�

Date created : 18/11/18

Date finished:

DescDVtion: ʹ����DVЭ��&DV��ʵ��·��ѡ��

****************************************************/

class RouterWithDV {
private:
	RouterWithDV();                                // ctor hidden
	RouterWithDV(RouterWithDV const&);            // copy ctor hidden
	~RouterWithDV() = default;                               // dtor hidden
	int LOCAL;
	vector<RouterLink> RouterTable;                 /*�ֲ�·�ɱ�һά�������洢��ǰ�ڵ㵽�ھӵ�·����Ϣ*/
	vector<ForwardingRouterInfo> ForwardingTable;   /*ת����ͨ��·�ɱ��������յ�ĳһ�����Ϣ�󣬾�����һ��*/

public:
	static RouterWithDV& Instance() {
		static RouterWithDV theRouterWithDV;
		return theRouterWithDV;
	}
	vector<RouterLink> GetDVRouterTable() {
		return RouterTable;
	}
	vector<ForwardingRouterInfo> GetDVForwardingTable() {
		return ForwardingTable;
	}
	/*���·�ɱ��г���180��û��ͨ�ŵ�·�ɣ���routeLink�е�linkState��Ϊfalse���޷�����*/
	void CheckRouterTable();

	/*����ID�����·״̬�������Ա�ʾ�ܹ�����*/
	bool CheckLinkState(int RouterID);

	/*·��ѡ������仯�����ת����*/
	bool UpdateForwardingTable(vector<ForwardingRouterInfo>& fromOtherRouterTable, const int fromNum);
	bool UpdateForwardingTable(const int nextNum, const int st, const int cost);

	/*������������·�ɸ�����Ϣ*/
	bool ReceiveUpdatingRouterInfo(vector<ForwardingRouterInfo>& fromOtherRouterTable, const int fromNum);

	/*���͸��������·�ɵĸ�����Ϣ*/
	vector<ForwardingRouterInfo>* SendUpdatingRouterInfo(vector<ForwardingRouterInfo>* toOtherRouterTable);

	/*�޸��뵱ǰ�ڽӽ�����·������Ϣ*/
	bool ModifyNeighborRouterInfo(int neighborRouterID, int cost);

	/*������Ϣ��ȷ����һ����ַID*/
	int DecideForwardingAddress(int routerID);

	/*����·�ɱ�*/
	void DrawRouterTable();

	/*��ӡת����*/
	void PrintForwardingTable();

};

