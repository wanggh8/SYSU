#pragma once
#include <iostream>
#include <string.h>
using namespace std;
#include "ControlUnit.h"
#include "json/json.h"
#pragma warning(disable : 4996)
int main() {
	ControlUnit::Instance().Run();
	system("pause");
	return 0;
}
