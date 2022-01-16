#pragma once
#include "GameObject.h"
#include "GameBoard.h"
#include <cstdlib>
#include <cstdio>
#include <ctime>
#include <vector>

class Food : public GameObject
{
public:
	Food() : GameObject() {}

	void relativeRotate(glm::mat4 *relativeM, float degree);

	void respawnInNewPlace(int maxRandomNumber, GameBoard *gameBoard, std::vector<Coordinate> &coordinates);
	~Food();
};

