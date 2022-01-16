#pragma once
#include "utils.h"
/*****************************************************

Author: qyh �����
 
Date created : 18/11/18

Date finished:

Description: ʹ��LS�㷨�ķ�ʽ������·��ѡ��

****************************************************/

class RouterWithLS {

private:
	RouterWithLS();                                 // ctor hidden
	RouterWithLS(RouterWithLS const&);            // copy ctor hidden
	RouterWithLS& operator=(RouterWithLS const&); // assign op. hidden
	~RouterWithLS() = default;                                // dtor hidden
	
													//�м����
	int local;									   //����·�ɱ��
	//vector<int> dist;                             //��ǰ·������
	int dist[10] = { 0 };
	//vector<int> path;                            //·����¼
	int path[10] = { 0 };
	vector<bool> visited;						//��ǩ�Ƿ����

	vector<vector<RouterLink>> RouterTable;           /*ȫ��·�ɱ���ά�������洢��ǰ�ڵ㵽�ھӵ�·����Ϣ*/
	vector<ForwardingRouterInfo> ForwardingTable;     /*ת����ͨ��·�ɱ��������յ�ĳһ�����Ϣ�󣬾�����һ��*/

public:
	static RouterWithLS& Instance() {
		static RouterWithLS theRouterWithOSPF;
		return theRouterWithOSPF;
	}

	/*��ȡ·�ɱ�*/
	vector<vector<RouterLink>> GetLSRouterTable() {
		return RouterTable;
	}

	/*����ID��Ȼ�����ʱ���*/
	void UpdateTimestamp(int routerID);

	/*���·�ɱ��г���180��û��ͨ�ŵ�·�ɣ���routeLink�е�linkState��Ϊfalse���޷�����*/
	int CheckRouterTable();

	/*����ID�����·״̬�������Ա�ʾ�ܹ�����*/
	bool CheckLinkState(int RouterID);

	/*����·�ɱ�ı仯������ת����*/
	bool UpdateForwadingTable();

	/*������������·�ɸ�����Ϣ*/
	bool ReceiveUpdatingRouterInfo(vector<vector<RouterLink>> fromOtherRouterTable);

	/*���͸��������·�ɵĸ�����Ϣ*/
	vector<vector<RouterLink>> SendUpdatingRouterInfo();

	/*�޸��뵱ǰ�ڽӽ�����·������Ϣ*/
	bool ModifyNeighborRouterInfo(int neighborRouterID, int cost);

	/*������Ϣ��ȷ����һ����ַID*/
	int DecideForwardingAddress(int routerID);


	/*����·�ɱ�*/
	void DrawRouterTable();

	/*��ӡת����*/
	void PrintForwardingTable();

};

