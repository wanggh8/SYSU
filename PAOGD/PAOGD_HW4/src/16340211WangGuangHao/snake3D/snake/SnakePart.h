#pragma once
#include <iostream>
#include <vector>
#include "GameObject.h"
#include "GameBoard.h"
#include "Food.h"
using namespace std;
class SnakePart : public GameObject
{
private:
	float rotateValue;
	int direction;
	bool head;
public:
	SnakePart(glm::mat4 *startingWorldMatrix, bool head, int direction) : GameObject(startingWorldMatrix) {
		this->direction = direction;
		this->head = head;
	};
	SnakePart(glm::mat4 *startingWorldMatrix, bool head, int direction, GLuint *tex) : GameObject(startingWorldMatrix) {
		this->direction = direction;
		this->head = head;
		this->tex = *tex;
		this->model.init("cube.obj", this->M);
	};
	void move(GameBoard *gameBoard, std::vector <SnakePart> &snakeParts);
	void move(Coordinate * coordinate, glm::mat4 * relativeM, float degree);
	void rotate(float deg, float direction);
	void relativeRotate(glm::mat4 *relativeM, float degree);
	bool isHead();
	int getDirection();
	~SnakePart();
};