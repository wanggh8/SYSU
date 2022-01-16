#pragma once
#include <vector>
#include "GameObject.h"
#include "GameBoard.h"
#include "Food.h"
#include "SnakePart.h"
#include "lodepng.h"

using namespace std;
class Snake
{
private:
	float rotateValue;
	int direction;
	std::vector <SnakePart> snakeParts;
	int length;
	bool growing;
	short turn;
	const float PI = 3.141592653589793f;
	GLuint tex;

public:
	Snake(glm::mat4 *startingWorldMatrix) { 
		this->growing = false;
		this->direction = 0;
		SnakePart snakePart(startingWorldMatrix, true, 0);
		this->snakeParts.push_back(snakePart);
		this->length = 1;
	};
	void move(GameBoard *gameBoard, Food *food);
	void rotate(float deg, float direction);
	void relativeRotate(glm::mat4 *relativeM, float degree);
	void grow();
	void draw(glm::mat4 *V);
	void init(char *pathImage, char *pathObj);
	void setInitPosition(float x, float y, float z);
	void turnLeft();
	void turnRight();
	void dontTurn();
	short getTurn();
	std::vector <SnakePart> getSnakeParts();
	~Snake();
};